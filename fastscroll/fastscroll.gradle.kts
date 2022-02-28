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

import extension.defaultLintOptions
import extension.defaultSdkVersions

plugins {
    `android-library`
    `kotlin-android`
}

android {
    defaultSdkVersions(minSdk = 16)
    defaultLintOptions()

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }

    publishing {
        singleVariant("release")
    }
}

dependencies {
    implementation(libs.kotlin.stdlib.jdk8)

    api(libs.androidx.recyclerview)

    implementation(libs.androidx.ktx)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.coordinatorlayout)
    implementation(libs.androidx.swiperefreshlayout)
}

script("publish")
