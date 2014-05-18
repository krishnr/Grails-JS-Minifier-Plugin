import grails.util.GrailsUtil
import grails.util.Environment

eventCreateWarStart = { name, stagingDir ->
    compileJS stagingDir
}


void compileJS(stagingDir) {

	// don't do this; compiles too much and causes issues
	/*
    System.out.println("\nCompiling JS files ....\n")

	def classLoader = Thread.currentThread().contextClassLoader
	classLoader.addURL(new File(classesDirPath).toURL())

	String className = 'org.nthgen.jscompiler.JSCompilerHelper'
	def helper = Class.forName(className, true, classLoader)

    stagingDir.eachFileRecurse { file ->
        String JSFile = file as String

		helper.compile(JSFile, file)
    }
    */
}
