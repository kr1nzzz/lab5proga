import java.io.File;
import java.nio.file.Files;
import java.util.*;

/**
 * Менеджер команд: хранит команды в Map и вызывает их по имени.
 * Поддерживает history и execute_script (с защитой от рекурсии).
 */
public class CommandManager {

    private final Map<String, Command> commands = new HashMap<>();
    private final Deque<String> history = new ArrayDeque<>();      // последние 13
    private final Deque<String> scriptStack = new ArrayDeque<>();  // защита от рекурсии

    private final CommandContext ctx;

    public CommandManager(CollectionManager cm, XmlIO io) {
        this.ctx = new CommandContext(cm, io, this);

        // РЕГИСТРАЦИЯ КОМАНД (каждая - отдельный класс)
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

    public CommandContext context() {
        return ctx;
    }

    public Collection<Command> allCommands() {
        // Для help
        List<Command> list = new ArrayList<>(commands.values());
        list.sort(Comparator.comparing(Command::name));
        return list;
    }

    public void register(Command cmd) {
        commands.put(cmd.name(), cmd);
    }

    /**
     * Главный вход: обработать строку с командой.
     *
     * @return true если нужно выйти
     */
    public boolean handleLine(String line, InputManager input) {
        String trimmed = line.trim();
        if (trimmed.isEmpty()) return false;

        String[] parts = trimmed.split("\\s+");
        String cmdName = parts[0];

        pushHistory(cmdName);

        String[] args = Arrays.copyOfRange(parts, 1, parts.length);

        Command cmd = commands.get(cmdName);
        if (cmd == null) {
            System.out.println("Неизвестная команда. help - список команд.");
            return false;
        }

        try {
            return cmd.execute(args, input, ctx);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
            return false;
        }
    }

    private void pushHistory(String cmd) {
        history.addLast(cmd);
        while (history.size() > 13) history.removeFirst();
    }

    public void printHistory() {
        if (history.isEmpty()) {
            System.out.println("История пуста.");
            return;
        }
        for (String h : history) System.out.println(h);
    }

    public long parseLongArg(String s, String fieldName) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Некорректное число для " + fieldName + ": " + s);
        }
    }

    /**
     * Выполнить скрипт (execute_script).
     */
    public void executeScript(String fileName) throws Exception {
        File f = new File(fileName);
        if (!f.exists() || !f.isFile()) {
            System.out.println("Файл скрипта не найден: " + fileName);
            return;
        }

        String abs = f.getCanonicalPath();
        if (scriptStack.contains(abs)) {
            System.out.println("Обнаружена рекурсия скриптов. Прерывание: " + fileName);
            return;
        }

        scriptStack.push(abs);
        try {
            List<String> lines = Files.readAllLines(f.toPath());
            Scanner sc = new Scanner(String.join("\n", lines));
            InputManager scriptInput = new InputManager(sc, false);

            for (String line : lines) {
                String cmdLine = line.trim();
                if (cmdLine.isEmpty()) continue;

                System.out.println("> " + cmdLine);
                boolean exit = handleLine(cmdLine, scriptInput);
                if (exit) {
                    System.out.println("Команда exit внутри скрипта: прекращаем выполнение скрипта.");
                    break;
                }
            }
        } finally {
            scriptStack.pop();
        }
    }
}