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

import static org.jenetics.util.arrays.shuffle;

import java.util.Arrays;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

import org.jenetics.util.Accumulator.Max;


/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class accumulatorsMaxTest
	extends MappedAccumulatorTester<Accumulator.Max<Double>>
{

	final Factory<Accumulator.Max<Double>>
	_factory = new Factory<Accumulator.Max<Double>>() {
		@Override
		public Max<Double> newInstance() {
			final Random random = RandomRegistry.getRandom();

			final Max<Double> max = new Max<>();
			for (int i = 0; i < 1000; ++i) {
				max.accumulate(random.nextGaussian());
			}

			return max;
		}
	};
	@Override
	protected Factory<Max<Double>> getFactory() {
		return _factory;
	}

	@Test
	public void max() {
		final Integer[] array = new Integer[20];
		for (int i = 0; i < array.length; ++i) {
			array[i] = i;
		}
		shuffle(array);

		final Accumulator.Max<Integer> max = new Accumulator.Max<>();
		Accumulator.accumulate(Arrays.asList(array), max);
		Assert.assertEquals(max.getMax(), new Integer(19));
	}

}
