plugins {
    id("com.gradleup.shadow") version "9.0.0-beta6"

    application
}

repositories {
    mavenCentral()

    maven("https://mvnrepository.com/")
}

dependencies {
    implementation(libs.gson)
    implementation(libs.jnativehook)
    implementation(libs.jave)
    implementation(libs.miglayout)
    implementation(libs.apple)
}

application {
    mainClass = "ca.exp.soundboard.gui.SoundboardFrame"
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = application.mainClass
        }
    }

    compileJava {
        options.compilerArgs.add("--enable-preview")
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
