/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.example;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import org.jscience.mathematics.number.Integer64;

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
 * @version 1.0 &mdash; <em>$Date$</em>
 */
public class StringGenerator {

	private static class Gen
		implements Function<Genotype<CharacterGene>, Integer64>,
					Serializable
	{
		private static final long serialVersionUID = 1L;

		private final String value;

		public Gen(final String value) {
			this.value = value;
		}

		@Override
		public Integer64 apply(final Genotype<CharacterGene> genotype) {
			final CharacterChromosome chromosome = (CharacterChromosome)genotype.getChromosome();
			return Integer64.valueOf(value.length() - levenshtein(value, chromosome));
		}

		@Override
		public String toString() {
			return value;
		}
	}

	public static void main(String[] args) throws Exception {
		final int maxThreads = Runtime.getRuntime().availableProcessors() + 2;
		final ExecutorService pool = Executors.newFixedThreadPool(maxThreads);

		final String value =
			"jenetics";

		final CharSeq chars = CharSeq.valueOf("a-z");
		final Factory<Genotype<CharacterGene>> gtf = Genotype.valueOf(
			new CharacterChromosome(chars, value.length())
		);
		final Gen ff = new Gen(value);
		final GeneticAlgorithm<CharacterGene, Integer64>
		ga = new GeneticAlgorithm<>(gtf, ff);

		ga.setPopulationSize(500);
		ga.setSurvivorSelector(
			new StochasticUniversalSelector<CharacterGene, Integer64>()
		);
		ga.setOffspringSelector(
			new TournamentSelector<CharacterGene, Integer64>(5)
		);
		ga.setAlterer(new CompositeAlterer<>(
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
