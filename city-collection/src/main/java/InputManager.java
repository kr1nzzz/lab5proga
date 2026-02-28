import java.time.LocalDateTime;

/**
 * Менеджер пользовательского ввода.
 * <p>
 * Считывает данные из {@link java.util.Scanner} и выполняет валидацию значений
 * согласно ограничениям полей моделей (City, Coordinates, Human).
 * Поддерживает интерактивный режим (с приглашениями) и режим выполнения скрипта.
 * </p>
 */
public class InputManager {

    private final java.util.Scanner scanner;
    private final boolean interactive;

    /**
     * Конструктор менеджера ввода.
     *
     * @param scanner     источник ввода
     * @param interactive true — интерактивный режим (печатать приглашения), false — режим скрипта
     */
    public InputManager(java.util.Scanner scanner, boolean interactive) {
        this.scanner = scanner;
        this.interactive = interactive;
    }

    /**
     * Считывает строку из scanner, печатая приглашение в интерактивном режиме.
     *
     * @param text приглашение
     * @return введённая строка или null, если ввода больше нет
     */
    private String prompt(String text) {
        if (interactive) System.out.print(text);
        if (!scanner.hasNextLine()) return null;
        return scanner.nextLine();
    }

    /**
     * Считывает непустую строку (после trim()).
     *
     * @param field имя поля для сообщений пользователю
     * @return непустая строка
     */
    private String readNonEmptyString(String field) {
        while (true) {
            String s = prompt("Введите " + field + ": ");
            if (s == null) throw new IllegalArgumentException("Нет ввода для " + field);
            s = s.trim();
            if (!s.isEmpty()) return s;
            System.out.println("Ошибка: строка не должна быть пустой.");
        }
    }

    /**
     * Считывает int с проверкой предиката.
     *
     * @param field     имя поля
     * @param predicate условие корректности (может быть null)
     * @param errMsg    сообщение при нарушении условия
     * @param allowNull разрешить null через пустую строку
     * @return Integer (если allowNull=true) или число
     */
    private Integer readInt(String field, java.util.function.IntPredicate predicate, String errMsg, boolean allowNull) {
        while (true) {
            String s = prompt("Введите " + field + (allowNull ? " (пусто = null)" : "") + ": ");
            if (s == null) throw new IllegalArgumentException("Нет ввода для " + field);
            s = s.trim();
            if (allowNull && s.isEmpty()) return null;

            try {
                int v = Integer.parseInt(s);
                if (predicate != null && !predicate.test(v)) {
                    System.out.println("Ошибка: " + errMsg);
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: нужно целое число.");
            }
        }
    }

    /**
     * Считывает Long с проверкой.
     */
    private Long readLongObj(String field, java.util.function.LongPredicate predicate, String errMsg, boolean allowNull) {
        while (true) {
            String s = prompt("Введите " + field + (allowNull ? " (пусто = null)" : "") + ": ");
            if (s == null) throw new IllegalArgumentException("Нет ввода для " + field);
            s = s.trim();
            if (allowNull && s.isEmpty()) return null;

            try {
                long v = Long.parseLong(s);
                if (predicate != null && !predicate.test(v)) {
                    System.out.println("Ошибка: " + errMsg);
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: нужно целое число (long).");
            }
        }
    }

    /**
     * Считывает long (примитив) с проверкой.
     */
    private long readLongPrim(String field, java.util.function.LongPredicate predicate, String errMsg) {
        while (true) {
            String s = prompt("Введите " + field + ": ");
            if (s == null) throw new IllegalArgumentException("Нет ввода для " + field);
            s = s.trim();

            try {
                long v = Long.parseLong(s);
                if (predicate != null && !predicate.test(v)) {
                    System.out.println("Ошибка: " + errMsg);
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: нужно целое число (long).");
            }
        }
    }

    /**
     * Считывает Double с проверкой.
     */
    private Double readDoubleObj(String field, java.util.function.DoublePredicate predicate, String errMsg, boolean allowNull) {
        while (true) {
            String s = prompt("Введите " + field + (allowNull ? " (пусто = null)" : "") + ": ");
            if (s == null) throw new IllegalArgumentException("Нет ввода для " + field);
            s = s.trim();
            if (allowNull && s.isEmpty()) return null;

            try {
                double v = Double.parseDouble(s);
                if (predicate != null && !predicate.test(v)) {
                    System.out.println("Ошибка: " + errMsg);
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: нужно число (double).");
            }
        }
    }

    /**
     * Считывает Float с проверкой.
     */
    private Float readFloatObj(String field, java.util.function.DoublePredicate predicate, String errMsg, boolean allowNull) {
        while (true) {
            String s = prompt("Введите " + field + (allowNull ? " (пусто = null)" : "") + ": ");
            if (s == null) throw new IllegalArgumentException("Нет ввода для " + field);
            s = s.trim();
            if (allowNull && s.isEmpty()) return null;

            try {
                float v = Float.parseFloat(s);
                if (predicate != null && !predicate.test(v)) {
                    System.out.println("Ошибка: " + errMsg);
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: нужно число (float).");
            }
        }
    }

    /**
     * Считывает enum-значение из списка доступных.
     */
    private <E extends Enum<E>> E readEnum(String field, Class<E> enumClass, boolean allowNull) {
        E[] vals = enumClass.getEnumConstants();

        while (true) {
            System.out.println("Возможные значения " + field + ":");
            for (E v : vals) System.out.println(" - " + v.name());

            String s = prompt("Введите " + field + (allowNull ? " (пусто = null)" : "") + ": ");
            if (s == null) throw new IllegalArgumentException("Нет ввода для " + field);
            s = s.trim();
            if (allowNull && s.isEmpty()) return null;

            try {
                return Enum.valueOf(enumClass, s);
            } catch (Exception e) {
                System.out.println("Ошибка: нужно одно из значений списка.");
            }
        }
    }

    /**
     * Считывает LocalDateTime в формате ISO (YYYY-MM-DDTHH:MM:SS).
     */
    private LocalDateTime readDateTime(String field, boolean allowNull) {
        while (true) {
            String s = prompt("Введите " + field + " в формате YYYY-MM-DDTHH:MM:SS" + (allowNull ? " (пусто = null)" : "") + ": ");
            if (s == null) throw new IllegalArgumentException("Нет ввода для " + field);
            s = s.trim();
            if (allowNull && s.isEmpty()) return null;

            try {
                return LocalDateTime.parse(s);
            } catch (Exception e) {
                System.out.println("Ошибка: неверный формат даты-времени.");
            }
        }
    }

    /**
     * Считывает поля нового города для команды add.
     *
     * @param cm менеджер коллекции (используется для генерации id)
     * @return заполненный объект {@link City}
     */
    public City readCityForAdd(CollectionManager cm) {
        City c = new City();
        c.setId(cm.generateId());               // auto
        c.setCreationDate(LocalDateTime.now()); // auto
        fillCityFields(c, true);
        return c;
    }

    /**
     * Считывает поля города для команды update.
     *
     * @param cm менеджер коллекции
     * @param id идентификатор обновляемого элемента
     * @return заполненный объект {@link City} с сохранённым id
     */
    public City readCityForUpdate(CollectionManager cm, long id) {
        City c = new City();
        c.setId(id);                            // сохраняем id
        c.setCreationDate(LocalDateTime.now()); // для простоты генерируем заново
        fillCityFields(c, true);
        return c;
    }

    /**
     * Считывает город для сравнения в команде remove_lower.
     *
     * @param cm менеджер коллекции (для генерации id)
     * @return объект City (используется только как pivot)
     */
    public City readCityForComparison(CollectionManager cm) {
        City c = new City();
        c.setId(cm.generateId());
        c.setCreationDate(LocalDateTime.now());
        fillCityFields(c, true);
        return c;
    }

    /**
     * Заполняет поля City с проверкой ограничений.
     *
     * @param c               город
     * @param allowGovernorNull разрешить governor = null
     */
    private void fillCityFields(City c, boolean allowGovernorNull) {
        c.setName(readNonEmptyString("name (не пустое)"));

        Coordinates coords = new Coordinates();
        int x = readInt("coordinates.x (int > -288)", v -> v > -288, "x должен быть > -288", false);
        Integer y = readInt("coordinates.y (Integer, <= 882)", v -> v <= 882, "y должен быть <= 882", false);
        coords.setX(x);
        coords.setY(y);
        c.setCoordinates(coords);

        Double area = readDoubleObj("area (Double > 0)", v -> v > 0, "area должен быть > 0", false);
        c.setArea(area);

        Long population = readLongObj("population (Long > 0)", v -> v > 0, "population должен быть > 0", false);
        c.setPopulation(population);

        long masl = readLongPrim("metersAboveSeaLevel (long)", null, "");
        c.setMetersAboveSeaLevel(masl);

        c.setClimate(readEnum("climate", Climate.class, false));
        c.setGovernment(readEnum("government", Government.class, false));
        c.setStandardOfLiving(readEnum("standardOfLiving", StandardOfLiving.class, true));

        if (allowGovernorNull) {
            String ans = prompt("Вводить governor? (y/N): ");
            if (ans != null && ans.trim().equalsIgnoreCase("y")) {
                c.setGovernor(readHuman());
            } else {
                c.setGovernor(null);
            }
        } else {
            c.setGovernor(readHuman());
        }
    }

    /**
     * Считывает объект Human (губернатора) с проверками.
     *
     * @return Human
     */
    private Human readHuman() {
        Human h = new Human();
        h.setName(readNonEmptyString("governor.name (не пустое)"));
        Float height = readFloatObj("governor.height (Float > 0)", v -> v > 0, "height должен быть > 0", false);
        h.setHeight(height);
        h.setBirthday(readDateTime("governor.birthday", true));
        return h;
    }

    /**
     * Считывает значение governor для команды remove_all_by_governor.
     *
     * @return объект {@link Human} или {@code null}, если выбран governor = null
     */
    public Human readGovernorValue() {
        String ans = prompt("Удалять по governor=null? (y/N): ");
        if (ans != null && ans.trim().equalsIgnoreCase("y")) return null;
        return readHuman();
    }
}