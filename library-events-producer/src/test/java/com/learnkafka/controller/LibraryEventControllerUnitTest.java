package com.learnkafka.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnkafka.domain.Book;
import com.learnkafka.domain.LibraryEvent;
import com.learnkafka.producer.LibraryEventProducer;
import org.junit.jupiter.api.Test;
import org.mockito.internal.stubbing.answers.ThrowsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(LibraryEventsController.class)
@AutoConfigureMockMvc
public class LibraryEventControllerUnitTest {
    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectmapper = new ObjectMapper();

    @MockBean
    LibraryEventProducer libraryEventProducer;

    @Test
    void postLibraryEvent() throws Exception {
        //given
        Book book = Book.builder()
                .bookId(123)
                .bookAuthor("Itamar")
                .bookName("Kafka using Spring Boot")
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(null)
                .book(book)
                .build();

        String json = objectmapper.writeValueAsString(libraryEvent);

        when(libraryEventProducer.sendLibraryEventWithProducerRecord(isA(LibraryEvent.class))).thenReturn(null);
        //doNothing().when(libraryEventProducer).sendLibraryEventWithProducerRecord(isA(LibraryEvent.class));
        //When

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/libraryevent")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isCreated());

    }

    @Test
    void postLibraryEvent_4xx() throws Exception {
        //given
        Book book = Book.builder()
                .bookId(null)
                .bookAuthor(null)
                .bookName("Kafka using Spring Boot")
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(null)
                .book(book)
                .build();

        String json = objectmapper.writeValueAsString(libraryEvent);

        when(libraryEventProducer.sendLibraryEventWithProducerRecord(isA(LibraryEvent.class))).thenReturn(null);
        //doNothing().when(libraryEventProducer).sendLibraryEventWithProducerRecord(isA(LibraryEvent.class));

        //expect
        String expectErrorMessage = "book.bookAuthor - must not be blank, book.bookId - must not be null";

        mockMvc.perform(MockMvcRequestBuilders.post("/v1/libraryevent")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().string(expectErrorMessage));
    }


    @Test
    void updateLibraryEvent() throws Exception {
        //given
        Book book = Book.builder()
                .bookId(123)
                .bookAuthor("Itamar")
                .bookName("Kafka using Spring Boot")
                .build();

        int libraryEventId = 123;

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(libraryEventId)
                .book(book)
                .build();

        String json = objectmapper.writeValueAsString(libraryEvent);

        when(libraryEventProducer.sendLibraryEventWithProducerRecord(isA(LibraryEvent.class))).thenReturn(null);
        //doNothing().when(libraryEventProducer).sendLibraryEventWithProducerRecord(isA(LibraryEvent.class));
        //When

        mockMvc.perform(MockMvcRequestBuilders.put("/v1/libraryevent")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void updateLibraryEvent_withNullLibraryEventId() throws Exception {
        //given
        Book book = Book.builder()
                .bookId(123)
                .bookAuthor("Itamar")
                .bookName("Kafka using Spring Boot")
                .build();

        LibraryEvent libraryEvent = LibraryEvent.builder()
                .libraryEventId(null)
                .book(book)
                .build();

        String json = objectmapper.writeValueAsString(libraryEvent);

        when(libraryEventProducer.sendLibraryEventWithProducerRecord(isA(LibraryEvent.class))).thenReturn(null);
        //doNothing().when(libraryEventProducer).sendLibraryEventWithProducerRecord(isA(LibraryEvent.class));
        //When

        mockMvc.perform(MockMvcRequestBuilders.put("/v1/libraryevent")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
