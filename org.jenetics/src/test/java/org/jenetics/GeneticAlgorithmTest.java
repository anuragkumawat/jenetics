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
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.function.Predicate;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

import org.jenetics.util.Concurrent;
import org.jenetics.util.Factory;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.Scoped;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class GeneticAlgorithmTest {

	private static class FF
		implements Function<Genotype<DoubleGene>, Double>,
					Serializable
	{
		private static final long serialVersionUID = 618089611921083000L;

		@Override
		public Double apply(final Genotype<DoubleGene> genotype) {
			return genotype.getGene().getAllele();
		}
	}

	@Test
	public void optimize() {
		final Random random = new Random(123456);
		try (Scoped<Random> rs = RandomRegistry.scope(random);
			Scoped<Concurrent> cs = Concurrent.serial())
		{
			Assert.assertSame(random, RandomRegistry.getRandom());
			Assert.assertSame(random, rs.get());

			final Factory<Genotype<DoubleGene>> factory = Genotype.of(
				DoubleChromosome.of(0, 1)
			);
			final Function<Genotype<DoubleGene>, Double> ff = new FF();

			final GeneticAlgorithm<DoubleGene, Double> ga = new GeneticAlgorithm<>(factory, ff);
			ga.setPopulationSize(200);
			ga.setAlterer(new MeanAlterer<>());
			ga.setOffspringFraction(0.3);
			ga.setOffspringSelector(new RouletteWheelSelector<>());
			ga.setSurvivorSelector(new TournamentSelector<>());

			ga.setup();
			ga.evolve(100);

			Statistics<DoubleGene, Double> s = ga.getBestStatistics();
			Reporter.log(s.toString());
			Assert.assertEquals(s.getAgeMean(), 21.40500000000002);
			Assert.assertEquals(s.getAgeVariance(), 648.051231155779);
			Assert.assertEquals(s.getSamples(), 200);
			Assert.assertEquals(s.getBestFitness(), 0.9955101231254028, 0.00000001);
			Assert.assertEquals(s.getWorstFitness(), 0.03640144995042627, 0.00000001);

			s = ga.getStatistics();
			Reporter.log(s.toString());

			Assert.assertEquals(s.getAgeMean(), 23.15500000000001, 0.000001);
			Assert.assertEquals(s.getAgeVariance(), 82.23213567839196, 0.000001);
			Assert.assertEquals(s.getSamples(), 200);
			Assert.assertEquals(s.getBestFitness(), 0.9955101231254028, 0.00000001);
			Assert.assertEquals(s.getWorstFitness(), 0.9955101231254028, 0.00000001);
		}

		Assert.assertNotNull(Concurrent.getExecutor());
	}

	private static class Base implements Comparable<Base> {
		@Override public int compareTo(Base o) {
			return 0;
		}
	}

	public static class Derived extends Base {
	}

	@SuppressWarnings("null")
	public void evolve() {
		Predicate<Statistics<? extends Float64Gene, ? extends Base>> until = null;
		GeneticAlgorithm<Float64Gene, Derived> ga = null;

		ga.evolve(until);
		ga.evolve(termination.Generation(1));

		GeneticAlgorithm<DoubleGene, Double> ga2 = null;
		ga2.evolve(termination.<Double>SteadyFitness(10));
	}

	@Test(invocationCount = 10)
	public void evolveForkJoinPool() {
		final ForkJoinPool pool = new ForkJoinPool(10);

		try (Scoped<Concurrent> concurrent = Concurrent.scope(pool)) {
			final Factory<Genotype<DoubleGene>> factory = Genotype.of(DoubleChromosome.of(-1, 1));
			final Function<Genotype<DoubleGene>, Double> ff = new FF();

			final GeneticAlgorithm<DoubleGene, Double> ga = new GeneticAlgorithm<>(factory, ff);
			ga.setPopulationSize(1000);
			ga.setAlterer(new MeanAlterer<>());
			ga.setOffspringFraction(0.3);
			ga.setOffspringSelector(new RouletteWheelSelector<>());
			ga.setSurvivorSelector(new StochasticUniversalSelector<>());

			ga.setup();
			for (int i = 0; i < 10; ++i) {
				ga.evolve();
			}
		} finally {
			pool.shutdown();
		}
	}

			ga.setAlterer(new MeanAlterer<DoubleGene>());
			ga.setOffspringFraction(0.3);
			ga.setOffspringSelector(new RouletteWheelSelector<DoubleGene, Double>());
			ga.setSurvivorSelector(new StochasticUniversalSelector<DoubleGene, Double>());

			ga.setup();
			for (int i = 0; i < 10; ++i) {
				ga.evolve();
			}
		} finally {
			pool.shutdown();
		}
	}

}
