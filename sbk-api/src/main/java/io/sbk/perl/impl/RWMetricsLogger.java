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


import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.sbk.perl.Time;
import io.sbk.perl.TimeUnit;

/**
 * Class for recoding/printing benchmark results of Readers and Writers
 * on micrometer Composite Meter Registry.
 */
public class RWMetricsLogger extends MetricsLogger {

    public RWMetricsLogger(String header, String action, double[] percentiles, Time time, TimeUnit latencyTimeUnit,
                         CompositeMeterRegistry compositeRegistry, int readers, int writers) {
       super(header, action,  percentiles, time, latencyTimeUnit, compositeRegistry);
       final String writersName = metricPrefix + "_Writers";
       final String readersName = metricPrefix + "_Readers";
       this.registry.gauge(writersName, writers);
       this.registry.gauge(readersName, readers);
    }
}