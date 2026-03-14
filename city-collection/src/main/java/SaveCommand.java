/**
 * Команда сохранения коллекции в файл.
 */
public class SaveCommand implements Command {

    @Override
    public String name() {
        return "save";
    }

    @Override
    public String description() {
        return "сохранить коллекцию в файл";
    }

    @Override
    public boolean execute(String[] args, InputManager input, CommandContext ctx) {
        try {
            ctx.io.saveFrom(ctx.cm);
            System.out.println("Коллекция сохранена.");
        } catch (Exception e) {
            System.out.println("Ошибка сохранения: " + e.getMessage());
        }
        return true;
    }
}