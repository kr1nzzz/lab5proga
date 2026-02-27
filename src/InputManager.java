import java.time.LocalDateTime;

/**
 * Handles reading fields from Scanner with validation.
 * Works both interactive and script mode.
 */
public class InputManager {
    private final java.util.Scanner scanner;
    private final boolean interactive;

    public InputManager(java.util.Scanner scanner, boolean interactive) {
        this.scanner = scanner;
        this.interactive = interactive;
    }

    private String prompt(String text) {
        if (interactive) System.out.print(text);
        if (!scanner.hasNextLine()) return null;
        return scanner.nextLine();
    }

    private String readNonEmptyString(String field) {
        while (true) {
            String s = prompt("Введите " + field + ": ");
            if (s == null) throw new IllegalArgumentException("Нет ввода для " + field);
            s = s.trim();
            if (!s.isEmpty()) return s;
            System.out.println("Ошибка: строка не должна быть пустой.");
        }
    }

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

    public City readCityForAdd(CollectionManager cm) {
        City c = new City();
        c.setId(cm.generateId());                 // auto
        c.setCreationDate(LocalDateTime.now());   // auto

        fillCityFields(c, true);
        return c;
    }

    public City readCityForUpdate(CollectionManager cm, long id) {
        City c = new City();
        c.setId(id);                              // keep id
        c.setCreationDate(LocalDateTime.now());    // simple: regenerate on update (можно иначе, но так проще)

        fillCityFields(c, true);
        return c;
    }

    /**
     * For remove_lower: we only need a comparable City.
     * We'll generate id/creationDate automatically.
     */
    public City readCityForComparison(CollectionManager cm) {
        City c = new City();
        c.setId(cm.generateId());
        c.setCreationDate(LocalDateTime.now());
        fillCityFields(c, true);
        return c;
    }

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

        // governor can be null (empty line)
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

    private Human readHuman() {
        Human h = new Human();
        h.setName(readNonEmptyString("governor.name (не пустое)"));
        Float height = readFloatObj("governor.height (Float > 0)", v -> v > 0, "height должен быть > 0", false);
        h.setHeight(height);
        h.setBirthday(readDateTime("governor.birthday", true));
        return h;
    }

    /**
     * For remove_all_by_governor: user enters governor object or null.
     */
    public Human readGovernorValue() {
        String ans = prompt("Удалять по governor=null? (y/N): ");
        if (ans != null && ans.trim().equalsIgnoreCase("y")) return null;
        return readHuman();
    }
}
