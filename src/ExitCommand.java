/**
 * exit: завершить программу (без сохранения)
 */
public class ExitCommand implements Command {

    @Override
    public String name() { return "exit"; }

    @Override
    public String description() { return "завершить программу (без сохранения)"; }

    @Override
    public boolean execute(String[] args, InputManager input, CommandContext ctx) {
        System.out.println("Выход без сохранения.");
        return true;
    }
}