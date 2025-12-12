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
