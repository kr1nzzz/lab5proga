/**
 * add: добавить новый элемент в коллекцию
 */
public class AddCommand implements Command {

    @Override
    public String name() { return "add"; }

    @Override
    public String description() { return "добавить новый элемент в коллекцию"; }

    @Override
    public boolean execute(String[] args, InputManager input, CommandContext ctx) {
        City city = input.readCityForAdd(ctx.cm);
        ctx.cm.add(city);
        System.out.println("Элемент добавлен. id=" + city.getId());
        return false;
    }
}