import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Scanner;

public class XmlIO {
    private final String filePath;

    public XmlIO(String filePath) {
        this.filePath = filePath;
    }

    public void loadInto(CollectionManager cm) throws Exception {
        File f = new File(filePath);
        if (!f.exists()) {
            cm.clear();
            return;
        }
        if (!f.canRead()) throw new IllegalArgumentException("Нет прав на чтение файла.");

        String xml;
        try (Scanner sc = new Scanner(f, StandardCharsets.UTF_8)) {
            sc.useDelimiter("\\A"); // whole file
            xml = sc.hasNext() ? sc.next() : "";
        }

        cm.clear();

        int pos = 0;
        while (true) {
            int start = xml.indexOf("<city>", pos);
            if (start < 0) break;
            int end = xml.indexOf("</city>", start);
            if (end < 0) break;

            String block = xml.substring(start + "<city>".length(), end).trim();
            pos = end + "</city>".length();

            try {
                City c = parseCity(block, cm);
                if (c != null) cm.getAll().add(c);
            } catch (Exception e) {
                // skip invalid city
                System.out.println("Пропуск элемента из файла (ошибка): " + e.getMessage());
            }
        }

        cm.sortDefault();
        cm.syncNextIdFromLoadedData();
    }

    public void saveFrom(CollectionManager cm) throws Exception {
        File f = new File(filePath);
        if (f.exists() && !f.canWrite()) throw new IllegalArgumentException("Нет прав на запись файла.");

        try (FileWriter fw = new FileWriter(f, StandardCharsets.UTF_8)) {
            fw.write("<cities>\n");
            for (City c : cm.getAll()) {
                fw.write("  <city>\n");
                fw.write(tag("id", String.valueOf(c.getId())));
                fw.write(tag("name", escape(c.getName())));
                fw.write("    <coordinates>\n");
                fw.write(tag("x", String.valueOf(c.getCoordinates().getX())));
                fw.write(tag("y", String.valueOf(c.getCoordinates().getY())));
                fw.write("    </coordinates>\n");
                fw.write(tag("creationDate", c.getCreationDate().toString()));
                fw.write(tag("area", String.valueOf(c.getArea())));
                fw.write(tag("population", String.valueOf(c.getPopulation())));
                fw.write(tag("metersAboveSeaLevel", String.valueOf(c.getMetersAboveSeaLevel())));
                fw.write(tag("climate", c.getClimate().name()));
                fw.write(tag("government", c.getGovernment().name()));
                fw.write(tag("standardOfLiving", c.getStandardOfLiving() == null ? "" : c.getStandardOfLiving().name()));
                if (c.getGovernor() == null) {
                    fw.write("    <governor></governor>\n");
                } else {
                    fw.write("    <governor>\n");
                    fw.write(tag("name", escape(c.getGovernor().getName())));
                    fw.write(tag("height", String.valueOf(c.getGovernor().getHeight())));
                    fw.write(tag("birthday", c.getGovernor().getBirthday() == null ? "" : c.getGovernor().getBirthday().toString()));
                    fw.write("    </governor>\n");
                }
                fw.write("  </city>\n");
            }
            fw.write("</cities>\n");
        }
    }

    private City parseCity(String block, CollectionManager cm) {
        City c = new City();

        long id = Long.parseLong(text(block, "id"));
        if (id <= 0) throw new IllegalArgumentException("id должен быть > 0");
        c.setId(id);

        String name = unescape(text(block, "name"));
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("name пустой");
        c.setName(name.trim());

        String coordsBlock = inner(block, "coordinates");
        Coordinates coords = new Coordinates();
        int x = Integer.parseInt(text(coordsBlock, "x"));
        if (x <= -288) throw new IllegalArgumentException("coordinates.x должен быть > -288");
        Integer y = Integer.parseInt(text(coordsBlock, "y"));
        if (y > 882) throw new IllegalArgumentException("coordinates.y должен быть <= 882");
        coords.setX(x);
        coords.setY(y);
        c.setCoordinates(coords);

        String cd = text(block, "creationDate");
        LocalDateTime creationDate = (cd == null || cd.isEmpty()) ? LocalDateTime.now() : LocalDateTime.parse(cd);
        c.setCreationDate(creationDate);

        double area = Double.parseDouble(text(block, "area"));
        if (area <= 0) throw new IllegalArgumentException("area должен быть > 0");
        c.setArea(area);

        long population = Long.parseLong(text(block, "population"));
        if (population <= 0) throw new IllegalArgumentException("population должен быть > 0");
        c.setPopulation(population);

        long masl = Long.parseLong(text(block, "metersAboveSeaLevel"));
        c.setMetersAboveSeaLevel(masl);

        c.setClimate(Climate.valueOf(text(block, "climate")));
        c.setGovernment(Government.valueOf(text(block, "government")));

        String sol = text(block, "standardOfLiving");
        if (sol == null || sol.trim().isEmpty()) c.setStandardOfLiving(null);
        else c.setStandardOfLiving(StandardOfLiving.valueOf(sol.trim()));

        // governor
        String govBlock = inner(block, "governor");
        if (govBlock == null || govBlock.trim().isEmpty()) {
            c.setGovernor(null);
        } else {
            Human h = new Human();
            String gname = unescape(text(govBlock, "name"));
            if (gname == null || gname.trim().isEmpty()) throw new IllegalArgumentException("governor.name пустой");
            h.setName(gname.trim());

            float height = Float.parseFloat(text(govBlock, "height"));
            if (height <= 0) throw new IllegalArgumentException("governor.height должен быть > 0");
            h.setHeight(height);

            String b = text(govBlock, "birthday");
            if (b == null || b.trim().isEmpty()) h.setBirthday(null);
            else h.setBirthday(LocalDateTime.parse(b.trim()));

            c.setGovernor(h);
        }

        return c;
    }

    private static String tag(String name, String value) {
        if (value == null) value = "";
        return "    <" + name + ">" + value + "</" + name + ">\n";
    }


    private static String text(String src, String tag) {
        if (src == null) return null;
        String open = "<" + tag + ">";
        String close = "</" + tag + ">";
        int a = src.indexOf(open);
        int b = src.indexOf(close);
        if (a < 0 || b < 0 || b < a) return null;
        return src.substring(a + open.length(), b).trim();
    }


    private static String inner(String src, String tag) {
        if (src == null) return null;
        String open = "<" + tag + ">";
        String close = "</" + tag + ">";
        int a = src.indexOf(open);
        int b = src.indexOf(close);
        if (a < 0 || b < 0 || b < a) return null;
        return src.substring(a + open.length(), b);
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private static String unescape(String s) {
        if (s == null) return null;
        return s.replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&");
    }
}