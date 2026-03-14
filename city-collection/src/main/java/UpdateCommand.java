/**
 * Команда обновления элемента коллекции по его идентификатору.
 * <p>
 * Если элемент с указанным идентификатором существует,
 * пользователю предлагается ввести новые значения его полей.
 * Если элемента с таким идентификатором нет, выводится предупреждение.
 * </p>
 */
public class UpdateCommand implements Command {

    /**
     * Возвращает имя команды.
     *
     * @return имя команды
     */
    @Override
    public String name() {
        return "update";
    }

    /**
     * Возвращает описание команды.
     *
     * @return описание команды
     */
    @Override
    public String description() {
        return "обновить элемент коллекции по id";
    }

    /**
     * Выполняет команду обновления элемента коллекции.
     *
     * @param args аргументы команды
     * @param input менеджер ввода
     * @param ctx контекст выполнения команды
     * @return {@code true}, если программа должна продолжить работу
     */
    @Override
    public boolean execute(String[] args, InputManager input, CommandContext ctx) {
        if (args.length != 1) {
            System.out.println("Использование: update id");
            return true;
        }

        long id;
        try {
            id = Long.parseLong(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("id должен быть числом.");
            return true;
        }

        if (!ctx.cm.containsId(id)) {
            System.out.println("Элемента с id " + id + " не существует.");
            return true;
        }

        try {
            City updatedCity = input.readCityForUpdate(ctx.cm, id);
            boolean updated = ctx.cm.update(id, updatedCity);

            if (updated) {
                System.out.println("Элемент успешно обновлён.");
            } else {
                System.out.println("Не удалось обновить элемент.");
            }
        } catch (Exception e) {
            System.out.println("Ошибка ввода данных: " + e.getMessage());
        }

        return true;
    }
}