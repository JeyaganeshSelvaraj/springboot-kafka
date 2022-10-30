package com.example.kafkaexample.handlers;

import org.apache.avro.specific.SpecificRecord;

public interface  EventHandler<T extends SpecificRecord> {
    void handle(T event);
}
