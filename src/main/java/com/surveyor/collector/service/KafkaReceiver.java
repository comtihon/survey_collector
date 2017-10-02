package com.surveyor.collector.service;

import com.surveyor.collector.data.dto.QuestionDTO;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaReceiver {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaReceiver.class);

    @Autowired
    private StatisticsFormer statisticsFormer;

    @KafkaListener(topics = "${spring.kafka.topic}")
    public void receive(ConsumerRecord<String, QuestionDTO> consumerRecord) {
        LOGGER.debug("got answered question: '{}'", consumerRecord);
        statisticsFormer.write(consumerRecord.value());
    }
}
