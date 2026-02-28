/**
 * show: вывести все элементы коллекции
 */
public class ShowCommand implements Command {

    @Override
    public String name() { return "show"; }

    @Override
    public String description() { return "вывести все элементы коллекции в строковом представлении"; }

    @Override
    public boolean execute(String[] args, InputManager input, CommandContext ctx) {
        if (ctx.cm.size() == 0) {
            System.out.println("Коллекция пуста.");
            return false;
        }
        for (City c : ctx.cm.getAll()) {
            System.out.println(c);
        }
        return false;
    }
}