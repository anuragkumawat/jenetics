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

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.util.function.Function;

import org.jenetics.DoubleChromosome;
import org.jenetics.DoubleGene;
import org.jenetics.GeneticAlgorithm;
import org.jenetics.Genotype;
import org.jenetics.MeanAlterer;
import org.jenetics.Mutator;
import org.jenetics.NumberStatistics;
import org.jenetics.Optimize;
import org.jenetics.engine.EvolutionEngine;
import org.jenetics.util.Factory;

final class Real
	implements Function<Genotype<DoubleGene>, Double>
{
	@Override
	public Double apply(Genotype<DoubleGene> genotype) {
		final double x = genotype.getGene().doubleValue();
		return cos(0.5 + sin(x)) * cos(x);
	}
}

public class RealFunction {
	public static void main(String[] args) {
		Factory<Genotype<DoubleGene>> gtf = Genotype.of(
			new DoubleChromosome(0.0, 2.0 * PI)
		);
		Function<Genotype<DoubleGene>, Double> ff = new Real();
		GeneticAlgorithm<DoubleGene, Double> ga =
			new GeneticAlgorithm<>(
				gtf, ff, Optimize.MINIMUM
			);

		ga.setStatisticsCalculator(
			new NumberStatistics.Calculator<DoubleGene, Double>()
		);
		ga.setPopulationSize(500);
		ga.setAlterers(
			new Mutator<>(0.03),
			new MeanAlterer<>(0.6)
		);

		ga.setup();
		ga.evolve(100);
		System.out.println(ga.getBestStatistics());
		System.out.println(ga.getBestPhenotype());

		RealFunction2.main();
	}
}

class RealFunction2 {

	private static Double evaluate(final Genotype<DoubleGene> gt) {
		final double x = gt.getGene().doubleValue();
		return cos(0.5 + sin(x)) * cos(x);
	}

	public static void main() {
		final EvolutionEngine<DoubleGene, Double> engine = EvolutionEngine.newBuilder(
				RealFunction2::evaluate,
				DoubleChromosome.of(0.0, 2.0*PI))
			.populationSize(500)
			.alterers(
				new Mutator<>(0.03),
				new MeanAlterer<>(0.6))
			.optimize(Optimize.MINIMUM)
			.build();

		final Object best = engine.stream().limit(100)
			.collect(engine.BestEvolutionResult)
			.getBestPhenotype();

		System.out.println("Best: " + best);

	}

}
