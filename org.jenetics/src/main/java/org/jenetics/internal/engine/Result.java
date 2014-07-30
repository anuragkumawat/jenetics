/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 */
package org.jenetics.internal.engine;

import static org.jenetics.internal.time.minus;

import java.time.Duration;
import java.time.Instant;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date$</em>
 */
public final class Result<T> {

	private final Instant _start;
	private final Instant _stop;

	private final T _value;

	public Result(final Instant start, final Instant stop, final T value) {
		_start = start;
		_stop = stop;
		_value = value;
	}

	public Instant getStart() {
		return _start;
	}

	public Instant getStop() {
		return _stop;
	}

	public Duration getDuration() {
		return minus(_stop, _start);
	}

	public T get() {
		return _value;
	}

}
