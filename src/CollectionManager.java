import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.stream.Collectors;

/**
 * Менеджер коллекции городов.
 * <p>
 * Хранит коллекцию типа {@link java.util.Vector} и предоставляет операции,
 * соответствующие командам пользователя (add, update, remove, clear и т.д.).
 * Также отвечает за сортировку по умолчанию и генерацию уникальных идентификаторов.
 * </p>
 */
public class CollectionManager {

    private final Vector<City> cities = new Vector<>();
    private final LocalDateTime initTime = LocalDateTime.now();
    private long nextId = 1;

    /**
     * Возвращает коллекцию (Vector) для чтения/вывода элементов.
     *
     * @return Vector городов
     */
    public Vector<City> getAll() {
        return cities;
    }

    /**
     * @return количество элементов в коллекции
     */
    public int size() {
        return cities.size();
    }

    /**
     * Формирует информацию о коллекции (тип, время инициализации, размер).
     *
     * @return строка с информацией о коллекции
     */
    public String info() {
        return "Тип коллекции: " + cities.getClass().getName() + "\n" +
                "Дата инициализации: " + initTime + "\n" +
                "Количество элементов: " + cities.size();
    }

    /**
     * Очищает коллекцию.
     */
    public void clear() {
        cities.clear();
    }

    /**
     * Генерирует новый уникальный идентификатор для города.
     *
     * @return уникальный id (> 0)
     */
    public synchronized long generateId() {
        return nextId++;
    }

    /**
     * Синхронизирует счётчик id после загрузки из файла.
     * <p>
     * Устанавливает следующий выдаваемый id как (maxId + 1),
     * чтобы не возникало конфликтов с уже существующими элементами.
     * </p>
     */
    public void syncNextIdFromLoadedData() {
        long max = 0;
        for (City c : cities) {
            if (c.getId() > max) max = c.getId();
        }
        nextId = Math.max(1, max + 1);
    }

    /**
     * Добавляет новый город в коллекцию и сортирует коллекцию по умолчанию.
     *
     * @param city добавляемый объект {@link City}
     * @throws IllegalArgumentException если {@code city == null}
     */
    public void add(City city) {
        if (city == null) throw new IllegalArgumentException("Нельзя добавить null.");

        if (containsId(city.getId())) {
            city.setId(generateId());
        }

        cities.add(city);
        sortDefault();
    }

    /**
     * Обновляет город по заданному id.
     *
     * @param id      идентификатор обновляемого элемента
     * @param newCity новое значение элемента
     * @return {@code true}, если элемент найден и обновлён, иначе {@code false}
     */
    public boolean update(long id, City newCity) {
        for (int i = 0; i < cities.size(); i++) {
            City old = cities.get(i);
            if (old.getId() == id) {
                newCity.setId(id);

                if (newCity.getCreationDate() == null) {
                    newCity.setCreationDate(LocalDateTime.now());
                }

                cities.set(i, newCity);
                sortDefault();
                return true;
            }
        }
        return false;
    }

    /**
     * Удаляет город по id.
     *
     * @param id идентификатор удаляемого элемента
     * @return {@code true}, если элемент был удалён, иначе {@code false}
     */
    public boolean removeById(long id) {
        Iterator<City> it = cities.iterator();
        while (it.hasNext()) {
            City c = it.next();
            if (c.getId() == id) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    /**
     * Переворачивает текущий порядок элементов коллекции.
     */
    public void reorder() {
        Collections.reverse(cities);
    }

    /**
     * Удаляет все элементы, которые меньше заданного (по compareTo в City).
     *
     * @param pivot опорный элемент для сравнения
     * @return количество удалённых элементов
     */
    public int removeLower(City pivot) {
        if (pivot == null) return 0;
        int before = cities.size();
        cities.removeIf(c -> c.compareTo(pivot) < 0);
        return before - cities.size();
    }

    /**
     * Удаляет все города, у которых governor равен заданному (или null).
     *
     * @param gov губернатор (может быть null)
     * @return количество удалённых элементов
     */
    public int removeAllByGovernor(Human gov) {
        int before = cities.size();
        cities.removeIf(c -> Objects.equals(c.getGovernor(), gov));
        return before - cities.size();
    }

    /**
     * Возвращает любой город с минимальным значением climate.
     *
     * @return город с минимальным climate или {@code null}, если коллекция пуста
     */
    public City minByClimate() {
        if (cities.isEmpty()) return null;

        City min = null;
        for (City c : cities) {
            if (min == null || c.getClimate().ordinal() < min.getClimate().ordinal()) {
                min = c;
            }
        }
        return min;
    }

    /**
     * Выводит значения поля governor всех элементов в порядке возрастания.
     * Если список пуст (нет governor или коллекция пуста) — выводит сообщение.
     */
    public void printFieldAscendingGovernor() {
        List<Human> list = cities.stream()
                .map(City::getGovernor)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());

        if (list.isEmpty()) {
            System.out.println("Нет значений governor (все null или коллекция пуста).");
            return;
        }

        for (Human h : list) {
            System.out.println(h);
        }
    }

    /**
     * Сортировка по умолчанию (требование задания).
     * City implements Comparable<City>.
     */
    public void sortDefault() {
        Collections.sort(cities);
    }

    /**
     * Проверяет наличие id в коллекции.
     *
     * @param id проверяемый id
     * @return true если такой id уже есть, иначе false
     */
    private boolean containsId(long id) {
        for (City c : cities) {
            if (c.getId() == id) return true;
        }
        return false;
    }
}