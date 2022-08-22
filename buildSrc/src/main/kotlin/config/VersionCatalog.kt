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

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

/**
 * Convenience method for accessing gradle/libs.versions.toml
 */
internal val Project.libs get() = VersionCatalog(this, "libs")

/**
 * Wrapper class for accessing a [org.gradle.api.artifacts.VersionCatalog]
 */
class VersionCatalog(project: Project, name: String) {

    private val catalog = project.rootProject.extensions.getByType<VersionCatalogsExtension>().named(name)

    val versions = Versions(catalog)

    /**
     * Get a library defined by this [VersionCatalog]
     */
    operator fun get(alias: String) = catalog.findLibrary(alias).get()

    /**
     * Get a library version defined by this [VersionCatalog]
     */
    class Versions(private val catalog: org.gradle.api.artifacts.VersionCatalog) {
        operator fun get(alias: String): String = catalog.findVersion(alias).get().requiredVersion
    }
}
