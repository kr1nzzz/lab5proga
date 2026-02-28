/**
 * clear: очистить коллекцию
 */
public class ClearCommand implements Command {

    @Override
    public String name() { return "clear"; }

    @Override
    public String description() { return "очистить коллекцию"; }

    @Override
    public boolean execute(String[] args, InputManager input, CommandContext ctx) {
        ctx.cm.clear();
        System.out.println("Коллекция очищена.");
        return false;
    }
}