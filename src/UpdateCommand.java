/**
 * update id: обновить элемент по id
 */
public class UpdateCommand implements Command {

    @Override
    public String name() { return "update"; }

    @Override
    public String description() { return "update id {element}: обновить элемент коллекции по id"; }

    @Override
    public boolean execute(String[] args, InputManager input, CommandContext ctx) {
        if (args.length < 1) {
            System.out.println("Использование: update id");
            return false;
        }
        long id = ctx.manager.parseLongArg(args[0], "id");
        City newCity = input.readCityForUpdate(ctx.cm, id);
        boolean ok = ctx.cm.update(id, newCity);
        System.out.println(ok ? "Элемент обновлён." : "Элемент с таким id не найден.");
        return false;
    }
}