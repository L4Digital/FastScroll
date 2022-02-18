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

group = Publish.Info.group
version = Publish.Info.semantic.version

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                artifactId = Publish.Info.artifact
                groupId = Publish.Info.group
                version = Publish.Info.semantic.version

                artifact(javadocJar)
                artifact(sourcesJar)
                artifact("$buildDir/outputs/aar/$artifactId-release.aar")

                pom {
                    name.set(Publish.Info.name)
                    description.set(Publish.Info.description)
                    url.set(Publish.Info.path)

                    licenses {
                        license {
                            name.set(Publish.License.name)
                            url.set(Publish.License.url)
                        }
                    }

                    developers {
                        developer {
                            id.set(Publish.Developer.id)
                            name.set(Publish.Developer.name)
                            email.set(Publish.Developer.email)
                        }
                    }

                    scm {
                        connection.set("scm:git:${Publish.Info.path}.git")
                        developerConnection.set("scm:git:ssh://${Publish.Info.path}.git")
                        url.set("https://${Publish.Info.path}/tree/master")
                    }
                }
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(
        Publish.config["signing.keyId"],
        Publish.config["signing.key"],
        Publish.config["signing.password"]
    )
    sign(publishing.publications)
}
