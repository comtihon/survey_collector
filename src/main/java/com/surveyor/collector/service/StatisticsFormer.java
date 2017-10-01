package com.surveyor.collector.service;

import com.surveyor.collector.config.ServerConfig;
import com.surveyor.collector.data.dto.QuestionDTO;
import com.surveyor.collector.data.entity.Question;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class StatisticsFormer {

    static final Logger logger = LoggerFactory.getLogger(StatisticsFormer.class);

    @Autowired
    private ServerConfig serverConfig;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ModelMapper modelMapper;

    @Async
    public void write(QuestionDTO questionDTO) {
        logger.debug("save {}", questionDTO);
        if (questionDTO != null) {
            Question question = modelMapper.map(questionDTO, Question.class);
            addStatistics(question);
        }
    }

    /**
     * Increment anwerId by 1 for a given questionId
     *
     * @param question answered
     */
    private void addStatistics(Question question) {
        Query query = new Query(Criteria.where("question_id").is(question.getQuestionId()));
        Update update = new Update().inc(question.getAnswerId(), 1);
        mongoTemplate.upsert(query, update, serverConfig.getCollection());
    }
}
