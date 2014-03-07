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

import java.util.function.Function;

import org.jenetics.BitChromosome;
import org.jenetics.BitGene;
import org.jenetics.Chromosome;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.Mutator;
import org.jenetics.NumberStatistics;
import org.jenetics.Optimize;
import org.jenetics.RouletteWheelSelector;
import org.jenetics.SinglePointCrossover;
import org.jenetics.util.Factory;
import java.util.function.Function;

final class OneCounter
	implements Function<Genotype<BitGene>, Integer>
{
	@Override
	public Integer apply(final Genotype<BitGene> genotype) {
		return ((BitChromosome)genotype.getChromosome()).bitCount();
	}
}

public class OnesCounting {
	public static void main(String[] args) {
		Factory<Genotype<BitGene>> gtf = Genotype.of(
			BitChromosome.of(20, 0.15)
		);
		final Function<Genotype<BitGene>, Integer> ff = genotype -> {
			final Chromosome<BitGene> chromosome = genotype.getChromosome();

			int count = 0;
			if (chromosome instanceof BitChromosome) {
				count = ((BitChromosome)chromosome).bitCount();
			} else {
				for (BitGene gene : genotype.getChromosome()) {
					if (gene.getBit()) {
						++count;
					}
				}
			}

			return count;
		};

		GeneticAlgorithm<BitGene, Integer> ga = new GeneticAlgorithm<>(
			gtf, ff, Optimize.MAXIMUM
		);

		ga.setStatisticsCalculator(new NumberStatistics.Calculator<>());
		ga.setPopulationSize(500);
		ga.setSelectors(new RouletteWheelSelector<>());
		ga.setAlterers(
			new Mutator<BitGene>(0.55),
			new SinglePointCrossover<BitGene>(0.06)
		);

		ga.setup();
		ga.evolve(100);
		System.out.println(ga.getBestStatistics());
		System.out.println(ga.getBestPhenotype());
	}
}
