package fil.command;

import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;

/**
 * return the receiver of the current method (this).
 */
public class ReceiverCommand implements Command<ObjectReference> {
    private final ThreadReference thread;

    public ReceiverCommand(VirtualMachine vm) {
        this.thread = vm.allThreads().get(0);
    }

    @Override
    public ObjectReference execute(String... args) throws IncompatibleThreadStateException {
        return thread.frame(0).thisObject();
    }

}
