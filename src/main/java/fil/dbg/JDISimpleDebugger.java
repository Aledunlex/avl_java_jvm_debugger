package fil.dbg;

/**
 * Create an instance of the debugger and attach it to the debuggee
 */
public class JDISimpleDebugger {

    public static void main(String[] args) throws Exception {
        ScriptableDebugger debuggerInstance = new ScriptableDebugger();
        debuggerInstance.attachTo(JDISimpleDebuggee.class);
    }

}

