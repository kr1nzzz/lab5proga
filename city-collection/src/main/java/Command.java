/**
 * Интерфейс команды приложения.
 */
public interface Command {

    /**
     * Возвращает имя команды.
     *
     * @return имя команды
     */
    String name();

    /**
     * Возвращает описание команды.
     *
     * @return описание команды
     */
    String description();

    /**
     * Выполняет команду.
     *
     * @param args аргументы команды
     * @param input менеджер ввода
     * @param ctx контекст выполнения
     * @return {@code true}, если программа должна продолжить работу,
     * иначе {@code false}
     */
    boolean execute(String[] args, InputManager input, CommandContext ctx);
}