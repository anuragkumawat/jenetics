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
package org.jenetics;

import java.io.Serializable;
import java.util.Random;
import java.util.function.Function;

import org.jscience.mathematics.number.Float64;

import org.jenetics.util.Array;
import org.jenetics.util.ISeq;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
class TestUtils {

	private TestUtils() {
	}

	/**
	 * Data for alter count tests.
	 */
	public static Object[][] alterCountParameters() {
		return new Object[][] {
			//    ngenes,       nchromosomes     npopulation
			{ new Integer(1),   new Integer(1),  new Integer(100) },
			{ new Integer(5),   new Integer(1),  new Integer(100) },
			{ new Integer(80),  new Integer(1),  new Integer(100) },
			{ new Integer(1),   new Integer(2),  new Integer(100) },
			{ new Integer(5),   new Integer(2),  new Integer(100) },
			{ new Integer(80),  new Integer(2),  new Integer(100) },
			{ new Integer(1),   new Integer(15), new Integer(100) },
			{ new Integer(5),   new Integer(15), new Integer(100) },
			{ new Integer(80),  new Integer(15), new Integer(100) },

			{ new Integer(1),   new Integer(1),  new Integer(150) },
			{ new Integer(5),   new Integer(1),  new Integer(150) },
			{ new Integer(80),  new Integer(1),  new Integer(150) },
			{ new Integer(1),   new Integer(2),  new Integer(150) },
			{ new Integer(5),   new Integer(2),  new Integer(150) },
			{ new Integer(80),  new Integer(2),  new Integer(150) },
			{ new Integer(1),   new Integer(15), new Integer(150) },
			{ new Integer(5),   new Integer(15), new Integer(150) },
			{ new Integer(80),  new Integer(15), new Integer(150) },

			{ new Integer(1),   new Integer(1),  new Integer(500) },
			{ new Integer(5),   new Integer(1),  new Integer(500) },
			{ new Integer(80),  new Integer(1),  new Integer(500) },
			{ new Integer(1),   new Integer(2),  new Integer(500) },
			{ new Integer(5),   new Integer(2),  new Integer(500) },
			{ new Integer(80),  new Integer(2),  new Integer(500) },
			{ new Integer(1),   new Integer(15), new Integer(500) },
			{ new Integer(5),   new Integer(15), new Integer(500) },
			{ new Integer(80),  new Integer(15), new Integer(500) }
		};
	}

	/**
	 * Data for alter probability tests.
	 */
	public static Object[][] alterProbabilityParameters() {
		return new Object[][] {
			//    ngenes,       nchromosomes     npopulation
			{ new Integer(20),   new Integer(20),  new Integer(20), new Double(0.5) },
			{ new Integer(1),   new Integer(1),  new Integer(150), new Double(0.15) },
			{ new Integer(5),   new Integer(1),  new Integer(150), new Double(0.15) },
			{ new Integer(80),  new Integer(1),  new Integer(150), new Double(0.15) },
			{ new Integer(1),   new Integer(2),  new Integer(150), new Double(0.15) },
			{ new Integer(5),   new Integer(2),  new Integer(150), new Double(0.15) },
			{ new Integer(80),  new Integer(2),  new Integer(150), new Double(0.15) },
			{ new Integer(1),   new Integer(15), new Integer(150), new Double(0.15) },
			{ new Integer(5),   new Integer(15), new Integer(150), new Double(0.15) },
			{ new Integer(80),  new Integer(15), new Integer(150), new Double(0.15) },

			{ new Integer(1),   new Integer(1),  new Integer(150), new Double(0.5) },
			{ new Integer(5),   new Integer(1),  new Integer(150), new Double(0.5) },
			{ new Integer(80),  new Integer(1),  new Integer(150), new Double(0.5) },
			{ new Integer(1),   new Integer(2),  new Integer(150), new Double(0.5) },
			{ new Integer(5),   new Integer(2),  new Integer(150), new Double(0.5) },
			{ new Integer(80),  new Integer(2),  new Integer(150), new Double(0.5) },
			{ new Integer(1),   new Integer(15), new Integer(150), new Double(0.5) },
			{ new Integer(5),   new Integer(15), new Integer(150), new Double(0.5) },
			{ new Integer(80),  new Integer(15), new Integer(150), new Double(0.5) },

			{ new Integer(1),   new Integer(1),  new Integer(150), new Double(0.85) },
			{ new Integer(5),   new Integer(1),  new Integer(150), new Double(0.85) },
			{ new Integer(80),  new Integer(1),  new Integer(150), new Double(0.85) },
			{ new Integer(1),   new Integer(2),  new Integer(150), new Double(0.85) },
			{ new Integer(5),   new Integer(2),  new Integer(150), new Double(0.85) },
			{ new Integer(80),  new Integer(2),  new Integer(150), new Double(0.85) },
			{ new Integer(1),   new Integer(15), new Integer(150), new Double(0.85) },
			{ new Integer(5),   new Integer(15), new Integer(150), new Double(0.85) },
			{ new Integer(80),  new Integer(15), new Integer(150), new Double(0.85) }
		};
	}

	/**
	 *  Create a population of Float64Genes
	 */
	public static final Population<Float64Gene, Float64> newFloat64GenePopulation(
		final int ngenes,
		final int nchromosomes,
		final int npopulation
	) {
		final Array<Float64Chromosome> chromosomes =
			new Array<>(nchromosomes);

		for (int i = 0; i < nchromosomes; ++i) {
			chromosomes.set(i, new Float64Chromosome(0, 10, ngenes));
		}

		final Genotype<Float64Gene> genotype = Genotype.valueOf(chromosomes.toISeq());
		final Population<Float64Gene, Float64> population =
			new Population<>(npopulation);

		for (int i = 0; i < npopulation; ++i) {
			population.add(Phenotype.valueOf(genotype.newInstance(), FF, 0));
		}

		return population;
	}

	public static final Population<EnumGene<Float64>, Float64> newPermutationFloat64GenePopulation(
		final int ngenes,
		final int nchromosomes,
		final int npopulation
	) {
		final Random random = RandomRegistry.getRandom();
		final Array<Float64> alleles = new Array<>(ngenes);
		for (int i = 0; i < ngenes; ++i) {
			alleles.set(i, Float64.valueOf(random.nextDouble()*10));
		}
		final ISeq<Float64> ialleles = alleles.toISeq();

		final Array<PermutationChromosome<Float64>> chromosomes =
			new Array<>(nchromosomes);

		for (int i = 0; i < nchromosomes; ++i) {
			chromosomes.set(i, new PermutationChromosome<>(ialleles));
		}

		final Genotype<EnumGene<Float64>> genotype = Genotype.valueOf(chromosomes.toISeq());
		final Population<EnumGene<Float64>, Float64> population =
			new Population<>(npopulation);

		for (int i = 0; i < npopulation; ++i) {
			population.add(Phenotype.valueOf(genotype.newInstance(), PFF, 0));
		}

		return population;
	}

	private static final Function<Genotype<EnumGene<Float64>>, Float64>
	PFF = new Function<Genotype<EnumGene<Float64>>, Float64>() {
		@Override
		public Float64 apply(Genotype<EnumGene<Float64>> value) {
			return value.getGene().getAllele();
		}
	};

	/**
	 * Count the number of different genes.
	 */
	public static int diff (
		final Population<Float64Gene, Float64> p1,
		final Population<Float64Gene, Float64> p2
	) {
		int count = 0;
		for (int i = 0; i < p1.size(); ++i) {
			final Genotype<?> gt1 = p1.get(i).getGenotype();
			final Genotype<?> gt2 = p2.get(i).getGenotype();

			for (int j = 0; j < gt1.length(); ++j) {
				final Chromosome<?> c1 = gt1.getChromosome(j);
				final Chromosome<?> c2 = gt2.getChromosome(j);

				for (int k = 0; k < c1.length(); ++k) {
					if (!c1.getGene(k).equals(c2.getGene(k))) {
						++count;
					}
				}
			}
		}
		return count;
	}

	/**
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @version $Id$
	 */
	private static final class Continous
		implements Function<Genotype<Float64Gene>, Float64>,
					Serializable
	{
		private static final long serialVersionUID = 1L;

		@Override
		public Float64 apply(Genotype<Float64Gene> genotype) {
			return genotype.getChromosome().getGene().getAllele();
		}
	}

	/**
	 * 'Identity' fitness function.
	 */
	public static final Function<Genotype<Float64Gene>, Float64> FF = new Continous();

	public static GeneticAlgorithm<Float64Gene, Float64> GA() {
		return new GeneticAlgorithm<>(
				Genotype.valueOf(new Float64Chromosome(0, 1)), FF
			);
	}


	public static Phenotype<Float64Gene, Float64> newFloat64Phenotype(final double value) {
		return Phenotype.valueOf(Genotype.valueOf(
				new Float64Chromosome(Float64Gene.valueOf(value, 0, 10))), FF, 0
			);
	}

	public static Phenotype<Float64Gene, Float64> newFloat64Phenotype() {
		final Random random = RandomRegistry.getRandom();
		return newFloat64Phenotype(random.nextDouble()*10);
	}

	public static Population<Float64Gene, Float64> newFloat64Population(final int length) {
		final Population<Float64Gene, Float64> population =
			new Population<>(length);

		for (int i = 0; i < length; ++i) {
			population.add(newFloat64Phenotype());
		}

		return population;
	}

}
