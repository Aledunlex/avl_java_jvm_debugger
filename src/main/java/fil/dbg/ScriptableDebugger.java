package fil.dbg;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import com.sun.jdi.connect.VMStartException;
import com.sun.jdi.event.*;
import com.sun.jdi.request.ClassPrepareRequest;
import fil.command.*;
import fil.ui.controllers.DebuggingAppController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;

/**
 * The `ScriptableDebugger` class is responsible for connecting to a virtual machine,
 * managing debugging commands, and handling debug events for a specified class.
 */
public class ScriptableDebugger {

    private Class<?> debugClass;
    private VirtualMachine vm;
    private CommandManager commandManager;
    private DebuggingAppController listenerApp;

    /**
     * Connects to and launches the virtual machine for debugging.
     *
     * @return The connected VirtualMachine instance.
     * @throws IOException                        If an I/O error occurs.
     * @throws IllegalConnectorArgumentsException If the connector arguments are invalid.
     * @throws VMStartException                   If the virtual machine fails to start.
     */
    public VirtualMachine connectAndLaunchVM() throws IOException, IllegalConnectorArgumentsException, VMStartException {
        LaunchingConnector launchingConnector = Bootstrap.virtualMachineManager().defaultConnector();
        Map<String, Connector.Argument> arguments = launchingConnector.defaultArguments();
        arguments.get("main").setValue(debugClass.getName());
        return launchingConnector.launch(arguments);
    }

    /**
     * Attaches the debugger to the specified debuggee class and starts debugging.
     *
     * @param debuggeeClass The class to debug.
     */
    public void attachTo(Class<?> debuggeeClass) {

        this.debugClass = debuggeeClass;
        try {
            vm = connectAndLaunchVM();
            enableClassPrepareRequest(vm);
            initCommandManager();
            startDebugger();

            // log dans la console du debugger l'output de la VM (debuggee)
            System.out.println("Debugee output ===");
            InputStreamReader reader = new InputStreamReader(vm.process().getInputStream());
            OutputStreamWriter writer = new OutputStreamWriter(System.out);

            char[] buf = new char[vm.process().getInputStream().available()];
            reader.read(buf);
            writer.write(buf);
            writer.flush();
        } catch (VMDisconnectedException e) {
            System.out.println("Virtual Machine is disconnected: " + e.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Enables the class prepare request for the specified virtual machine.
     *
     * @param vm the virtual machine
     */
    private void enableClassPrepareRequest(VirtualMachine vm) {
        System.out.println("ClassPrepareRequest enabled for " + debugClass.getName());
        ClassPrepareRequest classPrepareRequest = vm.eventRequestManager().createClassPrepareRequest();
        classPrepareRequest.addClassFilter(debugClass.getName());
        classPrepareRequest.enable();
    }

    /**
     * Starts the debugger event loop to handle debug events.
     *
     * @throws Exception If an exception occurs during debugging.
     */
    public void startDebugger() throws Exception {
        EventSet eventSet;
        while ((eventSet = vm.eventQueue().remove()) != null) {
            for (Event event : eventSet) {
                System.out.println(event.toString());

                // catch exception VMDisconnectedException
                if (event instanceof VMDisconnectEvent) {
                    System.out.println("===End of program.");
                    return;
                }

                // break on main class
                if (event instanceof ClassPrepareEvent) {
                    // getting the first line of the main method of the debuggee class
                    ClassPrepareEvent classPrepareEvent = (ClassPrepareEvent) event;
                    int lineNumber = classPrepareEvent.referenceType().methodsByName("main").get(0).location().lineNumber();
                    commandManager.execute("break", debugClass.getName(), String.valueOf(lineNumber));
                }

                // letting the listener (e.g. the GUI controller) handle the event if required
                notifyListener(event);

                // event for step command
                if (event instanceof BreakpointEvent) {
                    // disable breakpoint if it is a once only breakpoint
                    BreakpointEvent breakpointEvent = (BreakpointEvent) event;
                    Object isOnceOnly = breakpointEvent.request().getProperty("once");
                    if (isOnceOnly != null && (boolean) isOnceOnly) {
                        breakpointEvent.request().disable();
                        vm.eventRequestManager().deleteEventRequest(breakpointEvent.request());
                    }
                    // give the hand to the user to enter a command
                    detectAndExecuteCommand();
                }

                if (event instanceof StepEvent) {
                    event.request().disable();
                    detectAndExecuteCommand();
                }

                vm.resume();
            }
        }
    }

    /**
     * Notifies the listener application of the specified event (e.g. the GUI controller).
     *
     * @param event the event to notify
     */
    private void notifyListener(Event event) {
        if (listenerApp != null) {
            listenerApp.onEvent(event);
        }
    }

    /**
     * Initializes the command manager with the commands to be used for debugging.
     */
    private void initCommandManager() {
        commandManager = new CommandManager();
        commandManager.register("step", new StepCommand(vm));
        commandManager.register("step-over", new StepOverCommand(vm));
        commandManager.register("continue", new ContinueCommand(vm));
        commandManager.register("frame", new FrameCommand(vm));
        commandManager.register("temporaries", new TemporariesCommand(vm));
        commandManager.register("stack", new StackCommand(vm));
        commandManager.register("receiver", new ReceiverCommand(vm));
        commandManager.register("sender", new SenderCommand(vm));
        commandManager.register("receiver-variables", new ReceiverVariablesCommand(vm));
        commandManager.register("method", new MethodCommand(vm));
        commandManager.register("arguments", new ArgumentsCommand(vm));
        commandManager.register("breakpoints", new BreakPointsCommand(vm));

        // Note: Les commandes suivantes nécessitent des paramètres supplémentaires qu'il
        // faudra passer en argument à l'exécution
        commandManager.register("print-var", new PrintVarCommand(vm));
        commandManager.register("break", new BreakCommand(vm));
        commandManager.register("break-once", new BreakOnceCommand(vm));
        commandManager.register("break-on-count", new BreakOnCountCommand(vm));
        commandManager.register("break-before-method-call", new BreakBeforeMethodCallCommand(vm));
    }

    /**
     * Detects and executes a command entered by the user.
     *
     * @throws Exception If an exception occurs during command execution.
     */
    public void detectAndExecuteCommand() throws Exception {
        if (listenerApp != null) {
            vm.suspend();
            // leaving the GUI handle command inputs
            return;
        }
        InputStreamReader in = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(in);
        System.out.println("Enter command (or 'continue' to proceed):");

        String userInput = reader.readLine();
        String[] commandAndArgs = userInput.split(" ");
        String command = commandAndArgs[0];

        Command<?> cmd;
        try {
            if (commandAndArgs.length > 1) {
                String[] args = new String[commandAndArgs.length - 1];
                System.arraycopy(commandAndArgs, 1, args, 0, commandAndArgs.length - 1);
                cmd = commandManager.execute(command, args);
            } else {
                cmd = commandManager.execute(command);
            }
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            System.err.println("Try another command.");
            detectAndExecuteCommand();
            return;
        }

        if (!(cmd instanceof StepCommand || cmd instanceof ContinueCommand)) {
            // if the command does not require moving on, waits for the next user input
            detectAndExecuteCommand();
        }
    }

    /**
     * Gets the currently set debug class.
     *
     * @return The currently set debug class.
     */
    public Class<?> getDebugClass() {
        return debugClass;
    }

    /**
     * Sets the debug class to be used for debugging.
     *
     * @param debugClass The debug class to set.
     */
    public void setDebugClass(Class<?> debugClass) {
        this.debugClass = debugClass;
    }

    /**
     * Gets the VirtualMachine instance associated with the debugger.
     *
     * @return The VirtualMachine instance.
     */
    public VirtualMachine getVm() {
        return vm;
    }

    public void setVm(VirtualMachine vm) {
        this.vm = vm;
    }

    /**
     * Gets the CommandManager instance for managing debugging commands.
     *
     * @return The CommandManager instance.
     */
    public CommandManager getCommandManager() {
        return commandManager;
    }

    /**
     * Sets the CommandManager instance for managing debugging commands.
     *
     * @param commandManager The CommandManager instance to set.
     */
    public void setCommandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    /**
     * Sets the listener application for handling debugging events.
     *
     * @param listenerApp The DebuggingAppController instance to set as the listener.
     */
    public void setListenerApp(DebuggingAppController listenerApp) {
        this.listenerApp = listenerApp;
    }

}
