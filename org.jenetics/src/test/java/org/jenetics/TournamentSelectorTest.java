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

import static java.lang.String.format;

import java.util.Arrays;
import java.util.stream.IntStream;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.internal.util.Named;

import org.jenetics.stat.Distribution;
import org.jenetics.stat.Histogram;
import org.jenetics.stat.StatisticsAssert;
import org.jenetics.stat.UniformDistribution;
import org.jenetics.util.Factory;
import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.Scoped;
import org.jenetics.util.TestData;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class TournamentSelectorTest
	extends SelectorTester<TournamentSelector<DoubleGene, Double>>
{

	@Override
	protected Factory<TournamentSelector<DoubleGene, Double>> factory() {
		return () -> new TournamentSelector<>(3);
	}

	@Override
	protected Distribution<Double> getDistribution() {
		return new UniformDistribution<>(getDomain());
	}

	@Override
	@Test
	public void selectDistribution() {
		//throw new SkipException("TODO: implement this test.");
	}

	@Test(dataProvider = "expectedDistribution", invocationCount = 20, successPercentage = 95)
	public void selectDist(
		final Integer tournamentSize,
		final Named<double[]> expected,
		final Optimize opt
	) {
		final int loops = (int)(tournamentSize*1.6);
		final int npopulation = POPULATION_COUNT;

		final ThreadLocal<LCG64ShiftRandom> random = new LCG64ShiftRandom.ThreadLocal();
		try (Scoped<LCG64ShiftRandom> sr = RandomRegistry.scope(random)) {
			final Histogram<Double> distribution = SelectorTester.distribution(
				new TournamentSelector<DoubleGene, Double>(tournamentSize),
				opt,
				npopulation,
				loops
			);

			StatisticsAssert.assertDistribution(distribution, expected.value, 0.00001);
		}
	}

	@DataProvider(name = "expectedDistribution")
	public Object[][] expectedDistribution() {
		final String resource =
			"/org/jenetics/selector/distribution/TournamentSelector";

		return Arrays.stream(Optimize.values())
			.flatMap(opt -> {
				final TestData data = TestData.of(resource, opt.toString());
				final double[][] csv = data.stream()
					.map(TestData::toDouble)
					.toArray(double[][]::new);

				final int[] sizes = TestData.toInt(csv[0]);

				return IntStream.range(0, sizes.length)
					.mapToObj(i -> new Object[]{
						sizes[i],
						Named.of(
							format("distribution[%d]", sizes[i]),
							expected(csv, i)
						),
						opt
					});
			}).toArray(Object[][]::new);
	}

	private static double[] expected(final double[][] csv, final int c) {
		final double[] col = new double[csv.length - 1];
		for (int i = 0; i < col.length; ++i) {
			col[i] = Math.max(csv[i + 1][c], Double.MIN_VALUE);
		}
		return col;
	}

	public static void main(final String[] args) {
		writeDistributionData(Optimize.MAXIMUM);
        writeDistributionData(Optimize.MINIMUM);
	}

	private static void writeDistributionData(final Optimize opt) {
		final ThreadLocal<LCG64ShiftRandom> random = new LCG64ShiftRandom.ThreadLocal();
		try (Scoped<LCG64ShiftRandom> sr = RandomRegistry.scope(random)) {

			final int npopulation = POPULATION_COUNT;
			final int loops = 5_000_000;
			//final int loops = 100_000;

			printDistributions(
				System.out,
				Arrays.asList(2, 3, 4, 5, 6, 7, 13, 23, 37),
				TournamentSelector::new,
				opt,
				npopulation,
				loops
			);
		}
	}


}
