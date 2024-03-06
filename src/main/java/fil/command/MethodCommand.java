package fil.command;

import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.Method;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;

/**
 * return and print the current method.
 */
public class MethodCommand implements Command<Method> {
    private final ThreadReference thread;

    public MethodCommand(VirtualMachine vm) {
        this.thread = vm.allThreads().get(0);
    }

    @Override
    public Method execute(String... args) throws IncompatibleThreadStateException {
        Method method = null;
        try {
            method = thread.frame(0).location().method();
            System.out.println(method);
        } catch (IncompatibleThreadStateException e) {
            System.out.println("No current method");
        }
        return method;
    }
}
