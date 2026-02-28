/**
 * Интерфейс команды. Каждая команда - отдельный класс.
 */
public interface Command {

    /**
     * @return имя команды (например: "info")
     */
    String name();

    /**
     * @return короткое описание для help
     */
    String description();

    /**
     * Выполнить команду.
     *
     * @param args  аргументы команды (всё после имени команды)
     * @param input менеджер ввода (для чтения объектов)
     * @param ctx   контекст (коллекция, io, менеджер команд)
     * @return true если нужно завершить программу (exit), иначе false
     * @throws Exception если команда упала с ошибкой
     */
    boolean execute(String[] args, InputManager input, CommandContext ctx) throws Exception;
}