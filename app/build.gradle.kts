import com.adarshr.gradle.testlogger.theme.ThemeType
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("java")
    //setup Gradle Versions Plugin
    id("se.patrikerdes.use-latest-versions") version "0.2.18"
    id("com.github.ben-manes.versions") version "0.51.0"
    //setup entry point in our App
    id("application")
    //use the Checkstyle plugin
    checkstyle
    //use JaCoCo plugin
    jacoco
    id("io.freefair.lombok") version "8.11"
    id("com.adarshr.test-logger") version "4.0.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
//setup entry point in our App
application {
    mainClass = "hexlet.code.App"
}
dependencies {
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("io.javalin:javalin:6.4.0")
    implementation("io.javalin:javalin-rendering:6.4.0")
    // https://mvnrepository.com/artifact/io.javalin/javalin-bundle
    implementation("io.javalin:javalin-bundle:6.4.0")
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-simple
    implementation("org.slf4j:slf4j-simple:2.1.0-alpha1")
    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("com.h2database:h2:2.3.232")
    implementation("gg.jte:jte:3.1.16")
    // https://mvnrepository.com/artifact/org.postgresql/postgresql
    implementation("org.postgresql:postgresql:42.7.5")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.2")
    // https://mvnrepository.com/artifact/org.assertj/assertj-core
    testImplementation("org.assertj:assertj-core:3.27.3")
    // https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
    // https://mvnrepository.com/artifact/com.konghq/unirest-java
    implementation("com.konghq:unirest-java:3.14.5")
    // https://mvnrepository.com/artifact/org.jsoup/jsoup
    implementation("org.jsoup:jsoup:1.18.3")

}
testlogger {
    theme = ThemeType.MOCHA_PARALLEL // project level
}
tasks.test {
    useJUnitPlatform()
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
        events = mutableSetOf(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED)
        // showStackTraces = true
        // showCauses = true
        showStandardStreams = true
    }
}
//для интерактивнного ввода в консоль Gradle
tasks.getByName("run", JavaExec::class) {
    standardInput = System.`in`
}
tasks.jacocoTestReport { reports { xml.required.set(true) } }