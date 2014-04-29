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
package org.jenetics.stat;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.jenetics.internal.util.object.eq;

import java.util.function.IntConsumer;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;

import org.jenetics.internal.util.Hash;

/**
 * @see <a href="http://people.xiph.org/~tterribe/notes/homs.html">
 *      Computing Higher-Order Moments Online</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date$</em>
 */
public class IntMoments extends Moments implements IntConsumer {

	private int _min = Integer.MAX_VALUE;
	private int _max = Integer.MIN_VALUE;
	private long _sum = 0L;

	@Override
	public void accept(final int value) {
		update(value);

		_min = min(_min, value);
		_max = max(_max, value);
		_sum += value;
	}

	/**
	 * Combine two {@code DoubleMoments} statistic objects.
	 *
	 * @param other the other {@code DoubleMoments} statistics to combine with
	 *        {@code this} one.
	 * @return a new statistical objects.
	 * @throws java.lang.NullPointerException if the other statistical summary
	 *         is {@code null}.
	 */
	public void combine(final IntMoments other) {
		super.combine(other);
		_min = min(_min, other._min);
		_max = max(_max, other._max);
	}

	public int getMin() {
		return _min;
	}

	public int getMax() {
		return _max;
	}

	public long getSum() {
		return _sum;
	}

	@Override
	public int hashCode() {
		return Hash.of(IntMoments.class)
			.and(super.hashCode())
			.and(_min)
			.and(_max)
			.and(_sum).value();
	}

	@Override
	public boolean equals(final Object object) {
		if (object == null) {
			return true;
		}
		if (!(object instanceof IntMoments)) {
			return false;
		}

		final IntMoments moments = (IntMoments)object;
		return super.equals(object) &&
			eq(_min, moments._min) &&
			eq(_max, moments._max) &&
			eq(_sum, moments._sum);
	}

	@Override
	public String toString() {
		return String.format(
			"Summary[N=%d, ∧=%s, ∨=%s, Σ=%s, μ=%s, s2=%s, S=%s, K=%s]",
			getCount(), _min, _max, _sum,
			getMean(), getVariance(), getSkewness(), getKurtosis()
		);
	}


	public static <T> Collector<T, ?, IntMoments>
	collector(final ToIntFunction<? super T> mapper) {
		return Collector.of(
			IntMoments::new,
			(r, t) -> r.accept(mapper.applyAsInt(t)),
			(a, b) -> {a.combine(b); return a;}
		);
	}

}
