package fil.command;

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.StepRequest;

/**
 * execute the current line.
 */
public class StepOverCommand extends StepCommand {

    public StepOverCommand(VirtualMachine vm) {
        super(vm);
    }

    @Override
    protected int getGranularity() {
        return StepRequest.STEP_OVER;
    }

}
