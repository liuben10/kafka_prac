import java.io.IOException;
import java.util.HashMap;

public class Hello {

    public static void main(String...args) {
        try {
            Schema s = Loader.loadFromFile("test.json");
            Generator g = new Generator();
            Producer p = new Producer();
            while(true) {
                HashMap<String, Object> obj = g.generate(s);
                System.out.println(obj);
                p.publish(obj, "test");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}