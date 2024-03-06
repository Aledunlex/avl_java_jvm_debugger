package fil.command;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Manage the commands.
 */
public class CommandManager {

    private final Map<String, Command<?>> commands = new HashMap<>();

    /**
     * Register a command.
     *
     * @param commandString the string to type to execute the command
     * @param command       the command to register
     */
    public void register(String commandString, Command<?> command) {
        commands.put(commandString, command);
    }

    /**
     * Execute a command.
     *
     * @param commandString the string to type to execute the command
     * @param args          the arguments of the command
     * @return the command executed
     * @throws Exception if the command is not found
     */
    public Command<?> execute(String commandString, String... args) throws Exception {
        Command<?> command = commands.get(commandString);
        if (command != null) {
            System.out.println("Executing command: " + commandString);
            try {
                command.execute(args);
            } catch (Exception e) {
                System.out.println("Error during execution: " + e.toString());
            }
            return command;
        } else {
            throw new RuntimeException("Unknown command: " + commandString);
        }
    }

    public Collection<Command<?>> getCommands() {
        return commands.values();
    }

}