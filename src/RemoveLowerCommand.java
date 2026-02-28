/**
 * remove_lower {element}: удалить все элементы меньше заданного
 */
public class RemoveLowerCommand implements Command {

    @Override
    public String name() { return "remove_lower"; }

    @Override
    public String description() { return "remove_lower {element}: удалить все элементы, меньшие чем заданный"; }

    @Override
    public boolean execute(String[] args, InputManager input, CommandContext ctx) {
        City pivot = input.readCityForComparison(ctx.cm);
        int removed = ctx.cm.removeLower(pivot);
        System.out.println("Удалено элементов: " + removed);
        return false;
    }
}