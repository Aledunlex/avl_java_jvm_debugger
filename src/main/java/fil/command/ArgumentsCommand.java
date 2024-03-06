package fil.command;

import com.sun.jdi.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * return and print the list of arguments of the current method.
 */
public class ArgumentsCommand implements Command<Map<String, Value>> {
    private final ThreadReference thread;

    public ArgumentsCommand(VirtualMachine vm) {
        this.thread = vm.allThreads().get(0);
    }

    @Override
    public Map<String, Value> execute(String... args) throws AbsentInformationException, IncompatibleThreadStateException {
        Map<String, Value> arguments = new HashMap<>();
        try {
            StackFrame frame = thread.frame(0);
            Method method = frame.location().method();
            List<LocalVariable> methodArguments = method.arguments();
            for (LocalVariable localVariable : methodArguments) {
                Value value = frame.getValue(localVariable);
                arguments.put(localVariable.name(), value);
                System.out.println(localVariable.name() + " -> " + value);
            }
        } catch (Exception e) {
            System.out.println("Arguments could not be found");
        }
        return arguments;
    }

}
