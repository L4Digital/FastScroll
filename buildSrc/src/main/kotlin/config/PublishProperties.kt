package config

/**
 * Provides access to values read from publish.properties
 */
internal object PublishProperties : GradleProperties("publish.properties") {
    val name = "name".value
    val description = "description".value
    val path = "path".value
    val artifact = "artifact".value
    val group = "group".value

    object Developer {
        val id = "dev.id".value
        val name = "dev.name".value
        val email = "dev.email".value
    }

    object License {
        val name = "license.name".value
        val url = "license.url".value
    }

    val semantic = object : SemanticVersion {
        override val major = "version.major".value.toInt()
        override val minor = "version.minor".value.toInt()
        override val patch = "version.patch".value.toInt()
        override val identifier = get("version.identifier")
        override fun toString() = version
    }
}
