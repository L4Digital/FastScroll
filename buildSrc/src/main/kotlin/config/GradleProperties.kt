package config

import java.io.File
import java.util.Properties

/**
 * Provides access to values read from a properties file
 */
open class GradleProperties(propertyFile: String) {

    private val properties = Properties().apply {
        File(propertyFile).inputStream().use(::load)
    }

    operator fun get(key: String): String? = properties.getProperty(key)
}
