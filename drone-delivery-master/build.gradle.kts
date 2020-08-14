// build.gradle.kts

plugins {
    application
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<Wrapper> {
    gradleVersion = "5.5"
}

application {
    mainClassName = "com.mitchell.drone.DroneDelivery"
}

tasks {
    test {
        useJUnitPlatform()
    }
}

dependencies {
    compile("ch.qos.logback:logback-classic:1.2.3")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.2")
    testRuntime("org.junit.jupiter:junit-jupiter-engine:5.4.2")
}
