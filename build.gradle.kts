plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.2.0"
    id("org.jetbrains.intellij.platform") version "2.1.0"
}

group = "io.dontsayboj.mako"
version = "1.0.2"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    implementation("com.twelvemonkeys.imageio:imageio-webp:3.12.0")
    implementation("com.twelvemonkeys.imageio:imageio-core:3.12.0")

    intellijPlatform {
        intellijIdeaCommunity("2024.1.7")
        instrumentationTools()
    }
}

intellijPlatform {
    pluginConfiguration {
        name = "Mako Android Drawable Importer"
        version = project.version.toString()
        description = "Import and convert drawable resources for Android projects"
        vendor {
            name = "Delacrix Morgan"
            email = "delacrixmorgan@gmail.com"
            url = "https://github.com/delacrixmorgan/mako-intellij"
        }
        ideaVersion {
            sinceBuild = "241"
            untilBuild = null
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}