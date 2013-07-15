/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.util;

import static org.jenetics.internal.math.random.toDouble;
import static org.jenetics.internal.math.random.toDouble2;
import static org.jenetics.internal.math.random.toFloat;
import static org.jenetics.internal.math.random.toFloat2;

import java.util.Arrays;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.stat.Histogram;
import org.jenetics.stat.StatisticsAssert;
import org.jenetics.stat.UniformDistribution;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date: 2013-07-15 $</em>
 */
public class mathRandomTest {

	@Test
	public void seed() {
		for (int i = 0; i < 100; ++i) {
			final long seed1 = math.random.seed();
			final long seed2 = math.random.seed();
			Assert.assertNotEquals(seed1, seed2);
		}
	}

	@Test
	public void seedLong() {
		for (int i = 0; i < 100; ++i) {
			final long seed1 = math.random.seed(i);
			final long seed2 = math.random.seed(i);
			Assert.assertNotEquals(seed1, seed2);
		}
	}

	@Test
	public void seedBytes() {
		final int length = 123;

		for (int i = 0; i < 100; ++i) {
			final byte[] seed1 = math.random.seedBytes(length);
			final byte[] seed2 = math.random.seedBytes(length);
			Assert.assertFalse(Arrays.equals(seed1, seed2));
		}
	}

	@Test(invocationCount = 5)
	public void toFloat_int() {
		final Random random = new LCG64ShiftRandom();
		final Histogram<Double> histogram = Histogram.valueOf(0.0, 1.0, 15);

		for (int i = 0; i < 100000; ++i) {
			histogram.accumulate((double)toFloat(random.nextInt()));
		}

		final UniformDistribution<Double> distribution = new UniformDistribution<>(0.0, 1.0);
		StatisticsAssert.assertDistribution(histogram, distribution);
	}

	@Test(invocationCount = 5)
	public void toFloat_long() {
		final Random random = new LCG64ShiftRandom();
		final Histogram<Double> histogram = Histogram.valueOf(0.0, 1.0, 15);

		for (int i = 0; i < 100000; ++i) {
			histogram.accumulate((double)toFloat(random.nextLong()));
		}

		final UniformDistribution<Double> distribution = new UniformDistribution<>(0.0, 1.0);
		StatisticsAssert.assertDistribution(histogram, distribution);
	}

	@Test(invocationCount = 5)
	public void toDouble_long() {
		final Random random = new LCG64ShiftRandom();
		final Histogram<Double> histogram = Histogram.valueOf(0.0, 1.0, 15);

		for (int i = 0; i < 100000; ++i) {
			histogram.accumulate(toDouble(random.nextLong()));
		}

		final UniformDistribution<Double> distribution = new UniformDistribution<>(0.0, 1.0);
		StatisticsAssert.assertDistribution(histogram, distribution);
	}

	@Test(invocationCount = 5)
	public void toDouble_int_int() {
		final Random random = new LCG64ShiftRandom();
		final Histogram<Double> histogram = Histogram.valueOf(0.0, 1.0, 15);

		for (int i = 0; i < 100000; ++i) {
			final long value = random.nextLong();
			histogram.accumulate(toDouble((int)(value >>> 32), (int)value));
		}

		final UniformDistribution<Double> distribution = new UniformDistribution<>(0.0, 1.0);
		StatisticsAssert.assertDistribution(histogram, distribution);
	}

	@Test(invocationCount = 5)
	public void toFloat2_int() {
		final Random random = new LCG64ShiftRandom();
		final Histogram<Double> histogram = Histogram.valueOf(0.0, 1.0, 15);

		for (int i = 0; i < 100000; ++i) {
			histogram.accumulate((double)toFloat2(random.nextInt()));
		}

		final UniformDistribution<Double> distribution = new UniformDistribution<>(0.0, 1.0);
		StatisticsAssert.assertDistribution(histogram, distribution);
	}

	@Test(invocationCount = 5)
	public void toFloat2_long() {
		final Random random = new LCG64ShiftRandom();
		final Histogram<Double> histogram = Histogram.valueOf(0.0, 1.0, 15);

		for (int i = 0; i < 100000; ++i) {
			histogram.accumulate((double)toFloat2(random.nextLong()));
		}

		final UniformDistribution<Double> distribution = new UniformDistribution<>(0.0, 1.0);
		StatisticsAssert.assertDistribution(histogram, distribution);
	}

	@Test(invocationCount = 5)
	public void toDouble2_long() {
		final Random random = new LCG64ShiftRandom();
		final Histogram<Double> histogram = Histogram.valueOf(0.0, 1.0, 15);

		for (int i = 0; i < 100000; ++i) {
			histogram.accumulate(toDouble2(random.nextLong()));
		}

		final UniformDistribution<Double> distribution = new UniformDistribution<>(0.0, 1.0);
		StatisticsAssert.assertDistribution(histogram, distribution);
	}

	@Test(invocationCount = 5)
	public void toDouble2_int_int() {
		final Random random = new LCG64ShiftRandom();
		final Histogram<Double> histogram = Histogram.valueOf(0.0, 1.0, 15);

		for (int i = 0; i < 100000; ++i) {
			final long value = random.nextLong();
			histogram.accumulate(toDouble2((int)(value >>> 32), (int)value));
		}

		final UniformDistribution<Double> distribution = new UniformDistribution<>(0.0, 1.0);
		StatisticsAssert.assertDistribution(histogram, distribution);
	}

}









