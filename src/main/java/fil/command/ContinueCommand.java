package fil.command;

import com.sun.jdi.VirtualMachine;

/**
 * Continue the execution until the next breakpoint. The granularity is the step instruction.
 */
public class ContinueCommand implements Command<Void> {
    private final VirtualMachine vm;

    public ContinueCommand(VirtualMachine vm) {
        this.vm = vm;
    }

    @Override
    public Void execute(String... args) {
        vm.resume();
        return null;
    }
}
