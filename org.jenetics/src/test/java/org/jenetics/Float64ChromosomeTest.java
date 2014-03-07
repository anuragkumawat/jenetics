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

import static org.jenetics.stat.StatisticsAssert.assertDistribution;
import static org.jenetics.util.accumulators.accumulate;

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jscience.mathematics.number.Float64;

import org.jenetics.stat.Histogram;
import org.jenetics.stat.UniformDistribution;
import org.jenetics.stat.Variance;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.Scoped;
import org.jenetics.util.accumulators.MinMax;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
@SuppressWarnings("deprecation")
public class Float64ChromosomeTest
	extends NumberChromosomeTester<Float64, Float64Gene>
{

	private final Float64Chromosome
	_factory = new Float64Chromosome(0, Double.MAX_VALUE, 500);
	@Override protected Float64Chromosome getFactory() {
		return _factory;
	}

	@Test(invocationCount = 20, successPercentage = 95)
    public void newInstanceDistribution() {
		try (Scoped<Random> s = RandomRegistry.scope(new Random(12345))) {
			final Float64 min = Float64.ZERO;
			final Float64 max = Float64.valueOf(100);


			final MinMax<Float64> mm = new MinMax<>();
			final Histogram<Float64> histogram = Histogram.valueOf(min, max, 10);
			final Variance<Float64> variance = new Variance<>();

			for (int i = 0; i < 1000; ++i) {
				final Float64Chromosome chromosome = new Float64Chromosome(min, max, 500);

				accumulate(
						chromosome,
						mm.map(Float64Gene.Value),
						histogram.map(Float64Gene.Value),
						variance.map(Float64Gene.Value)
					);
			}

			Assert.assertTrue(mm.getMin().compareTo(0) >= 0);
			Assert.assertTrue(mm.getMax().compareTo(100) <= 100);
			assertDistribution(histogram, new UniformDistribution<>(min, max));
		}
    }

	@Test
	public void firstGeneConverter() {
		final Float64Chromosome c = getFactory().newInstance();

		Assert.assertEquals(Float64Chromosome.Gene.apply(c), c.getGene(0));
	}

	@Test
	public void geneConverter() {
		final Float64Chromosome c = getFactory().newInstance();

		for (int i = 0; i < c.length(); ++i) {
			Assert.assertEquals(
					Float64Chromosome.Gene(i).apply(c),
					c.getGene(i)
				);
		}
	}

	@Test
	public void genesConverter() {
		final Float64Chromosome c = getFactory().newInstance();
		Assert.assertEquals(
				Float64Chromosome.Genes.apply(c),
				c.toSeq()
			);
	}

}
