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

import java.io.IOException;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.util.Factory;
import org.jenetics.util.MappedAccumulatorTester;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.TestDataIterator;
import org.jenetics.util.TestDataIterator.Data;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class VarianceTest extends MappedAccumulatorTester<Variance<Double>> {

	private final static String DATA = "/org/jenetics/util/statistic-moments.txt";

	private final Factory<Variance<Double>> _factory = new Factory<Variance<Double>>() {
		@Override
		public Variance<Double> newInstance() {
			final Random random = RandomRegistry.getRandom();

			final Variance<Double> variance = new Variance<>();
			for (int i = 0; i < 1000; ++i) {
				variance.accumulate(random.nextGaussian());
			}

			return variance;
		}
	};
	@Override
	protected Factory<Variance<Double>> getFactory() {
		return _factory;
	}

	@Test
	public void variance() throws IOException {
		try (TestDataIterator it = dataIt()) {
			final Variance<Double> moment = new Variance<>();
			while (it.hasNext()) {
				final Data data = it.next();
				moment.accumulate(data.number);

				Assert.assertEquals(moment.getMean(), data.mean);
				Assert.assertEquals(moment.getVariance(), data.variance, 0.000001);
			}
		}
	}

	private static TestDataIterator dataIt() throws IOException {
		return new TestDataIterator(
			MeanTest.class.getResourceAsStream(DATA), "\\s"
		);
	}

}
