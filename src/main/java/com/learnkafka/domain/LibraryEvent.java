package com.learnkafka.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LibraryEvent {
    private String eventId = UUID.randomUUID().toString();
    private Book book;
}
