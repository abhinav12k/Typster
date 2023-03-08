import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "com.abhinav12k"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://jitpack.io")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("org.openrndr:openrndr-math:0.3.47")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "TypsterKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Typster"
            packageVersion = "1.0.0"
            copyright = "© 2023 Abhinav. All rights reserved."
            appResourcesRootDir.set(project.layout.projectDirectory.dir("resources"))

            val iconsRoot = project.file("src/jvmMain/resources/drawable")

            macOS {
                iconFile.set(iconsRoot.resolve("launcher_icons/mac.icns"))
            }
            windows {
                iconFile.set(iconsRoot.resolve("launcher_icons/windows.ico"))
            }
            linux {
                iconFile.set(iconsRoot.resolve("launcher_icons/linux.png"))
            }
        }
    }
}
