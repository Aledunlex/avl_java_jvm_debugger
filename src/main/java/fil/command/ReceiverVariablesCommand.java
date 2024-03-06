package fil.command;

import com.sun.jdi.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * return and print the list of instance variables of the current receiver,
 * under the form of a couple name -> value.
 */
public class ReceiverVariablesCommand implements Command<Map<String, Value>> {
    private final ThreadReference thread;

    public ReceiverVariablesCommand(VirtualMachine vm) {
        this.thread = vm.allThreads().get(0);
    }

    @Override
    public Map<String, Value> execute(String... args) throws IncompatibleThreadStateException {
        Map<String, Value> variables = new HashMap<>();
        try {
            ObjectReference thisObject = thread.frame(0).thisObject();
            ReferenceType referenceType = Objects.requireNonNull(thisObject).referenceType();
            for (Field field : referenceType.fields()) {
                Value value = thisObject.getValue(field);
                variables.put(field.name(), value);
                System.out.println(field.name() + " -> " + value);
            }
        } catch (Exception e) {
            System.out.println("No receiver reference found");
        }
        return variables;
    }

}
