/**
 * Copyright (c) KMG. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package io.sbk.api;

/**
 * Interface for performance Statistics.
 */
public interface Performance extends Benchmark {

     /**
     * Get the Time recorder for benchmarking.
     *
     * @return RecordTime Interface if its available ; returns null in case of failure.
     */
     RecordTime get();
}
