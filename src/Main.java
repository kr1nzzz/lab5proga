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
     * @param args аргументы командной строки; args[0] — путь к XML-файлу
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Ошибка: нужно передать имя файла как аргумент командной строки.");
            System.out.println("Пример: java Main data.xml");
            return;
        }

        String filePath = args[0];

        CollectionManager collectionManager = new CollectionManager();
        XmlIO xmlIO = new XmlIO(filePath);

        // Автозагрузка при запуске
        try {
            xmlIO.loadInto(collectionManager);
            System.out.println("Коллекция загружена из файла: " + filePath);
        } catch (Exception e) {
            System.out.println("Предупреждение: не удалось загрузить коллекцию: " + e.getMessage());
            System.out.println("Стартуем с пустой коллекцией.");
        }

        CommandManager commandManager = new CommandManager(collectionManager, xmlIO);

        System.out.println("Введите команду (help для справки).");
        Scanner scanner = new Scanner(System.in);
        InputManager input = new InputManager(scanner, true);

        while (true) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) break;

            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            boolean shouldExit = commandManager.handleLine(line, input);
            if (shouldExit) break;
        }

        System.out.println("Завершение программы.");
    }
}