package fil.command;

import com.sun.jdi.*;

import java.util.HashMap;
import java.util.Map;

/**
 * return and print the list of temporary variables of the current frame,
 * under the form of a couple name -> value.
 */
public class TemporariesCommand implements Command<Map<String, Value>> {
    private final ThreadReference thread;

    public TemporariesCommand(VirtualMachine vm) {
        this.thread = vm.allThreads().get(0);
    }

    @Override
    public Map<String, Value> execute(String... args) throws IncompatibleThreadStateException, AbsentInformationException {
        Map<String, Value> temporaries = new HashMap<>();
        try {
            StackFrame frame = thread.frame(0);
            for (LocalVariable localVariable : frame.visibleVariables()) {
                Value value = frame.getValue(localVariable);
                temporaries.put(localVariable.name(), value);
                System.out.println(localVariable.name() + " -> " + value);
            }
        } catch (Exception e) {
            System.out.println("No visible local variables");
        }
        return temporaries;
    }

}
