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
package org.jenetics.util;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.Random;
import java.util.function.IntConsumer;

import org.jenetics.internal.math.probability;

/**
 * Interface which delivers a stream of (positive) indexes ({@code int}s)s. The
 * stream ends if {@link #next()} returns {@code -1}. Here some usage examples:
 *
 * [code]
 * final IndexStream stream = ...;
 * for (int index = stream.next(); index != -1; index = stream.next()) {
 *     System.out.println(index);
 * }
 * [/code]
 * [code]
 * final IndexStream stream = ...;
 * int index = 0;
 * while ((index = stream.next()) != -1) {
 *     System.out.println(index);
 * }
 * [/code]
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.1 &mdash; <em>$Date$</em>
 */
public abstract class IndexStream {

	protected IndexStream() {
	}

	/**
	 * Return the next (positive inclusive zero) index, or -1 if the stream has
	 * reached its end.
	 *
	 * @return the next index, or -1 if the stream has reached its end.
	 */
	public abstract int next();

	/**
	 * Applies a {@code function} to all elements of this stream.
	 *
	 * @param consumer the function to apply to the elements.
	 * @throws NullPointerException if the given {@code function} is
	 *         {@code null}.
	 */
	public void forEach(final IntConsumer consumer) {
		for (int i = next(); i != -1; i = next()) {
			consumer.accept(i);
		}
	}

	/**
	 * Create a new random IndexIterator.
	 * @param n the maximal value (exclusively) the created index stream will
	 *         return.
	 * @param probability the index selection probability.
	 * @throws IllegalArgumentException if {@code n == Integer.MAX_VALUE} or
	 *         {@code n <= 0} or the given {@code probability} is not valid.
	 */
	public static IndexStream Random(final int n, final double probability) {
		return Random(n, probability, RandomRegistry.getRandom());
	}

	/**
	 * Create a new random IndexIterator. The elements returned by this stream
	 * are strictly increasing.
	 *
	 * @param n the maximal value (exclusively) the created index stream will
	 *        return.
	 * @param p the index selection probability.
	 * @param random the random engine used for creating the random indexes.
	 * @throws IllegalArgumentException if {@code n == Integer.MAX_VALUE} or
	 *         {@code n <= 0} or the given {@code probability} is not valid.
	 * @throws NullPointerException if the given {@code random} engine is
	 *         {@code null}.
	 */
	public static IndexStream Random(
		final int n,
		final double p,
		final Random random
	) {
		if (n == Integer.MAX_VALUE) {
			throw new IllegalArgumentException(format(
				"n must be smaller than Integer.MAX_VALUE."
			));
		}
		if (n <= 0) {
			throw new IllegalArgumentException(format(
				"n must be greater than zero: %d", n
			));
		}

		return new RandomIndexStream_(n, p, random);
	}

}

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 1.4 &mdash; <em>$Date$</em>
 */
final class RandomIndexStream extends IndexStream {
	private final int _n;
	private final int _p;
	private final Random _random;

	private int _pos = -1;

	RandomIndexStream(final int n, final double p, final Random random) {
		_n = n;
		_p = probability.toInt(p);
		_random = requireNonNull(random, "Random object must not be null.");
	}

	@Override
	public final int next() {
		while (_pos < _n && _random.nextInt() >= _p) {
			++_pos;
		}
		return (_pos < _n - 1) ? ++_pos : -1;
	}
}


