import java.time.LocalDateTime;
import java.util.Objects;

public class Human implements Comparable<Human> {
    private String name; // not null, not empty
    private Float height; // >0
    private LocalDateTime birthday; // may be null

    @Override
    public int compareTo(Human o) {
        int c = this.name.compareToIgnoreCase(o.name);
        if (c != 0) return c;
        c = Float.compare(this.height, o.height);
        if (c != 0) return c;
        if (this.birthday == null && o.birthday == null) return 0;
        if (this.birthday == null) return -1;
        if (o.birthday == null) return 1;
        return this.birthday.compareTo(o.birthday);
    }

    @Override
    public String toString() {
        return "Human{" +
                "name='" + name + '\'' +
                ", height=" + height +
                ", birthday=" + birthday +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Human)) return false;
        Human human = (Human) o;
        return Objects.equals(name, human.name) &&
                Objects.equals(height, human.height) &&
                Objects.equals(birthday, human.birthday);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, height, birthday);
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Float getHeight() { return height; }
    public void setHeight(Float height) { this.height = height; }

    public LocalDateTime getBirthday() { return birthday; }
    public void setBirthday(LocalDateTime birthday) { this.birthday = birthday; }
}