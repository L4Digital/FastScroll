package config

val nexusConfig = mutableMapOf(
    "ossrhUsername" to "",
    "ossrhPassword" to "",
    "sonatypeStagingProfileId" to "",
    "signing.key" to "",
    "signing.keyId" to "",
    "signing.password" to ""
)

/**
 * Provides access to values read from publish.properties
 */
internal object PublishProperties : GradleProperties("publish.properties") {
    val name = getString("name")
    val description = getString("description")
    val path = getString("path")
    val artifact = getString("artifact")
    val group = getString("group")

    object Developer {
        val id = getString("dev.id")
        val name = getString("dev.name")
        val email = getString("dev.email")
    }

    object License {
        val name = getString("license.name")
        val url = getString("license.url")
    }

    val semantic = object : SemanticVersion {
        override val major = getInt("version.major")
        override val minor = getInt("version.minor")
        override val patch = getInt("version.patch")
        override val identifier = get("version.identifier")
        override fun toString() = version
    }

    private fun getInt(key: String) = requireNotNull(get(key)).toInt()
    private fun getString(key: String) = requireNotNull(get(key)).toString()
}
