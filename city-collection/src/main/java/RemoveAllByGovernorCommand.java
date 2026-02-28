/**
 * remove_all_by_governor governor: удалить элементы с заданным governor
 */
public class RemoveAllByGovernorCommand implements Command {

    @Override
    public String name() { return "remove_all_by_governor"; }

    @Override
    public String description() { return "удалить все элементы, значение поля governor которых эквивалентно заданному"; }

    @Override
    public boolean execute(String[] args, InputManager input, CommandContext ctx) {
        Human gov = input.readGovernorValue();
        int removed = ctx.cm.removeAllByGovernor(gov);
        System.out.println("Удалено элементов: " + removed);
        return false;
    }
}