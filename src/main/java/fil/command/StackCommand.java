package fil.command;

import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;

import java.util.ArrayList;
import java.util.List;

/**
 * return the stack of methods that led to the current execution point.
 */
public class StackCommand implements Command<List<StackFrame>> {

    VirtualMachine vm;

    public StackCommand(VirtualMachine vm) {
        this.vm = vm;
    }

    @Override
    public List<StackFrame> execute(String... args) throws IncompatibleThreadStateException {
        List<StackFrame> frames = new ArrayList<>();
        try {
            for (ThreadReference thread : vm.allThreads()) {
                for (StackFrame frame : thread.frames()) {
                    frames.add(frame);
                    System.out.println(frame);
                }
            }
        } catch (Exception e) {
            System.out.println("Couldn't get stack frames");
        }
        return frames;
    }

}
