import net.labymod.labygradle.common.extension.LabyModAnnotationProcessorExtension.ReferenceType

dependencies {
    labyProcessor()
    api(project(":api"))
    labyApi("core")
}

labyModAnnotationProcessor {
    referenceType = ReferenceType.DEFAULT
}
