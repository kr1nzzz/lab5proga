/**
 * Команда выполнения скрипта из файла.
 */
public class ExecuteScriptCommand implements Command {

    @Override
    public String name() {
        return "execute_script";
    }

    @Override
    public String description() {
        return "считать и исполнить скрипт из указанного файла";
    }

    @Override
    public boolean execute(String[] args, InputManager input, CommandContext ctx) {
        if (args.length != 1) {
            System.out.println("Использование: execute_script file_name");
            return true;
        }

        try {
            ctx.manager.executeScript(args[0]);
        } catch (Exception e) {
            System.out.println("Ошибка выполнения скрипта: " + e.getMessage());
        }

        return true;
    }
}