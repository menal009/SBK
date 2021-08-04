/**
 * Copyright (c) KMG. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package io.sbk.perl.impl;

import io.sbk.perl.LatencyPercentiles;
import io.sbk.perl.LatencyRecord;
import io.sbk.perl.LatencyRecordWindow;
import io.sbk.perl.ReportLatencies;
import io.sbk.perl.Time;

import javax.annotation.concurrent.NotThreadSafe;

/**
 *  class for Performance statistics.
 */
@NotThreadSafe
public class ArrayLatencyRecorder extends LatencyRecordWindow {
    final private long[] latencies;
    private int minIndex;
    private int maxIndex;

    public ArrayLatencyRecorder(long lowLatency, long highLatency, long totalLatencyMax, long totalRecordsMax,
                                long bytesMax, double[] percentiles, Time time) {
        super(lowLatency, highLatency, totalLatencyMax, totalRecordsMax, bytesMax, percentiles, time);
        final int size = (int) Math.min(highLatency-lowLatency, Integer.MAX_VALUE);
        this.latencies = new long[size];
        this.minIndex = size;
        this.maxIndex = 0;
    }

    @Override
    final public void reset(long startTime) {
        super.reset(startTime);
        this.maxIndex = 0;
        this.minIndex = Integer.MAX_VALUE;
    }


    @SuppressWarnings("DuplicatedCode")
    @Override
    final public void copyPercentiles(LatencyPercentiles percentiles, ReportLatencies copyLatencies) {
        long curIndex;
        int index;
        long medianIndex;

        if (copyLatencies != null) {
            copyLatencies.reportLatencyRecord(this);
        }

        for (int i = 0; i < percentiles.fractions.length; i++) {
            percentiles.indexes[i] = (long) (validLatencyRecords * percentiles.fractions[i]);
            percentiles.latencies[i] = 0;
            percentiles.latencyCount[i] = 0;
        }
        percentiles.minLatency = -1;
        percentiles.maxLatency = 0;
        percentiles.medianLatency = 0;
        medianIndex = (long) (validLatencyRecords * 0.5);
        curIndex = 0;
        index = 0;
        for (int i = minIndex; i < Math.min(latencies.length, this.maxIndex+1); i++) {
            if (latencies[i] > 0) {

                final long latency = i + lowLatency;
                final long count = latencies[i];
                final long nextIndex =  curIndex + count;

                if (copyLatencies != null) {
                    copyLatencies.reportLatency(latency, count);
                }

                if (percentiles.minLatency == -1 ) {
                    percentiles.minLatency = latency;
                }
                percentiles.maxLatency = latency;
                if (medianIndex >= curIndex && medianIndex < nextIndex) {
                    percentiles.medianLatency = latency;
                }

                while (index < percentiles.indexes.length) {
                    if (percentiles.indexes[index] >= curIndex &&
                            percentiles.indexes[index] < nextIndex) {
                        percentiles.latencies[index] = latency;
                        percentiles.latencyCount[index] = count;
                        index += 1;
                    } else {
                        break;
                    }
                }

                curIndex = nextIndex;
                latencies[i] = 0;
            }
        }
    }

    @Override
    final public void reportLatencyRecord(LatencyRecord record) {
        super.updateRecord(record);
    }


    @Override
    final public void reportLatency(long latency, long count) {
        final int index = (int) (latency - this.lowLatency);
        if (index < this.latencies.length) {
            this.minIndex = Math.min(this.minIndex, index);
            this.maxIndex = Math.max(this.maxIndex, index);
            this.latencies[index] += count;
        }
    }



    /**
     * Record the latency.
     *  @param startTime start time.
     * @param bytes number of bytes.
     * @param events number of events(records).
     * @param latency latency value in milliseconds.
     */
    @Override
    final public void recordLatency(long startTime, int bytes, int events, long latency) {
        if (record(bytes, events, latency)) {
            reportLatency(latency, events);
        }
    }

}
