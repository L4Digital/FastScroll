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

import extension.detektVersion
import extension.getModuleSources
import io.gitlab.arturbosch.detekt.Detekt

plugins {
    id("io.gitlab.arturbosch.detekt")
}

val reportPath = "build/reports/codestyle"

detekt {
    toolVersion = detektVersion
    config = files("$rootDir/config/detekt/detekt.yml")
    autoCorrect = true
    parallel = true

    reports {
        sarif.enabled = false
        txt.enabled = false
    }
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")
}

tasks.withType<Detekt>().configureEach {
    config.from(files("$rootDir/config/detekt/detekt.yml"))
    autoCorrect = true
    jvmTarget = "1.8"

    reports {
        sarif.enabled = false
        txt.enabled = false
    }
}

/**
 * Runs detekt for all Kotlin files.
 */
if (rootProject.tasks.findByName("detektAll") == null) {
    rootProject.tasks.register<Detekt>("detektAll") {
        group = "verification"
        description = "Check Kotlin code style for all files."
        source(project.getModuleSources(), "buildSrc")
        include("**/java/**/*.kt", "**/kotlin/**", "*.kts")
        exclude("build/")

        reports {
            html.destination = file("$reportPath/detekt.html")
            xml.destination = file("$reportPath/detekt.xml")
        }
    }
}
