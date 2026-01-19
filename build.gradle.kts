plugins {
    id("java")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.intellijPlatform)
}

group = "io.dontsayboj.mako"
version = libs.versions.mako.get()

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    implementation(libs.imageio.webp)
    implementation(libs.imageio.core)
    implementation(libs.thumbnailator) // https://github.com/coobird/thumbnailator/tags
    implementation(libs.imgscalr) // https://mvnrepository.com/artifact/org.imgscalr/imgscalr-lib

    intellijPlatform {
        intellijIdeaCommunity(libs.versions.intellijIdea)
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
