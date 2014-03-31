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
package org.jenetics.example;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import org.jenetics.CharacterChromosome;
import org.jenetics.CharacterGene;
import org.jenetics.CompositeAlterer;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.Mutator;
import org.jenetics.SinglePointCrossover;
import org.jenetics.StochasticUniversalSelector;
import org.jenetics.TournamentSelector;
import org.jenetics.util.CharSeq;
import org.jenetics.util.Factory;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0 &mdash; <em>$Date$</em>
 */
public class StringGenerator {

	private static class Gen
		implements Function<Genotype<CharacterGene>, Integer>,
					Serializable
	{
		private static final long serialVersionUID = 1L;

		private final String value;

		public Gen(final String value) {
			this.value = value;
		}

		@Override
		public Integer apply(final Genotype<CharacterGene> gt) {
			return value.length() - levenshtein(
				value, (CharacterChromosome)gt.getChromosome()
			);
		}

		@Override
		public String toString() {
			return value;
		}
	}

	public static void main(String[] args) throws Exception {
		final int maxThreads = Runtime.getRuntime().availableProcessors() + 2;
		final ExecutorService pool = Executors.newFixedThreadPool(maxThreads);

		final String value = "jenetics";

		final CharSeq chars = CharSeq.of("a-z");
		final Factory<Genotype<CharacterGene>> gtf = Genotype.of(
			new CharacterChromosome(chars, value.length())
		);
		final Gen ff = new Gen(value);
		final GeneticAlgorithm<CharacterGene, Integer> ga = new GeneticAlgorithm<>(gtf, ff);

		ga.setPopulationSize(500);
		ga.setSurvivorSelector(
			new StochasticUniversalSelector<CharacterGene, Integer>()
		);
		ga.setOffspringSelector(
			new TournamentSelector<CharacterGene, Integer>(5)
		);
		ga.setAlterer(CompositeAlterer.of(
			new Mutator<CharacterGene>(0.1),
			new SinglePointCrossover<CharacterGene>(0.5)
		));

		final int generations = 100;

		GAUtils.printConfig(
				"String generator",
				ga,
				generations,
				((CompositeAlterer<?>)ga.getAlterer()).getAlterers().toArray()
			);

		GAUtils.execute(ga, generations, 20);

		pool.shutdown();
	}


	/**
	 * Return Levenshtein distance of two character sequences.
	 */
	private static int levenshtein(final CharSequence s, final CharSequence t) {
		//Step 1:
		final int n = s.length();
		final int m = t.length();
		if (n == 0 || m == 0) {
			return Math.max(n, m);
		}

		//Step 2:
		int d[][] = new int[n + 1][m +1];
		for (int i = 0; i <= n; ++i) {
			d[i][0] = i;
		}
		for (int j = 0; j <= m; ++j) {
			d[0][j] = j;
		}

		//Step 3:
		for (int i = 1; i <= n; ++i) {
			final char si = s.charAt(i - 1);

			//Step 4:
			for (int j = 1; j <= m; ++j) {
				final char tj = t.charAt(j - 1);

				//Step 5:
				int cost = 0;
				if (si == tj) {
					cost = 0;
				} else {
					cost = 1;
				}

				//Step 6:
				d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + cost);
			}
		}

		//Step 7:
		return d[n][m];
	}

	private static int min(final int a, final int b, final int c) {
		int m = a;
		if (b < m) {
			m = b;
		}
		if (c < m) {
			m = c;
		}
		return m;
	}

}
