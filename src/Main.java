import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Ошибка: нужно передать имя файла как аргумент командной строки.");
            System.out.println("Пример: java Main data.xml");
            return;
        }

        String filePath = args[0];

        CollectionManager collectionManager = new CollectionManager();
        XmlIO xmlIO = new XmlIO(filePath);

        try {
            xmlIO.loadInto(collectionManager);
            System.out.println("Коллекция загружена из файла: " + filePath);
        } catch (Exception e) {
            System.out.println("Предупреждение: не удалось загрузить коллекцию: " + e.getMessage());
            System.out.println("Стартуем с пустой коллекцией.");
        }

        CommandManager commandManager = new CommandManager(collectionManager, xmlIO);

        // interactive mode
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