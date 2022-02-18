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

import java.io.FileInputStream
import java.util.Properties

plugins {
   io.github.`gradle-nexus`.`publish-plugin`
}

val secretPropsFile = file("local.properties")

fun env(name: String) = System.getenv(name) ?: ""

if (secretPropsFile.exists()) {
    // Read local.properties file first if it exists
    val props = Properties()
    FileInputStream(secretPropsFile).use { props.load(it) }
    props.forEach { name, value -> Publish.config[name.toString()] = value.toString() }
} else {
    // Use system environment variables
    with(Publish.config) {
        put("ossrhUsername", env("OSSRH_USERNAME"))
        put("ossrhPassword", env("OSSRH_PASSWORD"))
        put("sonatypeStagingProfileId", env("STAGING_PROFILE_ID"))
        put("signing.key", env("SIGNING_KEY"))
        put("signing.keyId", env("SIGNING_KEY_ID"))
        put("signing.password", env("SIGNING_PASSWORD"))
    }
}

// Set up Sonatype repository
nexusPublishing {
    repositories {
        sonatype {
            stagingProfileId.set(Publish.config["sonatypeStagingProfileId"])
            username.set(Publish.config["ossrhUsername"])
            password.set(Publish.config["ossrhPassword"])
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}
