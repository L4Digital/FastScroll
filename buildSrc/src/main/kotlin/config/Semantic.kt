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

package config

/**
 * Represents a Semantic Version -> https://semver.org/
 */
interface Semantic {
    val major: Int
    val minor: Int
    val patch: Int
    val identifier: String? get() = null
    val version get() = "$major.$minor.$patch${identifier?.let { "-$it" } ?: ""}"
}
