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

package extension

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.Lint
import com.android.build.gradle.BaseExtension

private const val ANDROID_SDK = 31

/**
 * Sets the default Android SDK and Java versions
 */
fun BaseExtension.defaultSdkVersions(minSdk: Int) {
    compileSdkVersion(ANDROID_SDK)

    defaultConfig {
        this.minSdk = minSdk
        targetSdk = ANDROID_SDK
    }
}

/**
 * Sets default [Lint] options and allows overriding or additional configuration
 *
 * @param options Additional [Lint] to set or override
 */
@Suppress("UnstableApiUsage")
fun CommonExtension<*, *, *, *>.defaultLintOptions(options: Lint.() -> Unit = {}) {
    lint {
        quiet = false
        abortOnError = true
        warningsAsErrors = true
    }

    lint(options)
}
