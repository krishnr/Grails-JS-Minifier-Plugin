package org.nthgen.jscompiler;

import com.google.javascript.jscomp.CompilationLevel
import com.google.javascript.jscomp.CompilerOptions
import com.google.javascript.jscomp.Result
import com.google.javascript.jscomp.SourceFile
import com.google.javascript.jscomp.Compiler

import com.google.common.collect.ImmutableList;
import org.apache.commons.io.FileUtils
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.util.Collections;
import java.util.List;

/**
 * User: mh
 * Date: 2014-01-30
 * Time: 3:48 PM
 */
public class JSCompilerHelper {

	private static final Logger log = Logger.getLogger(JSCompilerHelper.class)

	static boolean compile(String jsFile, File file) {
		if(!(jsFile.endsWith(".min.js")) && jsFile.endsWith(".js")){

            String hash = generateFileHash(file)

			String minJSFile = jsFile.replace(".js", ".${hash}.js")

			//if the min.js file does NOT exist or if the original has been modified later than the min.js file
			File destFile = new File(minJSFile)
			if (!destFile.exists() || (destFile.length() == 0)) {
				//Creating new compiler and options objects
				Compiler compiler = new Compiler()
				CompilerOptions options = new CompilerOptions()

				//the compilation level is currently set to Simple. Other levels are Whitespace Only and Advanced (not recommended)
				CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(options)

				//Creating the source and destination file objects in order to copy
				File srcFile = file

				log.info("Attempting to read from file in: "+ srcFile.getCanonicalPath())

				//Copying the contents of the js file in to the min.js file
				FileUtils.copyFile(srcFile, destFile)

				//Creating a Sourcefile list of inputs (should only contain 1 input file)
				SourceFile input = SourceFile.fromFile(destFile)
				List<SourceFile> inputs = ImmutableList.of(input)

				//Creating a Sourcefile list of external files (should be an empty list)
				List<SourceFile> externs = Collections.emptyList()

				//Compiling the min.js file
				Result result = compiler.compile(externs, inputs, options)

				//Writing the compiled code into the min.js file
				FileWriter outputFilestream = new FileWriter(destFile)
				outputFilestream.write(compiler.toSource())
				outputFilestream.close()

				//Testing if the compilation was a success
				if (result.success) {
					log.info("Compilation of $jsFile to $minJSFile successful!")
				} else {
					log.info("Unable to minify $jsFile to $minJSFile successful!")
					return false
				}
			}

			// assuming we got here, that means that either the file was cached or we just created it
			// confirm that it's more than 0 bytes
			if (destFile.length() > 0)
				return true

			log.info("Length of $minJSFile file is zero bytes! Considering compilation a failure.")
		}

		return false
	}

    static String generateFileHash(File sourceFile) {
        FileInputStream fis = new FileInputStream(sourceFile);
        return org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
    }
}
