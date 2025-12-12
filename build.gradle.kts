plugins {
    kotlin("jvm") version "2.2.21"
}

kotlin {
    jvmToolchain(21)
}

sourceSets {
    main {
        java.srcDirs("src")
    }
}

tasks {
    wrapper {
        gradleVersion = "9.2.1"
    }
}

dependencies {
    implementation("tools.aqua:z3-turnkey:4.14.0")
}
