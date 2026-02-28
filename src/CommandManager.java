import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Scanner;

/**
 * Обработчик команд интерактивного режима.
 * <p>
 * Разбирает введённую строку на команду и аргументы, вызывает соответствующие операции
 * у {@link CollectionManager}. Ведёт историю последних команд и поддерживает выполнение
 * команд из файла (скрипт) с защитой от рекурсии.
 * </p>
 */
public class CommandManager {

    private final CollectionManager cm;
    private final XmlIO io;

    private final Deque<String> history = new ArrayDeque<>();      // последние 13 команд
    private final Deque<String> scriptStack = new ArrayDeque<>();  // защита от рекурсии скриптов

    /**
     * Конструктор.
     *
     * @param cm менеджер коллекции
     * @param io модуль чтения/записи XML
     */
    public CommandManager(CollectionManager cm, XmlIO io) {
        this.cm = cm;
        this.io = io;
    }

    /**
     * Обрабатывает одну строку с командой.
     *
     * @param line  строка, введённая пользователем или считанная из скрипта
     * @param input менеджер ввода, используемый для чтения полей объектов
     * @return {@code true}, если программа должна завершиться (команда exit), иначе {@code false}
     */
    public boolean handleLine(String line, InputManager input) {
        String[] parts = line.split("\\s+");
        String cmd = parts[0];

        // в историю пишем только имя команды (без аргументов)
        pushHistory(cmd);

        try {
            switch (cmd) {
                case "help":
                    help();
                    return false;

                case "info":
                    System.out.println(cm.info());
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
                    System.out.println(c == null ? "Коллекция пуста." : c.toString());
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

    /**
     * Печатает справку по командам.
     */
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

    /**
     * Выводит все элементы коллекции в строковом представлении.
     */
    private void show() {
        if (cm.size() == 0) {
            System.out.println("Коллекция пуста.");
            return;
        }
        for (City c : cm.getAll()) {
            System.out.println(c);
        }
    }

    /**
     * Добавляет команду в историю (последние 13).
     *
     * @param cmd имя команды
     */
    private void pushHistory(String cmd) {
        history.addLast(cmd);
        while (history.size() > 13) history.removeFirst();
    }

    /**
     * Печатает историю последних команд.
     */
    private void printHistory() {
        if (history.isEmpty()) {
            System.out.println("История пуста.");
            return;
        }
        for (String h : history) System.out.println(h);
    }

    /**
     * Парсит long с понятным сообщением об ошибке.
     *
     * @param s         строка
     * @param fieldName имя поля (для текста ошибки)
     * @return значение long
     */
    private long parseLong(String s, String fieldName) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Некорректное число для " + fieldName + ": " + s);
        }
    }

    /**
     * Выполняет команды из файла-скрипта.
     * <p>
     * Файл должен содержать команды в том же формате, что и интерактивный ввод.
     * Реализована защита от рекурсивных вызовов скриптов.
     * </p>
     *
     * @param fileName имя (или путь) файла скрипта
     * @throws Exception если произошла ошибка чтения файла или выполнения команд
     */
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