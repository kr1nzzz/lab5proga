/**
 * min_by_climate: вывести любой объект, значение поля climate которого является минимальным
 */
public class MinByClimateCommand implements Command {

    @Override
    public String name() { return "min_by_climate"; }

    @Override
    public String description() { return "вывести любой объект, значение поля climate которого минимально"; }

    @Override
    public boolean execute(String[] args, InputManager input, CommandContext ctx) {
        City c = ctx.cm.minByClimate();
        System.out.println(c == null ? "Коллекция пуста." : c.toString());
        return false;
    }
}