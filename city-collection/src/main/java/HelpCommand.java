/**
 * help: вывести справку по доступным командам
 */
public class HelpCommand implements Command {

    @Override
    public String name() {
        return "help";
    }

    @Override
    public String description() {
        return "вывести справку по доступным командам";
    }

    @Override
    public boolean execute(String[] args, InputManager input, CommandContext ctx) {
        System.out.println("Доступные команды:");
        for (Command c : ctx.manager.allCommands()) {
            System.out.println(" " + c.name() + " : " + c.description());
        }
        return false;
    }
}