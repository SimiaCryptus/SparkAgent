import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent

fun properties(key: String) = project.findProperty(key).toString()
group = properties("libraryGroup")
version = properties("libraryVersion")

plugins {
    java
    `java-library`
    `scala`
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    mavenCentral {
        metadataSources {
            mavenPom()
            artifact()
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

val jetty_version = "11.0.15"
val scala_version = "2.13.8"
val scala_mv = "2.13"
val spark_version = "3.3.2"
dependencies {

//    implementation("com.simiacryptus:JoePenai:1.0.8")
    implementation("com.simiacryptus:joe-penai:1.0.7")

//    implementation("com.simiacryptus:skyenet:1.0.4")
//    implementation("com.simiacryptus:SkyeNet:1.0.5")
    implementation("com.simiacryptus.skyenet:util:1.0.5")
    implementation("com.simiacryptus.skyenet:core:1.0.5")
    implementation("com.simiacryptus.skyenet:scala:1.0.5")
    implementation("com.simiacryptus.skyenet:webui:1.0.5")

    implementation("org.scala-lang:scala-library:$scala_version")
    implementation("org.scala-lang:scala-compiler:$scala_version")
    implementation("org.scala-lang:scala-reflect:$scala_version")

    implementation("org.jfree:jfreechart:1.5.4")
    implementation("org.jfree:jfreesvg:3.4.3")


    // Spark
    implementation("org.apache.spark:spark-core_$scala_mv:$spark_version")
    implementation("org.apache.spark:spark-sql_$scala_mv:$spark_version")
    implementation("org.apache.spark:spark-mllib_$scala_mv:$spark_version")

    implementation("org.eclipse.jetty:jetty-server:$jetty_version")
    implementation("org.eclipse.jetty:jetty-servlet:$jetty_version")
    implementation("org.eclipse.jetty:jetty-annotations:$jetty_version")
    implementation("org.eclipse.jetty.websocket:websocket-jetty-server:$jetty_version")
    implementation("org.eclipse.jetty.websocket:websocket-jetty-client:$jetty_version")
    implementation("org.eclipse.jetty.websocket:websocket-servlet:$jetty_version")

    implementation("com.amazonaws:aws-java-sdk:1.12.454")

    implementation("org.apache.httpcomponents:httpclient:4.5.14")

    implementation("org.slf4j:slf4j-api:2.0.5")
    //implementation("org.slf4j:slf4j-simple:2.0.5")
    implementation("ch.qos.logback:logback-classic:1.2.9")
    implementation("ch.qos.logback:logback-core:1.2.9")
    implementation("commons-io:commons-io:2.11.0")

}

tasks {
    test {
        useJUnitPlatform()
        systemProperty("surefire.useManifestOnlyJar", "false")
        testLogging {
            events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
            exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        }
        jvmArgs(
            "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED",
            "--add-opens", "java.base/java.util=ALL-UNNAMED",
            "--add-opens", "java.base/java.lang=ALL-UNNAMED"
        )
    }
    wrapper {
        gradleVersion = properties("gradleVersion")
    }
}

tasks.withType(ShadowJar::class.java).configureEach {
    isZip64 = true

    archiveClassifier.set("")
    mergeServiceFiles()
    append("META-INF/kotlin_module")

    exclude("**/META-INF/*.SF")
    exclude("**/META-INF/*.DSA")
    exclude("**/META-INF/*.RSA")
    exclude("**/META-INF/*.MF")
    exclude("META-INF/versions/9/module-info.class")

    manifest {
        attributes(
            "Main-Class" to "com.simiacryptus.skyenet.SparkAgent"
        )
    }
}

