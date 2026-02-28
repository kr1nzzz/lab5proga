/**
 * remove_by_id id: удалить элемент по id
 */
public class RemoveByIdCommand implements Command {

    @Override
    public String name() { return "remove_by_id"; }

    @Override
    public String description() { return "remove_by_id id: удалить элемент из коллекции по его id"; }

    @Override
    public boolean execute(String[] args, InputManager input, CommandContext ctx) {
        if (args.length < 1) {
            System.out.println("Использование: remove_by_id id");
            return false;
        }
        long id = ctx.manager.parseLongArg(args[0], "id");
        boolean ok = ctx.cm.removeById(id);
        System.out.println(ok ? "Удалено." : "Элемент с таким id не найден.");
        return false;
    }
}