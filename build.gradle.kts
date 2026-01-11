plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.2.0"
    id("org.jetbrains.intellij.platform") version "2.1.0"
}

group = "io.dontsayboj.mako"
version = "1.1.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    implementation("com.twelvemonkeys.imageio:imageio-webp:3.13.0")
    implementation("com.twelvemonkeys.imageio:imageio-core:3.13.0")
    implementation("net.coobird:thumbnailator:0.4.21") // https://github.com/coobird/thumbnailator/tags
    implementation("org.imgscalr:imgscalr-lib:4.2") // https://mvnrepository.com/artifact/org.imgscalr/imgscalr-lib

    intellijPlatform {
        intellijIdeaCommunity("2024.3")
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
            sinceBuild = "243"
            untilBuild = provider { null }
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}
