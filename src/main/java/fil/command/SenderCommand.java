package fil.command;

import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.StackFrame;
import com.sun.jdi.VirtualMachine;

/**
 * return the object that called the current method.
 */
public class SenderCommand implements Command<ObjectReference> {

    private final VirtualMachine vm;

    public SenderCommand(VirtualMachine vm) {
        this.vm = vm;
    }

    @Override
    public ObjectReference execute(String... args) throws IncompatibleThreadStateException {
        try {
            StackFrame callingFrame = vm.allThreads().get(0).frame(1);
            return callingFrame.thisObject();
        } catch (Exception e) {
            System.out.println("No object reference found");
            return null;
        }
    }

}
