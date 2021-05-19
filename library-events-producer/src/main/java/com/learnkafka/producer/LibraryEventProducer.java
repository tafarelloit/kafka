package com.learnkafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.domain.LibraryEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
public class LibraryEventProducer {

    @Autowired
    KafkaTemplate<Integer, String> kafkaTemplate;

    String topic = "library-events";

    @Autowired
    ObjectMapper objectMapper;

    //Asynchronous Call
    public void sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {

        Integer key = libraryEvent.getLibraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);

        ListenableFuture<SendResult<Integer, String>> listenableFuture = kafkaTemplate.sendDefault(key, value);

        listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                handleFailure(key, value, ex);
            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                handleSuccess(key, value, result);
            }
        });
    }

    //Asynchronous Call with Producer Record
    public ListenableFuture<SendResult<Integer, String>> sendLibraryEventWithProducerRecord(LibraryEvent libraryEvent) throws JsonProcessingException {
        Integer key = libraryEvent.getLibraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);

        log.info("value "+value);
        log.info("key "+String.valueOf(key));

        ProducerRecord<Integer, String> producerRecord = buildProducerRecord(key, value, topic);

        ListenableFuture<SendResult<Integer, String>> listenableFuture = kafkaTemplate.send(producerRecord);

        listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                handleFailure(key, value, ex);
            }

            @Override
            public void onSuccess(SendResult<Integer, String> result) {
                handleSuccess(key, value, result);
            }
        });

        return listenableFuture;
    }

    private ProducerRecord<Integer, String> buildProducerRecord(Integer key, String value, String topic) {

        List<Header> recordHeaders = List.of( new RecordHeader("event-source","scanner".getBytes()));

        return new ProducerRecord<>(topic, 0, key, value, recordHeaders);
    }

    //Synchronous Call
    public SendResult<Integer, String> sendLibraryEventSynchronous(LibraryEvent libraryEvent) throws
            JsonProcessingException, ExecutionException, InterruptedException, TimeoutException, Exception {
        Integer key = libraryEvent.getLibraryEventId();
        String value = objectMapper.writeValueAsString(libraryEvent);

        SendResult<Integer, String> sendResult = null;

        try {
            sendResult = kafkaTemplate.sendDefault(key, value).get(1, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException ex) {
            log.error("ExecutionException/InterruptedException Sending the message and the exception is {}", ex.getMessage());
        } catch (TimeoutException ex) {
            log.error("TimeoutException Sending the message and the exception is {}", ex.getMessage());
        } catch (Exception ex) {
            log.error("Exception Sending the message and the exception is {}", ex.getMessage());
        }
        return sendResult;
    }


    private void handleFailure(Integer key, String value, Throwable ex) {
        log.error("Error Sending the message and the exception is {}", ex.getMessage());
        try {
            throw ex;
        } catch (Throwable throwable) {
            log.error("Error in OnFailure: {}", throwable.getMessage());
        }
    }

    private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
        log.info("Message sent successfully for the key :{} and the value is {} , partition is {}", key, value, result.getRecordMetadata().partition());
    }

}
