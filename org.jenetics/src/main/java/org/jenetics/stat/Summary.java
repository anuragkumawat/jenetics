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

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date$</em>
 * @since @__version__@
 */
public interface Summary<N extends Number & Comparable<? super N>> {
	public long getSampleSize();
	public N getMin();
	public N getMax();
	public double getSum();
	public double getMean();
	public double getVariance();
	public double getSkewness();
	public double getKurtosis();

	public static <N extends Number & Comparable<? super N>>
	Collector<N, ?, Summary<N>> collector() {
		return new Collector<N, CollectibleSummary<N>, Summary<N>>() {
			@Override
			public Supplier<CollectibleSummary<N>> supplier() {
				return CollectibleSummary::new;
			}

			@Override
			public BiConsumer<CollectibleSummary<N>, N> accumulator() {
				return CollectibleSummary<N>::accumulate;
			}

			@Override
			public BinaryOperator<CollectibleSummary<N>> combiner() {
				return CollectibleSummary<N>::combine;
			}

			@Override
			public Function<CollectibleSummary<N>, Summary<N>> finisher() {
				return s -> s;
			}

			@Override
			public Set<Characteristics> characteristics() {
				return Collections.unmodifiableSet(EnumSet.of(
					//Characteristics.CONCURRENT,
					//Characteristics.UNORDERED,
					Characteristics.IDENTITY_FINISH
				));
			}
		};
	}
}

