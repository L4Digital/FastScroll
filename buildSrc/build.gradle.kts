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

@file:Suppress("InvalidPackageDeclaration")

// Enable Kotlin DSL for Gradle
plugins {
    `kotlin-dsl`
    `kotlin-dsl-precompiled-script-plugins`
}

repositories {
    maven {
        url = uri("https://plugins.gradle.org/m2/")
        content {
            includeGroup("io.github.gradle-nexus")
        }
    }

    google() {
        content {
            includeGroupByRegex("androidx.*")
            includeGroupByRegex("com\\.android.*")
            includeGroupByRegex("com\\.google.*")
        }
    }

    mavenCentral()
}

// Suppressing errors until these issues are resolved:
// https://issuetracker.google.com/issues/187326581
// https://youtrack.jetbrains.com/issue/KTIJ-19370
@Suppress("MISSING_DEPENDENCY_CLASS", "UNRESOLVED_REFERENCE", "UNRESOLVED_REFERENCE_WRONG_RECEIVER")
dependencies {
    implementation(libs.gradle.plugin.android)
    implementation(libs.gradle.plugin.kotlin)
    implementation(libs.gradle.plugin.dokka)
    implementation(libs.gradle.plugin.detekt)
    implementation(libs.gradle.plugin.publish)
}
