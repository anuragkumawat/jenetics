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

import static java.util.Objects.requireNonNull;
import static org.jenetics.internal.util.Equality.eq;

import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Function;

import org.jenetics.internal.util.Concurrency;
import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;

/**
 * Interface for accumulating values of a given type. Here an usage example:
 *
 * [code]
 * final MinMax&lt;Double&gt; minMax = new MinMax&lt;&gt;();
 * final Variance&lt;Double&gt; variance = new Variance&lt;&gt;();
 * final Quantile&lt;Double&gt; quantile = new Quantile&lt;&gt;();
 *
 * final List&lt;Double&gt; values = ...;
 * accumulators.accumulate(values, minMax, variance, quantile);
 * [/code]
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 3.0 &mdash; <em>$Date$</em>
 */
public interface Accumulator<T> {

	/**
	 * Accumulate the given value.
	 *
	 * @param value the value to accumulate.
	 */
	public void accumulate(final T value);

	/**
	 * Return a view of this adapter with a different type {@code B}.
	 *
	 * Usage example:
	 * [code]
	 * // Convert a string on the fly into a double value.
	 * final Converter&lt;String, Double&gt; converter = new Converter&lt;String, Double&gt;() {
	 *         public Double convert(final String value) {
	 *             return Double.valueOf(value);
	 *         }
	 *     };
	 *
	 * // The values to accumulate
	 * final List&lt;String&gt; values = Arrays.asList("0", "1", "2", "3", "4", "5");
	 *
	 * final Accumulators.Min&lt;Double&gt; accumulator = new Accumulators.Min&lt;Double&gt;();
	 *
	 * // No pain to accumulate collections of a different type.
	 * Accumulators.accumulate(values, accumulator.map(converter));
	 * [/code]
	 *
	 * @param <B> the type of the returned adapter (view).
	 * @param mapper the mapper needed to map between the type of this
	 *        adapter and the adapter view type.
	 * @return the adapter view with the different type.
	 * @throws NullPointerException if the given {@code converter} is {@code null}.
	 */
	public default <B> Accumulator<B> map(final Function<? super B, ? extends T> mapper) {
		return value -> accumulate(mapper.apply(value));
	}



	/**
	 * Calculates min value.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.0
	 * @version 1.0 &ndash; <em>$Revision$</em>
	 */
	public static final class Min<C extends Comparable<? super C>>
		extends AbstractAccumulator<C>
	{
		private C _min;

		/**
		 * Create a new Min accumulator.
		 */
		public Min() {
		}

		/**
		 * Copy constructor.
		 *
		 * @param min the accumulator to copy.
		 * @throws NullPointerException if {@code min} is {@code null}.
		 */
		public Min(final Min<C> min) {
			Objects.requireNonNull(min, "Min");
			_samples = min._samples;
			_min = min._min;
		}

		/**
		 * Return the min value, accumulated so far.
		 *
		 * @return the min value, accumulated so far.
		 */
		public C getMin() {
			return _min;
		}

		/**
		 * @throws NullPointerException if the given {@code value} is {@code null}.
		 */
		@Override
		public void accumulate(final C value) {
			if (_min == null) {
				_min = value;
			} else {
				if (value.compareTo(_min) < 0) {
					_min = value;
				}
			}

			++_samples;
		}

		@Override
		public int hashCode() {
			return Hash.of(getClass()).and(super.hashCode()).and(_min).value();
		}

		@Override
		public boolean equals(final Object obj) {
			return Equality.of(this, obj).test(min ->
				super.equals(obj) &&
				eq(_min, min._min)
			);
		}

		@Override
		public String toString() {
			return String.format(
				"%s[samples=%d, min=%s]",
				getClass().getSimpleName(), getSamples(), getMin()
			);
		}

		@Override
		public Min<C> clone() {
			return (Min<C>)super.clone();
		}
	}


	/**
	 * Calculates max value.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.0
	 * @version 1.0 &ndash; <em>$Revision$</em>
	 */
	public static final class Max<C extends Comparable<? super C>>
		extends AbstractAccumulator<C>
	{
		private C _max;

		/**
		 * Create a new Max accumulator.
		 */
		public Max() {
		}

		/**
		 * Copy constructor.
		 *
		 * @param max the accumulator to copy.
		 * @throws NullPointerException if {@code max} is {@code null}.
		 */
		public Max(final Max<C> max) {
			requireNonNull(max, "Max");
			_samples = max._samples;
			_max = max._max;
		}

		/**
		 * Return the max value, accumulated so far.
		 *
		 * @return the max value, accumulated so far.
		 */
		public C getMax() {
			return _max;
		}

		/**
		 * @throws NullPointerException if the given {@code value} is {@code null}.
		 */
		@Override
		public void accumulate(final C value) {
			if (_max == null) {
				_max = value;
			} else {
				if (value.compareTo(_max) > 0) {
					_max = value;
				}
			}

			++_samples;
		}

		@Override
		public int hashCode() {
			return Hash.of(getClass()).and(super.hashCode()).and(_max).value();
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj == null || obj.getClass() != getClass()) {
				return false;
			}

			final Max<?> max = (Max<?>)obj;
			return super.equals(obj) && eq(_max, max._max);
		}

		@Override
		public String toString() {
			return String.format(
				"%s[samples=%d, max=%s]",
				getClass().getSimpleName(), getSamples(), getMax()
			);
		}

		@Override
		public Max<C> clone() {
			return (Max<C>)super.clone();
		}
	}


	/**
	 * Calculates min and max values.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.0
	 * @version 1.0 &ndash; <em>$Revision$</em>
	 */
	public static final class MinMax<C extends Comparable<? super C>>
		extends AbstractAccumulator<C>
	{
		private C _min;
		private C _max;

		/**
		 * Create a new min-max accumulator.
		 */
		public MinMax() {
		}

		/**
		 * Copy constructor.
		 *
		 * @param mm the accumulator to copy.
		 * @throws NullPointerException if {@code mm} is {@code null}.
		 */
		public MinMax(final MinMax<C> mm) {
			requireNonNull(mm, "MinMax");
			_samples = mm._samples;
			_min = mm._min;
			_max = mm._max;
		}

		/**
		 * Return the min value, accumulated so far.
		 *
		 * @return the min value, accumulated so far.
		 */
		public C getMin() {
			return _min;
		}

		/**
		 * Return the max value, accumulated so far.
		 *
		 * @return the max value, accumulated so far.
		 */
		public C getMax() {
			return _max;
		}

		/**
		 * @throws NullPointerException if the given {@code value} is {@code null}.
		 */
		@Override
		public void accumulate(final C value) {
			if (_min == null) {
				_min = value;
				_max = value;
			} else {
				if (value.compareTo(_min) < 0) {
					_min = value;
				} else if (value.compareTo(_max) > 0) {
					_max = value;
				}
			}

			++_samples;
		}

		@Override
		public int hashCode() {
			return Hash.of(getClass()).
				and(super.hashCode()).
				and(_min).
				and(_max).value();
		}

		@Override
		public boolean equals(final Object obj) {
			return Equality.of(this, obj).test(mm ->
				super.equals(obj) &&
				eq(_min, mm._min) &&
				eq(_max, mm._max)
			);
		}

		@Override
		public String toString() {
			return String.format(
				"%s[samples=%d, min=%s, max=%s]",
				getClass().getSimpleName(), getSamples(), getMin(), getMax()
			);
		}

		@Override
		public MinMax<C> clone() {
			return (MinMax<C>)super.clone();
		}
	}

	/**
	 * Calls the {@link Accumulator#accumulate(Object)} method of all given
	 * {@code accumulators} with each value of the given {@code values}. The
	 * accumulation is done in parallel.
	 *
	 * @param <T> the value type.
	 * @param executor the {@link java.util.concurrent.Executor} service to
	 *        use
	 * @param values the values to accumulate.
	 * @param accus the accumulators to apply.
	 * @throws NullPointerException if one of the given arguments is {@code null}.
	 */
	public static <T> void accumulate(
		final Executor executor,
		final Iterable<? extends T> values,
		final Seq<? extends Accumulator<? super T>> accus
	) {
		switch (accus.length()) {
			case 1:
				Accumulator.accumulate(
					executor,
					values,
					accus.get(0)
				);
				break;
			case 2:
				Accumulator.accumulate(
					executor,
					values,
					accus.get(0),
					accus.get(1)
				);
				break;
			case 3:
				Accumulator.accumulate(
					executor,
					values,
					accus.get(0),
					accus.get(1),
					accus.get(2)
				);
				break;
			case 4:
				Accumulator.accumulate(
					executor,
					values,
					accus.get(0),
					accus.get(1),
					accus.get(2),
					accus.get(3)
				);
				break;
			case 5:
				Accumulator.accumulate(
					executor,
					values,
					accus.get(0),
					accus.get(1),
					accus.get(2),
					accus.get(3),
					accus.get(4)
				);
				break;
			default:
				try (Concurrency c = Concurrency.with(executor)) {
					c.execute(accus.map(a -> new Acc<>(values, a)).asList());
				}
		}
	}

	/**
	 * Calls the {@link Accumulator#accumulate(Object)} method of all given
	 * {@code accumulators} with each value of the given {@code values}. The
	 * accumulation is done in parallel.
	 *
	 * @param <T> the value type.
	 * @param executor the {@link java.util.concurrent.Executor} service to
	 *        use
	 * @param values the values to accumulate.
	 * @param accus the accumulators to apply.
	 * @throws NullPointerException if one of the given arguments is {@code null}.
	 */
	@SafeVarargs
	public static <T> void accumulate(
		final Executor executor,
		final Iterable<? extends T> values,
		final Accumulator<? super T>... accus
	) {
		accumulate(executor, values, MSeq.of(accus));
	}

	/**
	 * Calls the {@link Accumulator#accumulate(Object)} method of the given
	 * {@code accumulator} with each value of the given {@code values}.
	 *
	 * @param <T> the value type.
	 * @param values the values to accumulate.
	 * @param a the accumulator.
	 * @throws NullPointerException if one of the given arguments is {@code null}.
	 */
	public static <T> void accumulate(
		final Iterator<? extends T> values,
		final Accumulator<? super T> a
	) {
		while (values.hasNext()) {
			a.accumulate(values.next());
		}
	}

	/**
	 * Calls the {@link Accumulator#accumulate(Object)} method of the given
	 * {@code accumulator} with each value of the given {@code values}.
	 *
	 * @param <T> the value type.
	 * @param executor the {@link java.util.concurrent.Executor} service to
	 *        use
	 * @param values the values to accumulate.
	 * @param a the accumulator.
	 * @throws NullPointerException if one of the given arguments is {@code null}.
	 */
	public static <T> void accumulate(
		final Executor executor,
		final Iterable<? extends T> values,
		final Accumulator<? super T> a
	) {
		for (final T value : values) {
			a.accumulate(value);
		}
	}

	/**
	 * Calls the {@link Accumulator#accumulate(Object)} method of all given
	 * {@code accumulators} with each value of the given {@code values}. The
	 * accumulation is done in parallel.
	 *
	 * @param <T> the value type.
	 * @param executor the {@link java.util.concurrent.Executor} service to
	 *        use
	 * @param values the values to accumulate.
	 * @param a1 the first accumulator.
	 * @param a2 the second accumulator.
	 * @throws NullPointerException if one of the given arguments is {@code null}.
	 */
	public static <T> void accumulate(
		final Executor executor,
		final Iterable<? extends T> values,
		final Accumulator<? super T> a1,
		final Accumulator<? super T> a2
	) {
		try (Concurrency c = Concurrency.with(executor)) {
			c.execute(new Acc<>(values, a1));
			c.execute(new Acc<>(values, a2));
		}
	}

	/**
	 * Calls the {@link Accumulator#accumulate(Object)} method of all given
	 * {@code accumulators} with each value of the given {@code values}. The
	 * accumulation is done in parallel.
	 *
	 * @param <T> the value type.
	 * @param executor the {@link java.util.concurrent.Executor} service to
	 *        use
	 * @param values the values to accumulate.
	 * @param a1 the first accumulator.
	 * @param a2 the second accumulator.
	 * @param a3 the third accumulator
	 * @throws NullPointerException if one of the given arguments is {@code null}.
	 */
	public static <T> void accumulate(
		final Executor executor,
		final Iterable<? extends T> values,
		final Accumulator<? super T> a1,
		final Accumulator<? super T> a2,
		final Accumulator<? super T> a3
	) {
		try (Concurrency c = Concurrency.with(executor)) {
			c.execute(new Acc<>(values, a1));
			c.execute(new Acc<>(values, a2));
			c.execute(new Acc<>(values, a3));
		}
	}

	/**
	 * Calls the {@link Accumulator#accumulate(Object)} method of all given
	 * {@code accumulators} with each value of the given {@code values}. The
	 * accumulation is done in parallel.
	 *
	 * @param <T> the value type.
	 * @param executor the {@link java.util.concurrent.Executor} service to
	 *        use
	 * @param values the values to accumulate.
	 * @param a1 the first accumulator.
	 * @param a2 the second accumulator.
	 * @param a3 the third accumulator.
	 * @param a4 the fourth accumulator.
	 * @throws NullPointerException if one of the given arguments is {@code null}.
	 */
	public static <T> void accumulate(
		final Executor executor,
		final Iterable<? extends T> values,
		final Accumulator<? super T> a1,
		final Accumulator<? super T> a2,
		final Accumulator<? super T> a3,
		final Accumulator<? super T> a4
	) {
		try (Concurrency c = Concurrency.with(executor)) {
			c.execute(new Acc<>(values, a1));
			c.execute(new Acc<>(values, a2));
			c.execute(new Acc<>(values, a3));
			c.execute(new Acc<>(values, a4));
		}
	}

	/**
	 * Calls the {@link Accumulator#accumulate(Object)} method of all given
	 * {@code accumulators} with each value of the given {@code values}. The
	 * accumulation is done in parallel.
	 *
	 * @param <T> the value type.
	 * @param executor the {@link java.util.concurrent.Executor} service to
	 *        use
	 * @param values the values to accumulate.
	 * @param a1 the first accumulator.
	 * @param a2 the second accumulator.
	 * @param a3 the third accumulator.
	 * @param a4 the fourth accumulator.
	 * @param a5 the fifth accumulator.
	 * @throws NullPointerException if one of the given arguments is {@code null}.
	 */
	public static <T> void accumulate(
		final Executor executor,
		final Iterable<? extends T> values,
		final Accumulator<? super T> a1,
		final Accumulator<? super T> a2,
		final Accumulator<? super T> a3,
		final Accumulator<? super T> a4,
		final Accumulator<? super T> a5
	) {
		try (Concurrency c = Concurrency.with(executor)) {
			c.execute(new Acc<>(values, a1));
			c.execute(new Acc<>(values, a2));
			c.execute(new Acc<>(values, a3));
			c.execute(new Acc<>(values, a4));
			c.execute(new Acc<>(values, a5));
		}
	}

	static final class Acc<T> implements Runnable {
		private final Iterable<? extends T> _values;
		private final Accumulator<? super T> _accumulator;

		public Acc(
			final Iterable<? extends T> values,
			final Accumulator<? super T> accumulator
		) {
			_values = values;
			_accumulator = accumulator;
		}

		@Override
		public void run() {
			for (final T value : _values) {
				_accumulator.accumulate(value);
			}
		}
	}
}
