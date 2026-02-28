/**
 * info: вывести информацию о коллекции
 */
public class InfoCommand implements Command {

    @Override
    public String name() { return "info"; }

    @Override
    public String description() {
        return "вывести информацию о коллекции (тип, дата инициализации, количество элементов)";
    }

    @Override
    public boolean execute(String[] args, InputManager input, CommandContext ctx) {
        System.out.println(ctx.cm.info());
        return false;
    }
}