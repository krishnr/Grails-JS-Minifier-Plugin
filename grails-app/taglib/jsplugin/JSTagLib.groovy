package jsplugin

import com.google.common.collect.ImmutableList
import com.google.javascript.jscomp.SourceFile
import org.apache.commons.io.FileUtils
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.plugins.GrailsPluginManager
import org.nthgen.jscompiler.JSCompilerHelper
import com.google.javascript.jscomp.*
import org.nthgen.jscompiler.*

class JSTagLib {

    static namespace = "JS"

    GrailsApplication grailsApplication
    GrailsPluginManager pluginManager

    def scripts = { attrs, body ->

        //Removing the "src" and the ".js"

		String directory = attrs.remove("dir")
		String src = attrs.remove("file")

		Boolean absolute = attrs.remove('absolute')

        //if the app is run locally
		boolean outputUnminimized = true
        if (runMinimization()) {
            try {

                //if the app is run in a WAR
                int indexOfLast = src.lastIndexOf(".")
                String fileWithoutExtension = indexOfLast >= 0 ? src.substring(0, indexOfLast) : src

                //Setting the js file path
                String sourceFilePath = "/$directory/${fileWithoutExtension}.js"

                def file = grailsApplication.mainContext.getResource(sourceFilePath).getFile()

                String hash = JSCompilerHelper.generateFileHash(file)

                //Setting the min.js file path
                String minimizedFilePath = absolute ? src : resource(dir: directory, file: "${fileWithoutExtension}.${hash}.js", plugin: attrs.remove('plugin'), contextPath: attrs.remove('contextPath'))

				if (JSCompilerHelper.compile(file.absolutePath, file)) {
					out << "<script type=\"text/javascript\" src=\"${minimizedFilePath}\"></script>"
					outputUnminimized = false
				} else {
					log.error "Could not create $minimizedFilePath; falling back to the non-minimized version."
				}
			} catch (Exception e) {
				String finalPath = absolute ? src : resource(dir: directory, file: src, plugin:attrs.remove('plugin'))
				log.error "Could not minimize $finalPath! Falling back to the non-minimized version. Exception: ", e
			}
    	}

		// final fall through
		if (outputUnminimized) {
			// look up the file as a resource
			String finalPath = absolute ? src : resource(dir: directory, file: src, plugin: attrs.remove('plugin'))
			out << "<script type=\"text/javascript\" src=\"$finalPath\"></script>"
		}
    }




    private boolean runMinimization() {
        return grailsApplication.isWarDeployed()
    }

}

