import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

enum Type {
    INT,
    STRING,
    DOUBLE,
}

class RowSpec {
    @JsonProperty("name")
    String name;
    @JsonProperty("type")
    Type type;
    @JsonProperty("extra")
    Optional<HashMap<String, String>> extra;
}

class Schema {
    @JsonProperty("specs")
    List<RowSpec> specs;
}
class Loader {
    public static Schema loadFromFile(String filename) throws IOException {
        InputStream is = Loader.class.getResourceAsStream(filename);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        return mapper.readValue(is, Schema.class);
    }

    public static Schema loadFromString(String schema) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(schema, Schema.class);
    }
}