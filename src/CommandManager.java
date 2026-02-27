import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Parses and executes commands.
 */
public class CommandManager {
    private final CollectionManager cm;
    private final XmlIO io;

    private final Deque<String> history = new ArrayDeque<>(); // last 13
    private final Deque<String> scriptStack = new ArrayDeque<>(); // recursion guard

    public CommandManager(CollectionManager cm, XmlIO io) {
        this.cm = cm;
        this.io = io;
    }

    public boolean handleLine(String line, InputManager input) {
        String[] parts = line.split("\\s+");
        String cmd = parts[0];

        // history stores only command names (without args)
        pushHistory(cmd);

        try {
            switch (cmd) {
                case "help":
                    help();
                    return false;

                case "info":
                    info();
                    return false;

                case "show":
                    show();
                    return false;

                case "add": {
                    City city = input.readCityForAdd(cm);
                    cm.add(city);
                    System.out.println("Элемент добавлен. id=" + city.getId());
                    return false;
                }

                case "update": {
                    if (parts.length < 2) {
                        System.out.println("Использование: update id");
                        return false;
                    }
                    long id = parseLong(parts[1], "id");
                    City newCity = input.readCityForUpdate(cm, id);
                    boolean ok = cm.update(id, newCity);
                    System.out.println(ok ? "Элемент обновлён." : "Элемент с таким id не найден.");
                    return false;
                }

                case "remove_by_id": {
                    if (parts.length < 2) {
                        System.out.println("Использование: remove_by_id id");
                        return false;
                    }
                    long id = parseLong(parts[1], "id");
                    boolean ok = cm.removeById(id);
                    System.out.println(ok ? "Удалено." : "Элемент с таким id не найден.");
                    return false;
                }

                case "clear":
                    cm.clear();
                    System.out.println("Коллекция очищена.");
                    return false;

                case "save":
                    io.saveFrom(cm);
                    System.out.println("Коллекция сохранена.");
                    return false;

                case "execute_script": {
                    if (parts.length < 2) {
                        System.out.println("Использование: execute_script file_name");
                        return false;
                    }
                    executeScript(parts[1]);
                    return false;
                }

                case "exit":
                    System.out.println("Выход без сохранения.");
                    return true;

                case "remove_lower": {
                    City pivot = input.readCityForComparison(cm);
                    int removed = cm.removeLower(pivot);
                    System.out.println("Удалено элементов: " + removed);
                    return false;
                }

                case "reorder":
                    cm.reorder();
                    System.out.println("Порядок коллекции обращён.");
                    return false;

                case "history":
                    printHistory();
                    return false;

                case "remove_all_by_governor": {
                    Human gov = input.readGovernorValue();
                    int removed = cm.removeAllByGovernor(gov);
                    System.out.println("Удалено элементов: " + removed);
                    return false;
                }

                case "min_by_climate": {
                    City c = cm.minByClimate();
                    if (c == null) System.out.println("Коллекция пуста.");
                    else System.out.println(c);
                    return false;
                }

                case "print_field_ascending_governor":
                    cm.printFieldAscendingGovernor();
                    return false;

                default:
                    System.out.println("Неизвестная команда. help - список команд.");
                    return false;
            }
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
            return false;
        }
    }

    private void help() {
        System.out.println("Доступные команды:");
        System.out.println(" help");
        System.out.println(" info");
        System.out.println(" show");
        System.out.println(" add");
        System.out.println(" update id");
        System.out.println(" remove_by_id id");
        System.out.println(" clear");
        System.out.println(" save");
        System.out.println(" execute_script file_name");
        System.out.println(" exit");
        System.out.println(" remove_lower");
        System.out.println(" reorder");
        System.out.println(" history");
        System.out.println(" remove_all_by_governor");
        System.out.println(" min_by_climate");
        System.out.println(" print_field_ascending_governor");
    }

    private void info() {
        System.out.println(cm.info());
    }

    private void show() {
        if (cm.size() == 0) {
            System.out.println("Коллекция пуста.");
            return;
        }
        for (City c : cm.getAll()) {
            System.out.println(c);
        }
    }

    private void pushHistory(String cmd) {
        history.addLast(cmd);
        while (history.size() > 13) history.removeFirst();
    }

    private void printHistory() {
        if (history.isEmpty()) {
            System.out.println("История пуста.");
            return;
        }
        for (String h : history) System.out.println(h);
    }

    private long parseLong(String s, String fieldName) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Некорректное число для " + fieldName + ": " + s);
        }
    }

    private void executeScript(String fileName) throws Exception {
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
            List<String> lines = Files.readAllLines(Path.of(abs));
            Scanner sc = new Scanner(String.join("\n", lines));
            InputManager scriptInput = new InputManager(sc, false);

            for (String line : lines) {
                String cmdLine = line.trim();
                if (cmdLine.isEmpty()) continue;

                // печать выполняемой команды (удобно при проверке)
                System.out.println("> " + cmdLine);

                boolean exit = handleLine(cmdLine, scriptInput);
                if (exit) {
                    System.out.println("Команда exit в скрипте: прекращаем выполнение скрипта.");
                    break;
                }
            }
        } finally {
            scriptStack.pop();
        }
    }
}