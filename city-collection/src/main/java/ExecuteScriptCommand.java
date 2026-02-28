/**
 * execute_script file_name: выполнить скрипт из файла
 */
public class ExecuteScriptCommand implements Command {

    @Override
    public String name() { return "execute_script"; }

    @Override
    public String description() { return "execute_script file_name: считать и исполнить скрипт из указанного файла"; }

    @Override
    public boolean execute(String[] args, InputManager input, CommandContext ctx) throws Exception {
        if (args.length < 1) {
            System.out.println("Использование: execute_script file_name");
            return false;
        }
        ctx.manager.executeScript(args[0]);
        return false;
    }
}