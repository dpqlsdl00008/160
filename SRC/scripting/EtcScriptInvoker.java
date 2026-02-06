package scripting;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class EtcScriptInvoker {

    private static final ScriptEngineManager sem = new ScriptEngineManager();

    public static Invocable getInvocable(String path) {
        FileReader fr = null;
        try {
            path = "scripts/" + path;
            ScriptEngine engine = null;
            if (engine == null) {
                File scriptFile = new File(path);
                if (!scriptFile.exists()) {
                    return null;
                }
                engine = sem.getEngineByName("javascript");
                fr = new FileReader(scriptFile);
                engine.eval(fr);
            }
            return (Invocable) engine;
        } catch (Exception e) {
            System.err.println("Error executing Etc script. Path: " + path + "\nException " + e);
            return null;
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException ignore) {
            }
        }
    }
}
