/**
 * Координаты города.
 * <p>
 * Используется как составное поле класса {@link City}.
 * Ограничения значений проверяются на этапе ввода/загрузки.
 * </p>
 */
public class Coordinates {

    private int x;        // > -288
    private Integer y;    // not null, <= 882

    public int getX() { return x; }

    public void setX(int x) { this.x = x; }

    public Integer getY() { return y; }

    public void setY(Integer y) { this.y = y; }

    @Override
    public String toString() {
        return "Coordinates{x=" + x + ", y=" + y + "}";
    }
}