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

import config.libs
import extension.registerOnce
import io.gitlab.arturbosch.detekt.Detekt

plugins {
    io.gitlab.arturbosch.detekt
}

val configFile = "$rootDir/config/detekt/detekt.yml"
val detektVersion = libs.versions["detekt"]

detekt {
    parallel = true
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")
}

tasks.withType<Detekt>().configureEach {
    config.from(configFile)
    source("src")
    autoCorrect = true
    jvmTarget = "1.8"

    reports {
        sarif.required.set(false)
        txt.required.set(false)
    }
}

/**
 * Runs detekt for all Kotlin build files in projects
 */
rootProject.tasks.registerOnce<Detekt>("detektBuildFiles") {
    description = "Check Kotlin code style for project build files."
    source(rootDir)
    include("**/*.kts")
    exclude("build/", "buildSrc")
}

/**
 * Runs detekt for all Kotlin files in buildSrc
 */
rootProject.tasks.registerOnce<Detekt>("detektBuildSrc") {
    description = "Check Kotlin code style for buildSrc files."
    source("buildSrc")
    include("**/kotlin/**", "*.kts")
    exclude("build/")
}
