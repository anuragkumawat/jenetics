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

import static org.jenetics.TestUtils.newFloat64GenePopulation;
import static org.jenetics.stat.StatisticsAssert.assertDistribution;

import java.util.Random;

import javolution.context.LocalContext;

import org.jscience.mathematics.number.Float64;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.stat.Histogram;
import org.jenetics.stat.NormalDistribution;
import org.jenetics.stat.Variance;
import org.jenetics.util.CharSeq;
import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.Range;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class SinglePointCrossoverTest {

	private static final class ConstRandom extends Random {
		private static final long serialVersionUID = 1L;
		private final int _value;

		public ConstRandom(final int value) {
			_value = value;
		}

		@Override
		public int nextInt() {
			return _value;
		}

		@Override
		public int nextInt(int n) {
			return _value;
		}

	}

	@Test
	public void crossover() {
		final CharSeq chars = CharSeq.valueOf("a-zA-Z");

		final ISeq<CharacterGene> g1 = new CharacterChromosome(chars, 20).toSeq();
		final ISeq<CharacterGene> g2 = new CharacterChromosome(chars, 20).toSeq();

		LocalContext.enter();
		try {
			final SinglePointCrossover<CharacterGene>
			crossover = new SinglePointCrossover<>();

			int rv = 12;
			RandomRegistry.setRandom(new ConstRandom(rv));
			MSeq<CharacterGene> g1c = g1.copy();
			MSeq<CharacterGene> g2c = g2.copy();
			crossover.crossover(g1c, g2c);

			Assert.assertEquals(g1c.subSeq(0, rv), g1.subSeq(0, rv));
			Assert.assertEquals(g1c.subSeq(rv), g2.subSeq(rv));
			Assert.assertNotEquals(g1c, g2);
			Assert.assertNotEquals(g2c, g1);

			rv = 0;
			RandomRegistry.setRandom(new ConstRandom(rv));
			g1c = g1.copy();
			g2c = g2.copy();
			crossover.crossover(g1c, g2c);
			Assert.assertEquals(g1c, g2);
			Assert.assertEquals(g2c, g1);
			Assert.assertEquals(g1c.subSeq(0, rv), g1.subSeq(0, rv));
			Assert.assertEquals(g1c.subSeq(rv), g2.subSeq(rv));

			rv = 1;
			RandomRegistry.setRandom(new ConstRandom(rv));
			g1c = g1.copy();
			g2c = g2.copy();
			crossover.crossover(g1c, g2c);
			Assert.assertEquals(g1c.subSeq(0, rv), g1.subSeq(0, rv));
			Assert.assertEquals(g1c.subSeq(rv), g2.subSeq(rv));

			rv = g1.length();
			RandomRegistry.setRandom(new ConstRandom(rv));
			g1c = g1.copy();
			g2c = g2.copy();
			crossover.crossover(g1c, g2c);
			Assert.assertEquals(g1c, g1);
			Assert.assertEquals(g2c, g2);
			Assert.assertEquals(g1c.subSeq(0, rv), g1.subSeq(0, rv));
			Assert.assertEquals(g1c.subSeq(rv), g2.subSeq(rv));
		} finally {
			LocalContext.exit();
		}
	}

	@Test(dataProvider = "alterProbabilityParameters")
	public void alterProbability(
		final Integer ngenes,
		final Integer nchromosomes,
		final Integer npopulation,
		final Double p
	) {
		final Population<Float64Gene, Float64> population = newFloat64GenePopulation(
				ngenes, nchromosomes, npopulation
			);

		// The mutator to test.
		final SinglePointCrossover<Float64Gene> crossover = new SinglePointCrossover<>(p);

		final long nallgenes = ngenes*nchromosomes*npopulation;
		final long N = 200;
		final double mean = crossover.getOrder()*npopulation*p;

		final long min = 0;
		final long max = nallgenes;
		final Range<Long> domain = new Range<>(min, max);

		final Histogram<Long> histogram = Histogram.valueOf(min, max, 10);
		final Variance<Long> variance = new Variance<>();

		for (int i = 0; i < N; ++i) {
			final long alterations = crossover.alter(population, 1);
			histogram.accumulate(alterations);
			variance.accumulate(alterations);
		}

		// Normal distribution as approximation for binomial distribution.
		assertDistribution(histogram, new NormalDistribution<>(domain, mean, variance.getVariance()));
	}


	@DataProvider(name = "alterProbabilityParameters")
	public Object[][] alterProbabilityParameters() {
		return TestUtils.alterProbabilityParameters();
	}

}


















