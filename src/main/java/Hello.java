import java.io.IOException;

public class Hello {

    public static void main(String...args) {
        try {
            Schema s = Loader.loadFromFile("test.json");
            Generator g = new Generator();
            System.out.println(g.generate(s));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}