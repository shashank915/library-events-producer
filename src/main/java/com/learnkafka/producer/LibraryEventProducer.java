package com.learnkafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.domain.LibraryEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
@Slf4j
public class LibraryEventProducer {

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void produceLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {
        String key = libraryEvent.getEventId();
        String message = objectMapper.writeValueAsString(libraryEvent);
        ListenableFuture<SendResult<String, String>> sendResultListenableFuture =
                kafkaTemplate.sendDefault(key, message);

        sendResultListenableFuture.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                handleFailure(key, message, throwable);
            }

            @Override
            public void onSuccess(SendResult<String, String> sendResult) {
                handleSuccess(key, message, sendResult);
            }
        });

    }

    private void handleSuccess(String key, String message, SendResult<String, String> sendResult) {
        log.info("Event published successfully with key {}, value {} and partition {}.", key, message, sendResult.getRecordMetadata().partition());
    }

    private void handleFailure(String key, String message, Throwable throwable) {
        log.error("Event could not be produced successfully!! The exception is {}", throwable.getMessage());

        try {
            throw throwable;
        } catch (Throwable e) {
            log.error("Error while producing library-event: {}", e.getMessage());
        }
    }
}
