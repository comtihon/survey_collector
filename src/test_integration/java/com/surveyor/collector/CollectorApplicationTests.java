package com.surveyor.collector;


import com.surveyor.collector.data.dto.QuestionDTO;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
public class CollectorApplicationTests {
    @ClassRule
    public static KafkaEmbedded embeddedKafka =
            new KafkaEmbedded(1, true, "answers");
    @Autowired
    KafkaConfiguration kafkaConfiguration;
    @Value("${spring.kafka.topic}")
    private String topic;
    @Value("${spring.data.mongodb.collection}")
    private String collection;
    private KafkaTemplate<String, QuestionDTO> template;
    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    private KafkaMessageListenerContainer<String, QuestionDTO> container;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() throws Exception {
        // create a Kafka producer factory
        ProducerFactory<String, QuestionDTO> producerFactory =
                kafkaConfiguration.producerFactory(embeddedKafka);

        // create a Kafka template
        template = new KafkaTemplate<>(producerFactory);
        template.setDefaultTopic("answers");
        DefaultKafkaConsumerFactory<String, QuestionDTO> consumerFactory =
                kafkaConfiguration.consumerFactory(embeddedKafka);
        ContainerProperties containerProperties = new ContainerProperties("answers"); //TODO ${spring.kafka.topic}

        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        container.setupMessageListener((MessageListener<String, QuestionDTO>) System.out::println);
        container.start();

        ContainerTestUtils.waitForAssignment(container, embeddedKafka.getPartitionsPerTopic());
    }

    @After
    public void tearDown() {
        container.stop();
    }

    /**
     * Send data to kafka, check in postgres.
     *
     * @throws InterruptedException sleep 1 sec (to wait for data)
     */
    @Test
    public void testCollect() throws InterruptedException {
        QuestionDTO questionDTO1 = new QuestionDTO("question1", "answer1");
        QuestionDTO questionDTO2 = new QuestionDTO("question1", "answer2");
        QuestionDTO questionDTO3 = new QuestionDTO("question3", "answer3");
        QuestionDTO questionDTO4 = new QuestionDTO("question1", "answer2");

        for (QuestionDTO q : new QuestionDTO[]{questionDTO1, questionDTO2, questionDTO3, questionDTO4})
            template.sendDefault(q);
        Thread.sleep(1000);
        Query query = new Query(Criteria.where("question_id").is("question1"));
        Map res = mongoTemplate.findOne(query, Map.class, collection);
        Assert.assertEquals("question1", res.get("question_id"));
        Assert.assertEquals(1, res.get("answer1"));
        Assert.assertEquals(2, res.get("answer2"));
        query = new Query(Criteria.where("question_id").is("question3"));
        res = mongoTemplate.findOne(query, Map.class, collection);
        Assert.assertEquals("question3", res.get("question_id"));
        Assert.assertEquals(1, res.get("answer3"));
    }

}
