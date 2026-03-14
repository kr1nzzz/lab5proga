import java.io.File;
import java.util.Scanner;

/**
 * Точка входа в приложение.
 */
public class Main {

    /**
     * Запускает приложение.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Не указан путь к XML-файлу.");
            System.err.println("Пример запуска: java Main src/main/java/data.xml");
            return;
        }

        String filePath = args[0];
        CollectionManager collectionManager = new CollectionManager();
        XmlIO xmlIO = new XmlIO(filePath);

        try {
            xmlIO.loadInto(collectionManager);
            System.out.println("Коллекция успешно загружена из файла: "
                    + new File(filePath).getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Ошибка загрузки XML: " + e.getMessage());
            return;
        }

        CommandManager commandManager = new CommandManager(collectionManager, xmlIO);
        InputManager inputManager = new InputManager(new Scanner(System.in), true);
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите команду. help - список команд.");

        while (true) {
            try {
                System.out.print("> ");

                if (!scanner.hasNextLine()) {
                    break;
                }

                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }

                boolean shouldContinue = commandManager.handleLine(line, inputManager);
                if (!shouldContinue) {
                    System.out.println("Программа завершена.");
                    break;
                }
            } catch (Exception e) {
                System.err.println("Ошибка выполнения команды: " + e.getMessage());
            }
        }
    }
}