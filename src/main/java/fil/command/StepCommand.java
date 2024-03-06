package fil.command;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.StepRequest;

/**
 * execute the next instruction. If it is a method call, the execution enters this method.
 */
public class StepCommand implements Command<StepRequest> {

    private final VirtualMachine vm;
    private final ThreadReference thread;

    public StepCommand(VirtualMachine vm) {
        this.vm = vm;
        this.thread = vm.allThreads().get(0);
    }

    @Override
    public StepRequest execute(String... args) {
        try {
            StepRequest stepRequest = vm.eventRequestManager().createStepRequest(
                    thread, StepRequest.STEP_LINE, getGranularity()
            );
            stepRequest.enable();
            return stepRequest;
        } catch (Exception e) {
            System.out.println("Couldn't create a step request");
            return null;
        }
    }

    protected int getGranularity() {
        return StepRequest.STEP_INTO;
    }

}
