/**
 * Copyright (c) 2020 KMG. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package io.sbk.Pulsar;
import io.sbk.api.Writer;
import io.sbk.api.QuadConsumer;
import io.sbk.api.Parameters;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
/**
 * Class for Pulsar writer/producer.
 */
public class PulsarWriter extends Writer {
    final private Producer<byte[]> producer;

    public PulsarWriter(int writerID, Parameters params, QuadConsumer recordTime,
                              String topicName, PulsarClient client) throws IOException {
        super(writerID, params, recordTime);
        try {
            this.producer = client.newProducer()
                    .enableBatching(true)
                    .topic(topicName)
                    .blockIfQueueFull(true).create();
        } catch (PulsarClientException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public CompletableFuture writeAsync(byte[] data) throws IOException {
        return producer.sendAsync(data);
    }


    @Override
    public void flush() throws IOException {
        try {
            producer.flush();
        } catch (PulsarClientException ex) {
            throw new IOException(ex);
        }
    }

    @Override
    public synchronized void close() throws IOException {
        try {
            producer.close();
        } catch (PulsarClientException ex) {
            throw new IOException(ex);
        }
    }
}