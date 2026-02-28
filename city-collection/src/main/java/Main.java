import java.util.Scanner;

/**
 * Точка входа в приложение.
 * <p>
 * Запускает интерактивный режим управления коллекцией городов.
 * Имя XML-файла для загрузки/сохранения передаётся первым аргументом командной строки.
 * При старте пытается загрузить коллекцию из файла, затем принимает команды пользователя.
 * </p>
 */
public class Main {

    /**
     * Главный метод приложения.
     *
     * @param args args[0] — путь к XML-файлу
     */
    public static void main(String[] args) {
        // 1) Проверяем аргументы командной строки
        if (args.length < 1) {
            System.out.println("Ошибка: нужно передать имя файла как аргумент командной строки.");
            System.out.println("Пример: java Main data.xml");
            return;
        }

        String filePath = args[0];

        // 2) Создаём основные компоненты приложения
        CollectionManager collectionManager = new CollectionManager();
        XmlIO xmlIO = new XmlIO(filePath);

        // 3) Автоматическая загрузка коллекции при старте
        try {
            xmlIO.loadInto(collectionManager);
            System.out.println("Коллекция загружена из файла: " + filePath);
        } catch (Exception e) {
            System.out.println("Предупреждение: не удалось загрузить коллекцию: " + e.getMessage());
            System.out.println("Стартуем с пустой коллекцией.");
        }

        // 4) Менеджер команд (без switch-case, команды в отдельных классах)
        CommandManager commandManager = new CommandManager(collectionManager, xmlIO);

        // 5) Подготавливаем ввод пользователя
        Scanner scanner = new Scanner(System.in);
        InputManager inputManager = new InputManager(scanner, true);

        // 6) Главный цикл: читаем строки и передаём их менеджеру команд
        System.out.println("Введите команду (help для справки).");
        while (true) {
            System.out.print("> ");

            // Если ввода больше нет (например, Ctrl+D) — завершаем
            if (!scanner.hasNextLine()) break;

            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            // handleLine вернёт true только для команды exit
            boolean shouldExit = commandManager.handleLine(line, inputManager);
            if (shouldExit) break;
        }

        System.out.println("Завершение программы.");
    }
}