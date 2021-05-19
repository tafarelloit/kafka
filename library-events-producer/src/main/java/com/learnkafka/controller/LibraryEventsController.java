package com.learnkafka.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.learnkafka.domain.LibraryEvent;
import com.learnkafka.domain.LibraryEventType;
import com.learnkafka.producer.LibraryEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.concurrent.ExecutionException;

@RestController
@Slf4j
public class LibraryEventsController {

    @Autowired
    LibraryEventProducer libraryEventProducer;

    @PostMapping("/v1/libraryevent")
    public ResponseEntity<LibraryEvent> postLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent)
            throws Exception {
        //Invoke Kafka Producer
        //log.info("Before sendLibraryEvent");

        //Asynchronous call
        //libraryEventProducer.sendLibraryEvent(libraryEvent);

        //Asynchronous call with Producer Record
        libraryEvent.setLibraryEventType(LibraryEventType.NEW);
        libraryEventProducer.sendLibraryEventWithProducerRecord(libraryEvent);

        //Synchronous call
        //SendResult<Integer, String> sendResult = libraryEventProducer.sendLibraryEventSynchronous(libraryEvent);
        //log.info("SendResult is{}",sendResult.toString());

        //log.info("after sendLibraryEvent");
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryEvent);
    }


    @PutMapping("/v1/libraryevent")
    public ResponseEntity<?> putLibraryEvent(@RequestBody @Valid LibraryEvent libraryEvent)
            throws JsonProcessingException, ExecutionException, InterruptedException {

        if(libraryEvent.getLibraryEventId()==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please pass the LibraryEventId");

        }

        libraryEvent.setLibraryEventType(LibraryEventType.UPDATE);
        libraryEventProducer.sendLibraryEventWithProducerRecord(libraryEvent);

        return ResponseEntity.status(HttpStatus.OK).body(libraryEvent);
    }

}
