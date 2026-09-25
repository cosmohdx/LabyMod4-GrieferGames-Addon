plugins {
    id("net.labymod.labygradle")
    id("net.labymod.labygradle.addon")
}

val versions = providers.gradleProperty("net.labymod.minecraft-versions").get().split(";")
val addonVersion = providers.gradleProperty("addon.version").get()

group = "de.cosmohdx.griefergames"
version = addonVersion

labyMod {
    defaultPackageName = "de.cosmohdx.griefergames"

    minecraft {
        registerVersion(versions.toTypedArray()) {
            runs {
                getByName("client") {
                    // When the property is set to true, you can log in with a Minecraft account
                    // devLogin = true
                }
            }
        }
    }

    addonInfo {
        namespace = "griefergames"
        displayName = "GrieferGames"
        author = "GrieferGames (Cosmo & Syntax)"
        description = "This add-on adds many useful features for the GrieferGames.net server."
        minecraftVersion = "*"
        version = addonVersion
    }
}

subprojects {
    plugins.apply("net.labymod.labygradle")
    plugins.apply("net.labymod.labygradle.addon")

    group = rootProject.group
    version = rootProject.version

    extensions.findByType(JavaPluginExtension::class.java)?.apply {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}
