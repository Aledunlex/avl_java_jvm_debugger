package fil.command;

import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;

/**
 * return and print the current frame.
 */
public class FrameCommand implements Command<StackFrame> {
    private final ThreadReference thread;

    public FrameCommand(VirtualMachine vm) {
        this.thread = vm.allThreads().get(0);
    }

    @Override
    public StackFrame execute(String... args) throws IncompatibleThreadStateException {
        StackFrame frame = thread.frame(0);
        System.out.println("Current frame: " + frame);
        return frame;
    }

}
