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

plugins {
    id("com.android.library")
    id("maven-publish")
    signing
}

val sourcesJar by tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(android.sourceSets["main"].java.srcDirs)
}

val javadoc by tasks.register<Javadoc>("javadoc") {
    setSource(android.sourceSets["main"].java.srcDirs)
    classpath += files(android.bootClasspath.joinToString(separator = File.pathSeparator))
}

val javadocJar by tasks.register<Jar>("javadocJar") {
    dependsOn(javadoc)
    archiveClassifier.set("javadoc")
    from(javadoc.destinationDir)
}

tasks.register("generateArchives") { dependsOn(sourcesJar, javadocJar) }

artifacts {
    archives(sourcesJar)
    archives(javadocJar)
}

group = Publish.Info.group
version = Publish.Info.version

afterEvaluate {
    javadoc.classpath += files(android.libraryVariants.map { variant ->
        variant.javaCompileProvider.get().classpath
    })

    publishing {
        publications {
            create<MavenPublication>("release") {
                artifactId = Publish.Info.artifact
                groupId = Publish.Info.group
                version = Publish.Info.version.toString()

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
