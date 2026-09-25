import net.labymod.labygradle.common.extension.LabyModAnnotationProcessorExtension.ReferenceType

dependencies {
    labyProcessor()
    api(project(":api"))
    labyApi("core")
    testImplementation("org.junit.jupiter:junit-jupiter:5.12.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.12.2")
}

tasks.withType<org.gradle.api.tasks.testing.Test>().configureEach {
    useJUnitPlatform()
}

tasks.named<org.gradle.api.tasks.compile.JavaCompile>("compileTestJava") {
    // The LabyMod annotation processor is for addon main sources and rejects the test compile.
    options.compilerArgs.add("-proc:none")
}

labyModAnnotationProcessor {
    referenceType = ReferenceType.DEFAULT
}
