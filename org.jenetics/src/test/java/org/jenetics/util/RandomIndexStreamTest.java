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
package org.jenetics.util;

import static org.jenetics.internal.math.random.indexes;
import static org.jenetics.stat.StatisticsAssert.assertDistribution;

import java.util.PrimitiveIterator.OfInt;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.internal.math.probability;
import org.jenetics.internal.math.random;
import org.jenetics.internal.util.IntRef;

import org.jenetics.stat.Histogram;
import org.jenetics.stat.NormalDistribution;
import org.jenetics.stat.Variance;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class RandomIndexStreamTest {

	@Test
	public void compatibility() {
		final TestData data = new TestData(
			"/org/jenetics/util/IndexStream.Random.dat"
		);

		for (String[] line : data) {
			final Random random = new LCG64ShiftRandom.ThreadSafe(0);
			final double p = Double.parseDouble(line[0]);

			final OfInt it = org.jenetics.internal.math.random.indexes(random, 500, p).iterator();
			for (int i = 1; i < line.length; ++i) {
				final int index = Integer.parseInt(line[i]);
				Assert.assertEquals(it.nextInt(), index);
			}

			Assert.assertFalse(it.hasNext());
		}
	}

	@Test
	public void reference() {
		final int size = 5000;
		final double p = 0.5;

		final Random random1 = new LCG64ShiftRandom(0);
		final Random random2 = new LCG64ShiftRandom(0);

		for (int j = 0; j < 1; ++j) {
			final OfInt it = random.indexes(random1, size, p).iterator();
			final IndexStream stream2 = ReferenceRandomStream(
				size, p, random2
			);

			while (it.hasNext()) {
				Assert.assertEquals(it.nextInt(), stream2.next());
			}

			Assert.assertFalse(it.hasNext());
			Assert.assertEquals(stream2.next(), -1);
		}
	}

	@Test(dataProvider = "probabilities")
	public void distribution(final Integer n, final Double p) {
		final double mean = n*p;
		final double var = n*p*(1 - p);

		final Random random = new LCG64ShiftRandom();
		final Range<Long> domain = new Range<>(0L, n.longValue());

		final Histogram<Long> histogram = Histogram.of(
					domain.getMin(), domain.getMax(), 10
				);
		final Variance<Long> variance = new Variance<>();
		for (int i = 0; i < 2500; ++i) {
			final long k = k(n, p, random);

			histogram.accept(k);
			variance.accumulate(k);
		}

		// Normal distribution as approximation for binomial distribution.
		assertDistribution(histogram, new NormalDistribution<>(domain, mean, var));
	}

	double var(final double p, final long N) {
		return N*p*(1.0 - p);
	}

	double mean(final double p, final long N) {
		return N*p;
	}

	long k(final int n, final double p, final Random random) {
		final IntRef kt = new IntRef(0);
		org.jenetics.internal.math.random.indexes(random, n, p).forEach(i -> {
			++kt.value;
		});

		return kt.value;
	}

	@DataProvider(name = "probabilities")
	public Object[][] probabilities() {
		return new Object[][] {
			//    n,                p
			{ new Integer(1115),  new Double(0.015) },
			{ new Integer(1150),  new Double(0.015) },
			{ new Integer(1160),  new Double(0.015) },
			{ new Integer(1170),  new Double(0.015) },
			{ new Integer(11100), new Double(0.015) },
			{ new Integer(11200), new Double(0.015) },
			{ new Integer(11500), new Double(0.015) },

			{ new Integer(1115),  new Double(0.15) },
			{ new Integer(1150),  new Double(0.15) },
			{ new Integer(1160),  new Double(0.15) },
			{ new Integer(1170),  new Double(0.15) },
			{ new Integer(11100), new Double(0.15) },
			{ new Integer(11200), new Double(0.15) },
			{ new Integer(11500), new Double(0.15) },

			{ new Integer(515),   new Double(0.5) },
			{ new Integer(1115),  new Double(0.5) },
			{ new Integer(1150),  new Double(0.5) },
			{ new Integer(1160),  new Double(0.5) },
			{ new Integer(1170),  new Double(0.5) },
			{ new Integer(11100), new Double(0.5) },
			{ new Integer(11200), new Double(0.5) },
			{ new Integer(11500), new Double(0.5) },

			{ new Integer(515),   new Double(0.85) },
			{ new Integer(1115),  new Double(0.85) },
			{ new Integer(1150),  new Double(0.85) },
			{ new Integer(1160),  new Double(0.85) },
			{ new Integer(1170),  new Double(0.85) },
			{ new Integer(11100), new Double(0.85) },
			{ new Integer(11200), new Double(0.85) },
			{ new Integer(11500), new Double(0.85) },

			{ new Integer(515),   new Double(0.99) },
			{ new Integer(1115),  new Double(0.99) },
			{ new Integer(1150),  new Double(0.99) },
			{ new Integer(1160),  new Double(0.99) },
			{ new Integer(1170),  new Double(0.99) },
			{ new Integer(11100), new Double(0.99) },
			{ new Integer(11200), new Double(0.99) },
			{ new Integer(11500), new Double(0.99) }
		};
	}


	public static IndexStream ReferenceRandomStream(
		final int n,
		final double p,
		final Random random
	) {
		return new IndexStream() {
			private final int P = probability.toInt(p);
			private int _pos = -1;
			@Override public int next() {
				while (_pos < n && random.nextInt() >= P) {
					++_pos;
				}
				return (_pos < n - 1) ? ++_pos : -1;
			}

		};
	}

	interface IndexStream {
		public int next();
	}


//	public static void main(final String[] args) {
//		final int delta = 500;
//
//		for (int i = 0; i <= delta; ++i) {
//			final double p = (double)(i)/(double)delta;
//			final Random random = new LCG64ShiftRandom.ThreadSafe(0);
//			final IndexStream stream = ReferenceRandomStream(delta, p, random);
//
//			System.out.print(Double.toString(p));
//			System.out.print(",");
//			for (int j = stream.next(); j != -1; j = stream.next()) {
//				System.out.print(j);
//				System.out.print(",");
//			}
//			System.out.println();
//		}
//	}

}
