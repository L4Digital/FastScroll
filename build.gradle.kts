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

import extension.startsWithAny

// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", libs.versions.kotlin.get()))
    }
}

plugins {
    script("detekt")
    script("nexus")
}

allprojects {
    repositories {
        google() {
            content {
                includeGroupByRegex("androidx.*")
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
            }
        }

        mavenCentral()
    }
}

subprojects {
    script("detekt")
}

buildScan {
    // capture kapt and gradle properties with each build scan
    properties.filterKeys { it.startsWithAny("android", "kapt", "org.gradle") }
        .toSortedMap().entries
        .forEach { value(it.key, it.value.toString()) }
}

tasks.register<Delete>("clean") { delete(rootProject.buildDir) }
