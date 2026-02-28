/**
 * print_field_ascending_governor: вывести значения поля governor по возрастанию
 */
public class PrintFieldAscendingGovernorCommand implements Command {

    @Override
    public String name() { return "print_field_ascending_governor"; }

    @Override
    public String description() { return "вывести значения поля governor всех элементов в порядке возрастания"; }

    @Override
    public boolean execute(String[] args, InputManager input, CommandContext ctx) {
        ctx.cm.printFieldAscendingGovernor();
        return false;
    }
}