plugins {
    application
    kotlin("jvm").version("1.3.61")
    kotlin("kapt").version("1.3.61")
    kotlin("plugin.allopen").version("1.3.61")

    id("com.github.johnrengelman.shadow").version("5.2.0")
    id("org.jlleitschuh.gradle.ktlint").version("9.2.1")
    id("com.github.ben-manes.versions").version("0.27.0")
}

repositories {
    mavenLocal()
    jcenter()
}

configurations {
    all {
        resolutionStrategy {
            force(
                "com.pinterest:ktlint:0.36.0",
                "com.pinterest.ktlint:ktlint-reporter-checkstyle:0.36.0"
            )
        }
    }
}

dependencies {
    val micronaut = "1.3.1"
    val kotlin = "1.3.61"
    val logback = "1.2.3"
    val jackson = "2.10.2"
    val kotlintest = "1.1.5"
    val mockk = "1.9.3"
    val kotlintestRunner = "3.4.0"

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlin")

    implementation(platform("io.micronaut:micronaut-bom:$micronaut"))
    implementation("io.micronaut:micronaut-http-client")
    implementation("io.micronaut:micronaut-http-server-netty")
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut:micronaut-management")

    annotationProcessor("io.micronaut:micronaut-security")
    implementation("io.micronaut:micronaut-security")

    kapt(platform("io.micronaut:micronaut-bom:$micronaut"))
    kapt("io.micronaut:micronaut-inject-java")
    kapt("io.micronaut:micronaut-validation")
    kaptTest(platform("io.micronaut:micronaut-bom:$micronaut"))
    kaptTest("io.micronaut:micronaut-inject-java")

    runtimeOnly("ch.qos.logback:logback-classic:$logback")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin:$jackson")

    testImplementation(platform("io.micronaut:micronaut-bom:$micronaut"))
    testImplementation("io.micronaut.test:micronaut-test-kotlintest:$kotlintest")
    testImplementation("io.mockk:mockk:$mockk")
    testImplementation("io.kotlintest:kotlintest-runner-junit5:$kotlintestRunner")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.javaParameters = true
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

application {
    mainClassName = "com.albertattard.example.micronaut.Application"
}

allOpen {
    annotation("io.micronaut.aop.Around")
}

tasks {
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        mergeServiceFiles()
    }
}

defaultTasks("clean", "ktlintFormat", "dependencyUpdates", "test")
