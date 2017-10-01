# Statistics collector service [![Build Status](https://travis-ci.org/comtihon/survey_collector.svg?branch=master)](https://travis-ci.org/comtihon/survey_collector)
Gets answered questions from kafka and writes it mongodb.  
It doesn't write full statistics - it only increments answerId counter for every question.

## Run
Ensure that [MongoDb](https://www.mongodb.com/) and [Kafka](https://kafka.apache.org/) are accessible before running the service.  
Access urls are specified in application.properties for `spring.kafka.bootstrap-servers` for kafka and `spring.data.mongodb.uri` for mongodb.

### In docker

    sudo ./gradlew build buildDocker
    sudo docker run -p 8080:8080 -t com.surveyor.collector

### In OS

    ./gradlew bootRun
