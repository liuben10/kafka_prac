import java.util.HashMap;
import java.util.Properties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class Producer {

    private final Properties props;
    private final KafkaProducer<String, String> producer;
    private static final String bootstrapServer = ":9092";

    Producer() {
        Properties props = new Properties();
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        this.props = props;
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        this.producer = producer;
    }


    public void publish(HashMap<String, Object> obj, String topic) throws JsonProcessingException {
        String msg = new ObjectMapper().writeValueAsString(obj);
        publishMessage(msg, topic);
    }
    public void publishMessage(String msg, String topic) {
        try {
            producer.send(new ProducerRecord<>(topic, msg));
        } catch (Exception e) {
            System.err.println(e.getMessage());
            producer.close();
        } finally {
            producer.flush();
        }

    }
}