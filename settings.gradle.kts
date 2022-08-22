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
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

plugins {
    id("com.gradle.enterprise") version "3.10"
    id("com.gradle.common-custom-user-data-gradle-plugin") version "1.6.5"
}

rootProject.name = "fastscroll-android-lib"

include(":example")
include(":fastscroll")

// Set the build file name from the module name
rootProject.children.forEach { it.buildFileName = "${it.name}.gradle.kts" }

/** Configure Gradle Enterprise **/

val isCiBuild get() = System.getenv("CI") == "true"
val isGradleEnterpriseEnabled: Boolean
    get() {
        val gradleEnterpriseEnabled: String? by settings
        return gradleEnterpriseEnabled != "false"
    }

gradleEnterprise {
    buildScan {
        capture.isTaskInputFiles = true
        isUploadInBackground = !isCiBuild
        publishAlwaysIf(isGradleEnterpriseEnabled || isCiBuild)
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"

        obfuscation {
            username { null }
            hostname { null }
            ipAddresses { listOf("0.0.0.0") }
        }
    }
}

buildCache {
    local { isEnabled = true }
    remote(HttpBuildCache::class) { isEnabled = false }
}
