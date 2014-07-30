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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

import org.jenetics.Gene;
import org.jenetics.Optimize;
import org.jenetics.Population;
import org.jenetics.Selector;
import org.jenetics.internal.util.Timer;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date$</em>
 */
public class SelectStage<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
{

	public int survivors;
	public int offspring;
	public Selector<G, C> survivorSelector;
	public Selector<G, C> offspringSelector;
	public Optimize optimize;
	public Executor executor;

	public Result select(final Population<G, C> population) {
        final Timer timer = Timer.of();
		return new Result(
            timer,
			CompletableFuture.supplyAsync(timer.timing(() ->
				survivorSelector.select(population, survivors, optimize)),
				executor
			),
			CompletableFuture.supplyAsync(timer.timing(() ->
				offspringSelector.select(population, offspring, optimize)),
				executor
			)
		);
	}

	public final class Result {
        public final Timer timer;
		public final CompletionStage<Population<G, C>> survivors;
		public final CompletionStage<Population<G, C>> offspring;

		private Result(
            final Timer timer,
			final CompletionStage<Population<G, C>> survivors,
			final CompletionStage<Population<G, C>> offspring
		) {
            this.timer = timer;
			this.survivors = survivors;
			this.offspring = offspring;
		}
	}

}
