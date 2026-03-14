import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

/**
 * Управляет коллекцией объектов {@link City}.
 * <p>
 * Отвечает за хранение элементов, генерацию идентификаторов,
 * добавление, обновление, удаление, сортировку и выполнение
 * операций над коллекцией.
 * </p>
 *
 * <p>Коллекция хранится в виде {@code Vector<City>}.</p>
 */
public class CollectionManager {
    /**
     * Коллекция городов.
     */
    private final Vector<City> cities = new Vector<>();

    /**
     * Время инициализации менеджера коллекции.
     */
    private final LocalDateTime initTime = LocalDateTime.now();

    /**
     * Следующий идентификатор, который будет выдан новому элементу.
     */
    private long nextId = 1;

    /**
     * Возвращает все элементы коллекции.
     *
     * @return коллекция городов
     */
    public Vector<City> getAll() {
        return cities;
    }

    /**
     * Возвращает количество элементов в коллекции.
     *
     * @return размер коллекции
     */
    public int size() {
        return cities.size();
    }

    /**
     * Возвращает информацию о коллекции.
     *
     * @return строка с информацией о типе коллекции, времени инициализации и количестве элементов
     */
    public String info() {
        return "Тип коллекции: " + cities.getClass().getName()
                + "\nВремя инициализации: " + initTime
                + "\nКоличество элементов: " + cities.size();
    }

    /**
     * Очищает коллекцию.
     */
    public void clear() {
        cities.clear();
    }

    /**
     * Генерирует новый уникальный идентификатор.
     *
     * @return новый идентификатор
     */
    public long generateId() {
        return nextId++;
    }

    /**
     * Синхронизирует следующее значение идентификатора
     * на основе уже загруженных из файла данных.
     */
    public void syncNextIdFromLoadedData() {
        long maxId = 0;
        for (City city : cities) {
            if (city.getId() > maxId) {
                maxId = city.getId();
            }
        }
        nextId = maxId + 1;
    }

    /**
     * Добавляет новый элемент в коллекцию.
     * <p>
     * Если у объекта ещё не установлен корректный идентификатор,
     * он будет сгенерирован автоматически.
     * </p>
     *
     * @param city добавляемый город
     */
    public void add(City city) {
        if (city.getId() <= 0 || containsId(city.getId())) {
            city.setId(generateId());
        }
        if (city.getCreationDate() == null) {
            city.setCreationDate(LocalDateTime.now());
        }
        cities.add(city);
    }

    /**
     * Проверяет, существует ли элемент с указанным идентификатором.
     *
     * @param id идентификатор элемента
     * @return {@code true}, если элемент найден, иначе {@code false}
     */
    public boolean containsId(long id) {
        for (City city : cities) {
            if (city.getId() == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Обновляет элемент коллекции по идентификатору.
     *
     * @param id идентификатор обновляемого элемента
     * @param newCity новый объект города
     * @return {@code true}, если элемент найден и обновлён, иначе {@code false}
     */
    public boolean update(long id, City newCity) {
        for (int i = 0; i < cities.size(); i++) {
            if (cities.get(i).getId() == id) {
                newCity.setId(id);

                if (newCity.getCreationDate() == null) {
                    newCity.setCreationDate(cities.get(i).getCreationDate());
                }

                cities.set(i, newCity);
                return true;
            }
        }
        return false;
    }

    /**
     * Удаляет элемент из коллекции по идентификатору.
     *
     * @param id идентификатор удаляемого элемента
     * @return {@code true}, если элемент найден и удалён, иначе {@code false}
     */
    public boolean removeById(long id) {
        for (int i = 0; i < cities.size(); i++) {
            if (cities.get(i).getId() == id) {
                cities.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Разворачивает порядок элементов в коллекции.
     */
    public void reorder() {
        Collections.reverse(cities);
    }

    /**
     * Удаляет из коллекции все элементы, меньшие заданного.
     * <p>
     * Сравнение выполняется в соответствии с естественным порядком объектов {@link City}.
     * </p>
     *
     * @param pivot элемент, относительно которого выполняется сравнение
     * @return количество удалённых элементов
     */
    public int removeLower(City pivot) {
        int before = cities.size();
        cities.removeIf(city -> city.compareTo(pivot) < 0);
        return before - cities.size();
    }

    /**
     * Удаляет из коллекции все элементы, значение поля governor которых равно заданному.
     *
     * @param gov значение губернатора для сравнения
     * @return количество удалённых элементов
     */
    public int removeAllByGovernor(Human gov) {
        int before = cities.size();
        cities.removeIf(city -> {
            Human currentGovernor = city.getGovernor();
            if (currentGovernor == null && gov == null) {
                return true;
            }
            if (currentGovernor == null || gov == null) {
                return false;
            }
            return currentGovernor.equals(gov);
        });
        return before - cities.size();
    }

    /**
     * Возвращает элемент с минимальным значением поля climate.
     *
     * @return город с минимальным климатом или {@code null}, если коллекция пуста
     */
    public City minByClimate() {
        if (cities.isEmpty()) {
            return null;
        }

        City min = null;
        for (City city : cities) {
            if (city.getClimate() == null) {
                continue;
            }
            if (min == null || city.getClimate().compareTo(min.getClimate()) < 0) {
                min = city;
            }
        }
        return min;
    }

    /**
     * Выводит значения поля governor элементов коллекции
     * в порядке возрастания.
     */
    public void printFieldAscendingGovernor() {
        Vector<Human> governors = new Vector<>();

        for (City city : cities) {
            if (city.getGovernor() != null) {
                governors.add(city.getGovernor());
            }
        }

        governors.sort(Comparator.naturalOrder());

        for (Human governor : governors) {
            System.out.println(governor);
        }
    }

    /**
     * Сортирует коллекцию в естественном порядке.
     * <p>
     * Для сравнения используется реализация {@link Comparable} в классе {@link City}.
     * </p>
     */
    public void sortDefault() {
        Collections.sort(cities);
    }
}