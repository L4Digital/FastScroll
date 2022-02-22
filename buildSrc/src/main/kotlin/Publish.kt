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

import config.Semantic

object Publish {

    object Info {
        const val name = "FastScroll"
        const val description = "A ListView-like FastScroller for Android’s RecyclerView"
        const val path = "github.com/L4Digital/FastScroll"
        const val artifact = "fastscroll"
        const val group = "io.github.l4digital"

        val semantic = object : Semantic {
            override val major = 2
            override val minor = 1
            override val patch = 0
            override fun toString() = version
        }
    }

    object License {
        const val name = "The Apache License, Version 2.0"
        const val url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
    }

    object Developer {
        const val id = "randr0id"
        const val name = "Randy Webster"
        const val email = "randy@randr0id.com"
    }

    val config = mutableMapOf(
        "ossrhUsername" to "",
        "ossrhPassword" to "",
        "sonatypeStagingProfileId" to "",
        "signing.key" to "",
        "signing.keyId" to "",
        "signing.password" to ""
    )
}
