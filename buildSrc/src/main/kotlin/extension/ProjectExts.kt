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

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

fun Project.getLibVersion(name: String): String = extensions.getByType<VersionCatalogsExtension>()
    .named("libs")
    .findVersion(name).get()
    .requiredVersion

/**
 * Get array of source path for all modules
 */
fun Project.getModuleSources(vararg excludeModules: String = emptyArray()): Array<String> {
    val sources = mutableListOf<String>()

    rootProject.subprojects.forEach { project ->
        if (!excludeModules.contains(project.name)) {
            val path = project.path.substring(1).replace(":", "/")
            sources.add("$path/src")
        }
    }

    return sources.toTypedArray()
}
