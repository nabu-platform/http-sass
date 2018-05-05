package be.nabu.libs.http.sass;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import io.bit3.jsass.CompilationException;
import io.bit3.jsass.Compiler;
import io.bit3.jsass.Options;
import io.bit3.jsass.Output;

public class SassCompiler {
	
	public static void main(String...args) throws ScriptException, IOException, NoSuchMethodException {
		System.out.println(new SassCompiler().compile("$someVar: 123px; .some-selector { width: $someVar; }"));
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
					final String compiler = "var compile = function(sass) { var result = null; Sass.compile(sass, function(compiled) { result = compiled }); return result; }";
					engine.eval(compiler);
					
					invocable = (Invocable) engine;
				}
			}
		}
		return invocable;
	}
	
	@SuppressWarnings("unchecked")
	public String compile(String sass) {
		synchronized(this) {
			try {
				Map<String, Object> result = (Map<String, Object>) getInvocable().invokeFunction("compile", sass);
				if ("0".equals(result.get("status").toString())) {
					return (String) result.get("text");
				}
				else {
					// other fields of interest: status,file,line,column,message,formatted
					System.err.println(result.entrySet());
					throw new ParseException(result.get("message") + "\n\n" + result.get("formatted"), 0);
				}
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	// uses: https://github.com/bit3/jsass
	public String compile2(String sass) {
		Compiler compiler = new Compiler();
	    Options options = new Options();
	    try {
		    Output compileString = compiler.compileString(sass, options);
		    return compileString.getCss();
	    }
	    catch (CompilationException e) {
	    	throw new RuntimeException(e);
	    }
	}
	
	@SuppressWarnings("unchecked")
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
		final String compiler = "var compile = function(sass) { var result = null; Sass.compile(sass, function(compiled) { console.log('compiled', compiled.message); result = compiled }); return result; }";
		engine.eval(compiler);
		
		String sass = "$someVar: 123px; .some-selector { width: $someVar2; }";
		
		Invocable invocable = (Invocable) engine;

		Map<String, Object> invokeFunction = (Map<String, Object>) invocable.invokeFunction("compile", sass);
		
		System.out.println("Return is: " + invokeFunction.values());
	}
	
}
