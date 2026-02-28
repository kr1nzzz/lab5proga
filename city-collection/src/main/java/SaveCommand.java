/**
 * save: сохранить коллекцию в файл
 */
public class SaveCommand implements Command {

    @Override
    public String name() { return "save"; }

    @Override
    public String description() { return "сохранить коллекцию в файл"; }

    @Override
    public boolean execute(String[] args, InputManager input, CommandContext ctx) throws Exception {
        ctx.io.saveFrom(ctx.cm);
        System.out.println("Коллекция сохранена.");
        return false;
    }
}