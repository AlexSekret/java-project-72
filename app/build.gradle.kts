import com.adarshr.gradle.testlogger.theme.ThemeType

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
    id("io.freefair.lombok") version "8.10"
    id ("com.adarshr.test-logger") version "4.0.0"
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
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    annotationProcessor ("info.picocli:picocli-codegen:4.7.6")
}
testlogger {
    theme = ThemeType.MOCHA_PARALLEL // project level
}
tasks.test {
    useJUnitPlatform()
}
//для интерактивнного ввода в консоль Gradle
tasks.getByName("run", JavaExec::class) {
    standardInput = System.`in`
}
tasks.jacocoTestReport { reports { xml.required.set(true) } }