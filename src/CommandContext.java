/**
 * Контекст команд: тут лежат общие объекты, нужные всем командам.
 */
public class CommandContext {
    public final CollectionManager cm;
    public final XmlIO io;
    public final CommandManager manager;

    public CommandContext(CollectionManager cm, XmlIO io, CommandManager manager) {
        this.cm = cm;
        this.io = io;
        this.manager = manager;
    }
}