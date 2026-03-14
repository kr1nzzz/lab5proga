import java.util.*;

/**
 * Менеджер команд приложения.
 */
public class CommandManager {
    private final Map<String, Command> commands = new LinkedHashMap<>();
    private final Deque<String> history = new ArrayDeque<>();
    private final Deque<String> scriptStack = new ArrayDeque<>();
    private final CommandContext ctx;

    /**
     * Создает менеджер команд.
     *
     * @param cm менеджер коллекции
     * @param io менеджер XML
     */
    public CommandManager(CollectionManager cm, XmlIO io) {
        this.ctx = new CommandContext(cm, io, this);

        register(new HelpCommand());
        register(new InfoCommand());
        register(new ShowCommand());
        register(new AddCommand());
        register(new UpdateCommand());
        register(new RemoveByIdCommand());
        register(new ClearCommand());
        register(new SaveCommand());
        register(new ExecuteScriptCommand());
        register(new ExitCommand());
        register(new RemoveLowerCommand());
        register(new ReorderCommand());
        register(new HistoryCommand());
        register(new RemoveAllByGovernorCommand());
        register(new MinByClimateCommand());
        register(new PrintFieldAscendingGovernorCommand());
    }

    /**
     * Возвращает контекст команд.
     *
     * @return контекст команд
     */
    public CommandContext context() {
        return ctx;
    }

    /**
     * Возвращает все зарегистрированные команды.
     *
     * @return коллекция команд
     */
    public Collection<Command> allCommands() {
        return commands.values();
    }

    /**
     * Регистрирует новую команду.
     *
     * @param cmd команда
     */
    public void register(Command cmd) {
        commands.put(cmd.name(), cmd);
    }

    /**
     * Обрабатывает введенную пользователем строку.
     *
     * @param line строка команды
     * @param input менеджер ввода
     * @return {@code true}, если приложение должно продолжить работу,
     * иначе {@code false}
     */
    public boolean handleLine(String line, InputManager input) {
        String[] parts = line.trim().split("\\s+");
        if (parts.length == 0 || parts[0].isEmpty()) {
            return true;
        }

        String commandName = parts[0];
        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        Command command = commands.get(commandName);
        if (command == null) {
            System.out.println("Неизвестная команда: " + commandName);
            return true;
        }

        history.addLast(commandName);
        if (history.size() > 15) {
            history.removeFirst();
        }

        return command.execute(args, input, ctx);
    }

    /**
     * Выводит историю последних команд.
     */
    public void printHistory() {
        for (String cmd : history) {
            System.out.println(cmd);
        }
    }

    /**
     * Преобразует строку в long.
     *
     * @param s строка
     * @param fieldName имя поля
     * @return число типа long
     */
    public long parseLongArg(String s, String fieldName) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " должен быть числом.");
        }
    }

    /**
     * Выполняет команды из файла скрипта.
     *
     * @param fileName имя файла
     */
    public void executeScript(String fileName) {
        System.out.println("execute_script пока не реализован полностью: " + fileName);
    }
}