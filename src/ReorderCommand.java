/**
 * reorder: отсортировать коллекцию в порядке, обратном нынешнему
 */
public class ReorderCommand implements Command {

    @Override
    public String name() { return "reorder"; }

    @Override
    public String description() { return "отсортировать коллекцию в порядке, обратном нынешнему"; }

    @Override
    public boolean execute(String[] args, InputManager input, CommandContext ctx) {
        ctx.cm.reorder();
        System.out.println("Порядок коллекции обращён.");
        return false;
    }
}