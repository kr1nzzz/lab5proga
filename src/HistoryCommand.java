/**
 * history: вывести последние 13 команд
 */
public class HistoryCommand implements Command {

    @Override
    public String name() { return "history"; }

    @Override
    public String description() { return "вывести последние 13 команд (без аргументов)"; }

    @Override
    public boolean execute(String[] args, InputManager input, CommandContext ctx) {
        ctx.manager.printHistory();
        return false;
    }
}