import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class CollectionManager {
    private final Vector<City> cities = new Vector<>();
    private final LocalDateTime initTime = LocalDateTime.now();
    private long nextId = 1;

    public Vector<City> getAll() {
        return cities;
    }

    public int size() {
        return cities.size();
    }

    public String info() {
        return "Тип: " + cities.getClass().getName() + "\n" +
                "Дата инициализации: " + initTime + "\n" +
                "Количество элементов: " + cities.size();
    }

    public void clear() {
        cities.clear();
    }

    public synchronized long generateId() {
        return nextId++;
    }

    public void syncNextIdFromLoadedData() {
        long max = 0;
        for (City c : cities) max = Math.max(max, c.getId());
        nextId = Math.max(1, max + 1);
    }

    public void add(City city) {
        if (city == null) throw new IllegalArgumentException("city=null");
        // ensure unique id
        if (containsId(city.getId())) {
            city.setId(generateId());
        }
        cities.add(city);
        sortDefault();
    }

    public boolean update(long id, City newCity) {
        for (int i = 0; i < cities.size(); i++) {
            if (cities.get(i).getId() == id) {
                newCity.setId(id);
                if (newCity.getCreationDate() == null) newCity.setCreationDate(LocalDateTime.now());
                cities.set(i, newCity);
                sortDefault();
                return true;
            }
        }
        return false;
    }

    public boolean removeById(long id) {
        Iterator<City> it = cities.iterator();
        while (it.hasNext()) {
            if (it.next().getId() == id) {
                it.remove();
                return true;
            }
        }
        return false;
    }

    public int removeLower(City pivot) {
        if (pivot == null) return 0;
        int before = cities.size();
        cities.removeIf(c -> c.compareTo(pivot) < 0);
        return before - cities.size();
    }

    public void reorder() {
        Collections.reverse(cities);
    }

    public int removeAllByGovernor(Human gov) {
        int before = cities.size();
        cities.removeIf(c -> Objects.equals(c.getGovernor(), gov));
        return before - cities.size();
    }

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

    public void printFieldAscendingGovernor() {
        List<Human> govs = cities.stream()
                .map(City::getGovernor)
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());

        if (govs.isEmpty()) {
            System.out.println("Нет значений governor (все null или коллекция пуста).");
            return;
        }

        for (Human h : govs) System.out.println(h);
    }

    public void sortDefault() {
        Collections.sort(cities);
    }

    private boolean containsId(long id) {
        for (City c : cities) if (c.getId() == id) return true;
        return false;
    }
}