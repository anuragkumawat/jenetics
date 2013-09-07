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
package org.jenetics.stat;

import static java.lang.Math.floor;
import static java.lang.Math.sqrt;

import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.util.Factory;
import org.jenetics.util.LCG64ShiftRandom;
import org.jenetics.util.MappedAccumulatorTester;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class QuantileTest extends MappedAccumulatorTester<Quantile<Double>> {

	private final Factory<Quantile<Double>> _factory = new Factory<Quantile<Double>>() {
		@Override
		public Quantile<Double> newInstance() {
			final Random random = RandomRegistry.getRandom();

			final Quantile<Double> quantile = new Quantile<>(random.nextDouble());
			for (int i = 0; i < 1000; ++i) {
				quantile.accumulate(random.nextGaussian());
			}

			return quantile;
		}
	};
	@Override
	protected Factory<Quantile<Double>> getFactory() {
		return _factory;
	}


	@Test
	public void median() {
		final Quantile<Integer> quantile = Quantile.median();
		for (int i = 0; i < 1000; ++i) {
			quantile.accumulate(i);
			Assert.assertEquals(quantile.getValue(), floor(i/2.0), 1.0);
		}
	}

	@Test(dataProvider = "quantiles")
	public void quantile(final Double q) {
		final Random random = new LCG64ShiftRandom(1234);
		final Quantile<Double> quantile = new Quantile<>(q);

		final int N = 2_000_000;
		for (int i = 0; i < N; ++i) {
			quantile.accumulate(random.nextDouble());
		}

		Assert.assertEquals(quantile.getSamples(), N);
		Assert.assertEquals(quantile.getValue(), q, 1.0/sqrt(N));
	}

	@DataProvider(name = "quantiles")
	public Object[][] getQuantiles() {
		return new Double[][] {
			{0.01},
			{0.0123},
			{0.1},
			{0.25},
			{0.33},
			{0.45},
			{0.5},
			{0.57},
			{0.83},
			{0.93}
		};
	}

	@Test
	public void reset() {
		final Quantile<Integer> quantile = Quantile.median();
		for (int i = 0; i < 1000; ++i) {
			quantile.accumulate(i);
			Assert.assertEquals(quantile.getValue(), floor(i/2.0), 1.0);
		}

		quantile.reset();

		for (int i = 0; i < 1000; ++i) {
			quantile.accumulate(i);
			Assert.assertEquals(quantile.getValue(), floor(i/2.0), 1.0);
		}
	}


}




