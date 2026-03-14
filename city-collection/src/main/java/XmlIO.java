import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Выполняет загрузку коллекции из XML-файла и сохранение коллекции в XML-файл.
 * <p>
 * Класс реализует простую ручную сериализацию и десериализацию объектов {@link City}
 * без использования сторонних библиотек.
 * </p>
 *
 * <p>
 * При загрузке XML-файл читается целиком, затем из него извлекаются блоки {@code <city>...</city>},
 * после чего каждый блок преобразуется в объект {@link City}.
 * </p>
 */
public class XmlIO {
    /**
     * Путь к XML-файлу.
     */
    private final String filePath;

    /**
     * Создаёт объект для работы с указанным XML-файлом.
     *
     * @param filePath путь к XML-файлу
     */
    public XmlIO(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Загружает данные из XML-файла в коллекцию.
     * <p>
     * Перед загрузкой текущая коллекция очищается.
     * Если файл не существует, не является обычным файлом, недоступен для чтения,
     * пуст или не содержит ни одного элемента {@code <city>}, выбрасывается исключение.
     * </p>
     *
     * @param cm менеджер коллекции, в который будут загружены данные
     * @throws Exception если произошла ошибка чтения файла или разбора XML
     */
    public void loadInto(CollectionManager cm) throws Exception {
        File f = new File(filePath);

        if (!f.exists()) {
            throw new IllegalArgumentException("Файл не найден: " + f.getAbsolutePath());
        }

        if (!f.isFile()) {
            throw new IllegalArgumentException("Указанный путь не является файлом: " + f.getAbsolutePath());
        }

        if (!f.canRead()) {
            throw new IllegalArgumentException("Файл нельзя прочитать: " + f.getAbsolutePath());
        }

        String xml = Files.readString(f.toPath(), StandardCharsets.UTF_8).trim();

        if (xml.isEmpty()) {
            throw new IllegalArgumentException("XML-файл пустой.");
        }

        cm.clear();

        Pattern cityPattern = Pattern.compile("<city>(.*?)</city>", Pattern.DOTALL);
        Matcher matcher = cityPattern.matcher(xml);

        int loaded = 0;
        while (matcher.find()) {
            String block = matcher.group(1).trim();
            City city = parseCity(block);
            cm.add(city);
            loaded++;
        }

        cm.syncNextIdFromLoadedData();

        if (loaded == 0) {
            throw new IllegalArgumentException("В XML не найдено ни одного элемента <city>.");
        }
    }

    /**
     * Сохраняет коллекцию в XML-файл.
     *
     * @param cm менеджер коллекции, данные которого нужно сохранить
     * @throws Exception если произошла ошибка записи в файл
     */
    public void saveFrom(CollectionManager cm) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("<cities>\n");

        for (City city : cm.getAll()) {
            sb.append("  <city>\n");
            sb.append("    ").append(tag("id", String.valueOf(city.getId()))).append("\n");
            sb.append("    ").append(tag("name", city.getName())).append("\n");

            sb.append("    <coordinates>\n");
            sb.append("      ").append(tag("x", String.valueOf(city.getCoordinates().getX()))).append("\n");
            sb.append("      ").append(tag("y", String.valueOf(city.getCoordinates().getY()))).append("\n");
            sb.append("    </coordinates>\n");

            sb.append("    ").append(tag("creationDate", String.valueOf(city.getCreationDate()))).append("\n");
            sb.append("    ").append(tag("area", String.valueOf(city.getArea()))).append("\n");
            sb.append("    ").append(tag("population", String.valueOf(city.getPopulation()))).append("\n");
            sb.append("    ").append(tag("metersAboveSeaLevel", String.valueOf(city.getMetersAboveSeaLevel()))).append("\n");
            sb.append("    ").append(tag("climate", String.valueOf(city.getClimate()))).append("\n");
            sb.append("    ").append(tag("government", String.valueOf(city.getGovernment()))).append("\n");
            sb.append("    ").append(tag("standardOfLiving", String.valueOf(city.getStandardOfLiving()))).append("\n");

            if (city.getGovernor() != null) {
                sb.append("    <governor>\n");
                sb.append("      ").append(tag("name", city.getGovernor().getName())).append("\n");
                sb.append("      ").append(tag("height", String.valueOf(city.getGovernor().getHeight()))).append("\n");
                sb.append("      ").append(tag("birthday", String.valueOf(city.getGovernor().getBirthday()))).append("\n");
                sb.append("    </governor>\n");
            }

            sb.append("  </city>\n");
        }

        sb.append("</cities>\n");

        Files.writeString(new File(filePath).toPath(), sb.toString(), StandardCharsets.UTF_8);
    }

    /**
     * Преобразует XML-блок одного города в объект {@link City}.
     *
     * @param block XML-фрагмент, содержащий данные одного города
     * @return объект города, созданный на основе XML-данных
     * @throws Exception если обязательные данные отсутствуют или не могут быть преобразованы
     */
    private City parseCity(String block) throws Exception {
        City city = new City();

        city.setId(Long.parseLong(text(block, "id")));
        city.setName(text(block, "name"));

        String coordinatesBlock = inner(block, "coordinates");
        Coordinates coordinates = new Coordinates();
        coordinates.setX(Integer.parseInt(text(coordinatesBlock, "x")));

        String yText = text(coordinatesBlock, "y");
        coordinates.setY(yText == null || yText.isBlank() ? null : Integer.valueOf(yText));

        city.setCoordinates(coordinates);

        String creationDateText = text(block, "creationDate");
        city.setCreationDate(
                creationDateText == null || creationDateText.isBlank()
                        ? LocalDateTime.now()
                        : LocalDateTime.parse(creationDateText)
        );

        String areaText = text(block, "area");
        city.setArea(areaText == null || areaText.isBlank() ? null : Double.valueOf(areaText));

        String populationText = text(block, "population");
        city.setPopulation(populationText == null || populationText.isBlank() ? null : Long.valueOf(populationText));

        city.setMetersAboveSeaLevel(Long.parseLong(text(block, "metersAboveSeaLevel")));

        String climateText = text(block, "climate");
        city.setClimate(climateText == null || climateText.isBlank() ? null : Climate.valueOf(climateText));

        String governmentText = text(block, "government");
        city.setGovernment(governmentText == null || governmentText.isBlank() ? null : Government.valueOf(governmentText));

        String standardOfLivingText = text(block, "standardOfLiving");
        city.setStandardOfLiving(
                standardOfLivingText == null || standardOfLivingText.isBlank()
                        ? null
                        : StandardOfLiving.valueOf(standardOfLivingText)
        );

        String governorBlock = inner(block, "governor");
        if (governorBlock != null && !governorBlock.isBlank()) {
            Human governor = new Human();
            governor.setName(text(governorBlock, "name"));

            String heightText = text(governorBlock, "height");
            governor.setHeight(heightText == null || heightText.isBlank() ? null : Float.valueOf(heightText));

            String birthdayText = text(governorBlock, "birthday");
            governor.setBirthday(
                    birthdayText == null || birthdayText.isBlank()
                            ? null
                            : LocalDateTime.parse(birthdayText)
            );

            city.setGovernor(governor);
        }

        return city;
    }

    /**
     * Формирует XML-тег с указанным именем и значением.
     *
     * @param name имя XML-тега
     * @param value значение внутри тега
     * @return строка вида {@code <name>value</name>}
     */
    private static String tag(String name, String value) {
        return "<" + name + ">" + escape(value) + "</" + name + ">";
    }

    /**
     * Извлекает текстовое содержимое первого найденного XML-тега.
     *
     * @param src исходная строка, содержащая XML
     * @param tag имя XML-тега
     * @return текст внутри тега или {@code null}, если тег не найден
     */
    private static String text(String src, String tag) {
        if (src == null) {
            return null;
        }

        Pattern p = Pattern.compile("<" + tag + ">(.*?)</" + tag + ">", Pattern.DOTALL);
        Matcher m = p.matcher(src);

        if (!m.find()) {
            return null;
        }

        return unescape(m.group(1).trim());
    }

    /**
     * Извлекает внутреннее содержимое первого найденного XML-тега,
     * включая вложенные теги.
     *
     * @param src исходная строка, содержащая XML
     * @param tag имя XML-тега
     * @return содержимое внутри тега или {@code null}, если тег не найден
     */
    private static String inner(String src, String tag) {
        if (src == null) {
            return null;
        }

        Pattern p = Pattern.compile("<" + tag + ">(.*?)</" + tag + ">", Pattern.DOTALL);
        Matcher m = p.matcher(src);

        if (!m.find()) {
            return null;
        }

        return m.group(1).trim();
    }

    /**
     * Экранирует специальные символы для безопасной записи в XML.
     *
     * @param s исходная строка
     * @return строка с заменёнными XML-спецсимволами
     */
    private static String escape(String s) {
        if (s == null) {
            return "";
        }

        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    /**
     * Выполняет обратное преобразование XML-сущностей в обычные символы.
     *
     * @param s строка с XML-сущностями
     * @return строка после декодирования XML-сущностей
     */
    private static String unescape(String s) {
        if (s == null) {
            return null;
        }

        return s.replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&");
    }
}