/*
 * Copyright 2022 Randy Webster. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package script

import config.PublishProperties
import config.nexusConfig
import extension.getTask
import extension.releaseSourceSets
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    `android-library`
    `maven-publish`
    org.jetbrains.dokka
    signing
}

val javadoc get() = getTask<DokkaTask>("dokkaJavadoc")

val javadocJar by tasks.register<Jar>("javadocJar") {
    dependsOn(javadoc)
    archiveClassifier.set("javadoc")
    from(javadoc.outputDirectory)
}

@Suppress("SpreadOperator")
val sourcesJar by tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    val sources = android.releaseSourceSets.map { it.java.srcDirs }
    from(*sources.toTypedArray())
}

tasks.withType<DokkaTask>().configureEach { suppressInheritedMembers.set(true) }

tasks.register("generateArchives") { dependsOn(sourcesJar, javadocJar) }

artifacts {
    archives(javadocJar)
    archives(sourcesJar)
}

group = PublishProperties.group
version = PublishProperties.semantic.version

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                artifactId = PublishProperties.artifact
                groupId = PublishProperties.group
                version = PublishProperties.semantic.version

                artifact(javadocJar)
                artifact(sourcesJar)
                artifact("$buildDir/outputs/aar/$artifactId-release.aar")

                pom {
                    name.set(PublishProperties.name)
                    description.set(PublishProperties.description)
                    url.set(PublishProperties.path)

                    licenses {
                        license {
                            name.set(PublishProperties.License.name)
                            url.set(PublishProperties.License.url)
                        }
                    }

                    developers {
                        developer {
                            id.set(PublishProperties.Developer.id)
                            name.set(PublishProperties.Developer.name)
                            email.set(PublishProperties.Developer.email)
                        }
                    }

                    scm {
                        connection.set("scm:git:${PublishProperties.path}.git")
                        developerConnection.set("scm:git:ssh://${PublishProperties.path}.git")
                        url.set("https://${PublishProperties.path}/tree/master")
                    }
                }
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(
        nexusConfig["signing.keyId"],
        nexusConfig["signing.key"],
        nexusConfig["signing.password"]
    )
    sign(publishing.publications)
}
