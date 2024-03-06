package fil.command;

import com.sun.jdi.*;

import java.util.Map;

/**
 * Print the value of the variable passed as parameter.
 */
public class PrintVarCommand implements Command<Map<String, Value>> {

    private final ThreadReference thread;

    public PrintVarCommand(VirtualMachine vm) {
        this.thread = vm.allThreads().get(0);
    }

    @Override
    public Map<String, Value> execute(String... args) throws AbsentInformationException, IncompatibleThreadStateException {
        try {
            String varName = args[0];
            StackFrame frame = thread.frame(0);
            LocalVariable localVariable = frame.visibleVariableByName(varName);
            Value value = frame.getValue(localVariable);
            System.out.println(varName + " -> " + value);
            return Map.of(varName, value);
        } catch (Exception e) {
            System.out.println("Variable could not be found");
            return Map.of();
        }
    }

}
