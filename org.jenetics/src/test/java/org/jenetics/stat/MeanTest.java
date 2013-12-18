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

import java.util.Random;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.util.Factory;
import org.jenetics.util.MappedAccumulatorTester;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class MeanTest extends MappedAccumulatorTester<Mean<Double>> {

	private final Factory<Mean<Double>> _factory = new Factory<Mean<Double>>() {
		@Override
		public Mean<Double> newInstance() {
			final Random random = RandomRegistry.getRandom();

			final Mean<Double> mean = new Mean<>();
			for (int i = 0; i < 1000; ++i) {
				mean.accumulate(random.nextDouble());
			}

			return mean;
		}
	};
	@Override
	protected Factory<Mean<Double>> getFactory() {
		return _factory;
	}

	@Test
	public void mean() {
		final int length = 100_000;
		final Random random = new Random(234234);
		final SummaryStatistics stat = new SummaryStatistics();
		final Mean<Double> mean = new Mean<>();

		for (int i = 0; i < length; ++i) {
			final double value = random.nextDouble()*1_000;
			stat.addValue(value);
			mean.accumulate(value);
		}

		Assert.assertEquals(mean.getMean(), stat.getMean(), 0.0000001);
	}

}





