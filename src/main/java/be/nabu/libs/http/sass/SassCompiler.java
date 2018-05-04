package be.nabu.libs.http.sass;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class SassCompiler {
	
	public static void main(String...args) throws ScriptException, IOException, NoSuchMethodException {
		new SassCompiler().compile2();
	}
	
	private Invocable invocable;
	
	private Invocable getInvocable() throws ScriptException, IOException {
		if (invocable == null) {
			synchronized(this) {
				if (invocable == null) {
					final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
			        
					ScriptEngine engine = new ScriptEngineManager().getEngineByName("Nashorn");
					
					Reader reader = new InputStreamReader(contextClassLoader.getResourceAsStream("nashorn-simple-shims.js"));
					try {
						engine.eval(reader);
					}
					finally {
						reader.close();
					}
					
					// the latest version at time of creation, does not work...
//					reader = new InputStreamReader(contextClassLoader.getResourceAsStream("sass.sync.0.10.9.js"));
					
					// does not work (from: https://github.com/yogthos/lein-sass)
//					reader = new InputStreamReader(contextClassLoader.getResourceAsStream("sass.sync.0.9.13.js"));
					
					// does work but is slightly outdated (https://github.com/m4r71n/sass.js-rhino-nashorn)
					reader = new InputStreamReader(contextClassLoader.getResourceAsStream("sass.sync.0.9.6.cleanup-initialization.js"));
					
					try {
						engine.eval(reader);
					}
					finally {
						reader.close();
					}
					
//					final String sass = "var scss = '$someVar: 123px; .some-selector { width: $someVar; }';\n" +
//			                "Sass.compile(scss, function(result) {\n" +
//			                "    console.log(result.text);\n" +
//			                "});";
//					
					final String compiler = "var compile = function(sass) { var result = null; Sass.compile(sass, function(compiled) { result = compiled.text }); return result; }";
					engine.eval(compiler);
					
					invocable = (Invocable) engine;
				}
			}
		}
		return invocable;
	}
	
	public String compile(String sass) {
		synchronized(this) {
			try {
				return (String) getInvocable().invokeFunction("compile", sass);
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public void compile2() throws ScriptException, IOException, NoSuchMethodException {
		final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("Nashorn");
		
		//CompiledScript compile = ((Compilable) engine).compile("");
		
		Reader reader = new InputStreamReader(contextClassLoader.getResourceAsStream("nashorn-simple-shims.js"));
		try {
			engine.eval(reader);
		}
		finally {
			reader.close();
		}
		
		// the latest version at time of creation, does not work...
//		reader = new InputStreamReader(contextClassLoader.getResourceAsStream("sass.sync.0.10.9.js"));
		
		// does not work (from: https://github.com/yogthos/lein-sass)
//		reader = new InputStreamReader(contextClassLoader.getResourceAsStream("sass.sync.0.9.13.js"));
		
		// does work but is slightly outdated (https://github.com/m4r71n/sass.js-rhino-nashorn)
		reader = new InputStreamReader(contextClassLoader.getResourceAsStream("sass.sync.0.9.6.cleanup-initialization.js"));
		
		try {
			engine.eval(reader);
		}
		finally {
			reader.close();
		}
		
//		final String sass = "var scss = '$someVar: 123px; .some-selector { width: $someVar; }';\n" +
//                "Sass.compile(scss, function(result) {\n" +
//                "    console.log(result.text);\n" +
//                "});";
//		
		final String compiler = "var compile = function(sass) { var result = null; Sass.compile(sass, function(compiled) { result = compiled.text }); return result; }";
		engine.eval(compiler);
		
		String sass = "$someVar: 123px; .some-selector { width: $someVar; }";
		
		Invocable invocable = (Invocable) engine;

		System.out.println("Return is: " + invocable.invokeFunction("compile", sass));
		System.out.println("Return is: " + invocable.invokeFunction("compile", sass));
	}
	
}
