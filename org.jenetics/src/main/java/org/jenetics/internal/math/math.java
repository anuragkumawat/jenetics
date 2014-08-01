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
package org.jenetics.internal.math;

import static java.lang.Double.doubleToLongBits;
import static java.lang.Math.nextDown;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.jenetics.internal.util.object.checkProbability;

import java.math.BigInteger;
import java.util.Random;
import java.util.stream.IntStream;

import org.jenetics.util.RandomRegistry;
import org.jenetics.util.StaticObject;

/**
 * This object contains mathematical helper functions.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 3.0 &mdash; <em>$Date$</em>
 */
public final class math extends StaticObject {
	private math() {}

	/**
	 * Normalize the given double array, so that it sum to one. The
	 * normalization is performed in place and the same {@code values} are
	 * returned.
	 *
	 * @param values the values to normalize.
	 * @return the {@code values} array.
	 * @throws NullPointerException if the given double array is {@code null}.
	 */
	public static double[] normalize(final double[] values) {
		final double sum = 1.0/statistics.sum(values);
		for (int i = values.length; --i >= 0;) {
			values[i] = values[i]*sum;
		}

		return values;
	}

	/**
	 * <i>Clamping</i> a value between a pair of boundary values.
	 * <i>Note: using clamp with floating point numbers may give unexpected
	 * results if one of the values is {@code NaN}.</i>
	 *
	 * @param v the value to <i>clamp</i>
	 * @param lo the lower bound.
	 * @param hi the upper bound.
	 * @return The clamped value:
	 *        <ul>
	 *            <li>{@code lo if v < lo}</li>
	 *            <li>{@code hi if hi < v}</li>
	 *            <li>{@code otherwise, v}</li>
	 *        </ul>
	 */
	public static double clamp(final double v, final double lo, final double hi) {
		return v < lo ? lo : (v > hi ? hi : v);
	}

	/**
	 * Component wise division of the given double array.
	 *
	 * @param values the double values to divide.
	 * @param divisor the divisor.
	 * @throws NullPointerException if the given double array is {@code null}.
	 */
	public static void divide(final double[] values, final double divisor) {
		for (int i = values.length; --i >= 0;) {
			values[i] /= divisor;
		}
	}

	/**
	 * Binary exponentiation algorithm.
	 *
	 * @param b the base number.
	 * @param e the exponent.
	 * @return {@code b^e}.
	 */
	public static long pow(final long b, final long e) {
		long base = b;
		long exp = e;
		long result = 1;

		while (exp != 0) {
			if ((exp & 1) != 0) {
				result *= base;
			}
			exp >>>= 1;
			base *= base;
		}

		return result;
	}

	static boolean isMultiplicationSave(final int a, final int b) {
		final long m = (long)a*(long)b;
		return ((int)m) == m;
	}

	/**
	 * Return the <a href="http://en.wikipedia.org/wiki/Unit_in_the_last_place">ULP</a>
	 * distance of the given two double values.
	 *
	 * @param a first double.
	 * @param b second double.
	 * @return the ULP distance.
	 * @throws ArithmeticException if the distance doesn't fit in a long value.
	 */
	public static long ulpDistance(final double a, final double b) {
		return Math.subtractExact(ulpPosition(a), ulpPosition(b));
	}

	/**
	 * Calculating the <a href="http://en.wikipedia.org/wiki/Unit_in_the_last_place">ULP</a>
	 * position of a double number.
	 *
	 * [code]
	 * double a = 0.0;
	 * for (int i = 0; i &lt; 10; ++i) {
	 *     a = Math.nextAfter(a, Double.POSITIVE_INFINITY);
	 * }
	 *
	 * for (int i = 0; i &lt; 19; ++i) {
	 *     a = Math.nextAfter(a, Double.NEGATIVE_INFINITY);
	 *     System.out.println(
	 *          a + "\t" + ulpPosition(a) + "\t" + ulpDistance(0.0, a)
	 *     );
	 * }
	 * [/code]
	 *
	 * The code fragment above will create the following output:
	 * <pre>
	 *   4.4E-323    9  9
	 *   4.0E-323    8  8
	 *   3.5E-323    7  7
	 *   3.0E-323    6  6
	 *   2.5E-323    5  5
	 *   2.0E-323    4  4
	 *   1.5E-323    3  3
	 *   1.0E-323    2  2
	 *   4.9E-324    1  1
	 *   0.0         0  0
	 *  -4.9E-324   -1  1
	 *  -1.0E-323   -2  2
	 *  -1.5E-323   -3  3
	 *  -2.0E-323   -4  4
	 *  -2.5E-323   -5  5
	 *  -3.0E-323   -6  6
	 *  -3.5E-323   -7  7
	 *  -4.0E-323   -8  8
	 *  -4.4E-323   -9  9
	 * </pre>
	 *
	 * @param a the double number.
	 * @return the ULP position.
	 */
	public static long ulpPosition(final double a) {
		long t = doubleToLongBits(a);
		if (t < 0) {
			t = Long.MIN_VALUE - t;
		}
		return t;
	}

	/**
	 * Selects a random subset of size {@code k} from a set of size {@code n}.
	 *
	 * @see #subset(int, int[])
	 *
	 * @param n the size of the set.
	 * @param k the size of the subset.
	 * @throws IllegalArgumentException if {@code n < k}, {@code k == 0} or if
	 *          {@code n*k} will cause an integer overflow.
	 * @return the subset array.
	 */
	public static int[] subset(final int n, final int k) {
		return subset(n, k, RandomRegistry.getRandom());
	}

	/**
	 * Selects a random subset of size {@code k} from a set of size {@code n}.
	 *
	 * @see #subset(int, int[], Random)
	 *
	 * @param n the size of the set.
	 * @param k the size of the subset.
	 * @param random the random number generator used.
	 * @throws NullPointerException if {@code random} is {@code null}.
	 * @throws IllegalArgumentException if {@code n < k}, {@code k == 0} or if
	 *         {@code n*k} will cause an integer overflow.
	 * @return the subset array.
	 */
	public static int[] subset(final int n, final int k, final Random random) {
		requireNonNull(random, "Random");
		if (k <= 0) {
			throw new IllegalArgumentException(format(
					"Subset size smaller or equal zero: %s", k
				));
		}
		if (n < k) {
			throw new IllegalArgumentException(format(
					"n smaller than k: %s < %s.", n, k
				));
		}

		final int[] sub = new int[k];
		subset(n, sub,random);
		return sub;
	}

	/**
	 * <p>
	 * Selects a random subset of size {@code sub.length} from a set of size
	 * {@code n}.
	 * </p>
	 *
	 * <p>
	 * <em>Authors:</em>
	 * 	 FORTRAN77 original version by Albert Nijenhuis, Herbert Wilf. This
	 * 	 version based on the  C++ version by John Burkardt.
	 * </p>
	 *
	 * <p><em><a href="https://people.scs.fsu.edu/~burkardt/c_src/subset/subset.html">
	 *  Reference:</a></em>
	 * 	 Albert Nijenhuis, Herbert Wilf,
	 * 	 Combinatorial Algorithms for Computers and Calculators,
	 * 	 Second Edition,
	 * 	 Academic Press, 1978,
	 * 	 ISBN: 0-12-519260-6,
	 * 	 LC: QA164.N54.
	 * </p>
	 *
	 * @param n the size of the set.
	 * @param sub the sub set array.
	 * @throws NullPointerException if {@code sub} is {@code null}.
	 * @throws IllegalArgumentException if {@code n < sub.length},
	 *         {@code sub.length == 0} or {@code n*sub.length} will cause an
	 *         integer overflow.
	 */
	public static void subset(final int n, final int sub[]) {
		subset(n, sub, RandomRegistry.getRandom());
	}

	/**
	 * <p>
	 * Selects a random subset of size {@code sub.length} from a set of size
	 * {@code n}.
	 * </p>
	 *
	 * <p>
	 * <em>Authors:</em>
	 *      FORTRAN77 original version by Albert Nijenhuis, Herbert Wilf. This
	 *      version based on the  C++ version by John Burkardt.
	 * </p>
	 *
	 * <p><em><a href="https://people.scs.fsu.edu/~burkardt/c_src/subset/subset.html">
	 *  Reference:</a></em>
	 *      Albert Nijenhuis, Herbert Wilf,
	 *      Combinatorial Algorithms for Computers and Calculators,
	 *      Second Edition,
	 *      Academic Press, 1978,
	 *      ISBN: 0-12-519260-6,
	 *      LC: QA164.N54.
	 * </p>
	 *
	 * @param n the size of the set.
	 * @param sub the sub set array.
	 * @param random the random number generator used.
	 * @return the sub-set array for the given parameter
	 * @throws NullPointerException if {@code sub} or {@code random} is
	 *         {@code null}.
	 * @throws IllegalArgumentException if {@code n < sub.length},
	 *         {@code sub.length == 0} or {@code n*sub.length} will cause an
	 *         integer overflow.
	 */
	public static int[] subset(final int n, final int sub[], final Random random) {
		requireNonNull(random, "Random");
		requireNonNull(sub, "Sub set array");

		final int k = sub.length;
		if (k <= 0) {
			throw new IllegalArgumentException(format(
				"Subset size smaller or equal zero: %s", k
			));
		}
		if (n < k) {
			throw new IllegalArgumentException(format(
				"n smaller than k: %s < %s.", n, k
			));
		}
		if (!math.isMultiplicationSave(n, k)) {
			throw new IllegalArgumentException(format(
				"n*sub.length > Integer.MAX_VALUE (%s*%s = %s > %s)",
				n, sub.length, (long)n*(long)k, Integer.MAX_VALUE
			));
		}

		if (sub.length == n) {
			for (int i = 0; i < sub.length; ++i) {
				sub[i] = i;
			}
			return sub;
		}

		for (int i = 0; i < k; ++i) {
			sub[i] = (i*n)/k;
		}

		int l = 0;
		int ix = 0;
		for (int i = 0; i < k; ++i) {
			do {
				ix = nextInt(random, 1, n);
				l = (ix*k - 1)/n;
			} while (sub[l] >= ix);

			sub[l] = sub[l] + 1;
		}

		int m = 0;
		int ip = 0;
		int is = k;
		for (int i = 0; i < k; ++i) {
			m = sub[i];
			sub[i] = 0;

			if (m != (i*n)/k) {
				ip = ip + 1;
				sub[ip - 1] = m;
			}
		}

		int ihi = ip;
		int ids = 0;
		for (int i = 1; i <= ihi; ++i) {
			ip = ihi + 1 - i;
			l = 1 + (sub[ip - 1]*k - 1)/n;
			ids = sub[ip - 1] - ((l - 1)*n)/k;
			sub[ip - 1] = 0;
			sub[is - 1] = l;
			is = is - ids;
		}

		int ir = 0;
		int m0 = 0;
		for (int ll = 1; ll <= k; ++ll) {
			l = k + 1 - ll;

			if (sub[l - 1] != 0) {
				ir = l;
				m0 = 1 + ((sub[l - 1] - 1)*n)/k;
				m = (sub[l-1]*n)/k - m0 + 1;
			}

			ix = nextInt(random, m0, m0 + m - 1);

			int i = l + 1;
			while (i <= ir && ix >= sub[i - 1]) {
				ix = ix + 1;
				sub[ i- 2] = sub[i - 1];
				i = i + 1;
			}

			sub[i - 2] = ix;
			--m;
		}

		return sub;
	}

	private static int nextInt(final Random random, final int a, final int b) {
		return a == b ? a - 1 : random.nextInt(b - a) + a;
	}

	/**
	 * Some helper method concerning statistics.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.3
	 * @version 1.3 &mdash; <em>$Date$</em>
	 */
	public static final class statistics extends StaticObject {
		private statistics() {}

		/**
		 * Return the minimum value of the given double array.
		 *
		 * @param values the double array.
		 * @return the minimum value or {@link Double#NaN} if the given array is
		 *         empty.
		 * @throws NullPointerException if the given array is {@code null}.
		 */
		public static double min(final double[] values) {
			double min = Double.NaN;
			if (values.length > 0) {
				min = values[0];

				for (int i = values.length; --i >= 1;) {
					if (values[i] < min) {
						min = values[i];
					}
				}
			}

			return min;
		}

		/**
		 * Return the maximum value of the given double array.
		 *
		 * @param values the double array.
		 * @return the maximum value or {@link Double#NaN} if the given array is
		 *         empty.
		 * @throws NullPointerException if the given array is {@code null}.
		 */
		public static double max(final double[] values) {
			double max = Double.NaN;
			if (values.length > 0) {
				max = values[0];

				for (int i = values.length; --i >= 1;) {
					if (values[i] > max) {
						max = values[i];
					}
				}
			}

			return max;
		}

		/**
		 * Implementation of the <a href="http://en.wikipedia.org/wiki/Kahan_summation_algorithm">
		 * Kahan summation algorithm</a>.
		 *
		 * @param values the values to sum up.
		 * @return the sum of the given {@code values}.
		 * @throws NullPointerException if the given array is {@code null}.
		 */
		public static double sum(final double[] values) {
			double sum = 0.0;
			double c = 0.0;
			double y = 0.0;
			double t = 0.0;

			for (int i = values.length; --i >= 0;) {
				y = values[i] - c;
				t = sum + y;
				c = t - sum - y;
				sum = t;
			}

			return sum;
		}

		/**
		 * Add the values of the given array.
		 *
		 * @param values the values to add.
		 * @return the values sum.
		 * @throws NullPointerException if the values are null;
		 */
		public static long sum(final long[] values) {
			long sum = 0;
			for (int i = values.length; --i >= 0;) {
				sum += values[i];
			}
			return sum;
		}

	}

	/**
	 * Some helper method concerning random numbers and random seed generation.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.1
	 * @version 1.2 &mdash; <em>$Date$</em>
	 */
	public static final class random extends StaticObject {
		private random() {}

		/**
		 * Create an {@code IntStream} which creates random indexes within the
		 * given range and the index probability.
		 *
		 * @since 3.0
		 *
		 * @param random the random engine used for calculating the random
		 *        indexes
		 * @param start the start index (inclusively)
		 * @param end the end index (exclusively)
		 * @param p the index selection probability
		 * @return an new random index stream
		 * @throws java.lang.IllegalArgumentException if {@code p} is not a
		 *         valid probability.
		 */
		public static IntStream indexes(
			final Random random,
			final int start,
			final int end,
			final double p
		) {
			checkProbability(p);
			return equals(p, 0, 1E-20) ?
				IntStream.empty() :
				equals(p, 1, 1E-20) ?
					IntStream.range(start, end) :
					IntStream.range(start, end)
						.filter(i -> random.nextInt() < probability.toInt(p));
		}

		private static
		boolean equals(final double a, final double b, final double delta) {
			return Math.abs(a - b) <= delta;
		}

		/**
		 * Create an {@code IntStream} which creates random indexes within the
		 * given range and the index probability.
		 *
		 * @since 3.0
		 *
		 * @param random the random engine used for calculating the random
		 *        indexes
		 * @param n the end index (exclusively). The start index is zero.
		 * @param p the index selection probability
		 * @return an new random index stream
		 * @throws java.lang.IllegalArgumentException if {@code p} is not a
		 *         valid probability.
		 * @throws java.lang.NullPointerException if the given {@code random}
		 *         engine is {@code null}.
		 */
		public static IntStream indexes(
			final Random random,
			final int n,
			final double p
		) {
			return indexes(random, 0, n, p);
		}

		/**
		 * Returns a pseudo-random, uniformly distributed int value between min
		 * and max (min and max included).
		 *
		 * @param random the random engine to use for calculating the random
		 *        int value
		 * @param min lower bound for generated integer
		 * @param max upper bound for generated integer
		 * @return a random integer greater than or equal to {@code min} and
		 *         less than or equal to {@code max}
		 * @throws IllegalArgumentException if {@code min >= max}
		 * @throws java.lang.NullPointerException if the given {@code random}
		 *         engine is {@code null}.
		 */
		public static int nextInt(
			final Random random,
			final int min, final int max
		) {
			if (min >= max) {
				throw new IllegalArgumentException(format(
					"Min >= max: %d >= %d", min, max
				));
			}

			final int diff = max - min + 1;
			int result = 0;

			if (diff <= 0) {
				do {
					result = random.nextInt();
				} while (result < min || result > max);
			} else {
				result = random.nextInt(diff) + min;
			}

			return result;
		}

		/**
		 * Returns a pseudo-random, uniformly distributed int value between min
		 * and max (min and max included).
		 *
		 * @param random the random engine to use for calculating the random
		 *        long value
		 * @param min lower bound for generated long integer
		 * @param max upper bound for generated long integer
		 * @return a random long integer greater than or equal to {@code min}
		 *         and less than or equal to {@code max}
		 * @throws IllegalArgumentException if {@code min >= max}
		 * @throws java.lang.NullPointerException if the given {@code random}
		 *         engine is {@code null}.
		 */
		public static long nextLong(
			final Random random,
			final long min, final long max
		) {
			if (min >= max) {
				throw new IllegalArgumentException(format(
					"min >= max: %d >= %d.", min, max
				));
			}

			final long diff = (max - min) + 1;
			long result = 0;

			if (diff <= 0) {
				do {
					result = random.nextLong();
				} while (result < min || result > max);
			} else if (diff < Integer.MAX_VALUE) {
				result = random.nextInt((int)diff) + min;
			} else {
				result = nextLong(random, diff) + min;
			}

			return result;
		}

		/**
		 * Returns a pseudo-random, uniformly distributed int value between 0
		 * (inclusive) and the specified value (exclusive), drawn from the given
		 * random number generator's sequence.
		 *
		 * @param random the random engine used for creating the random number.
		 * @param n the bound on the random number to be returned. Must be
		 *        positive.
		 * @return the next pseudo-random, uniformly distributed int value
		 *         between 0 (inclusive) and n (exclusive) from the given random
		 *         number generator's sequence
		 * @throws IllegalArgumentException if n is smaller than 1.
		 * @throws java.lang.NullPointerException if the given {@code random}
		 *         engine is {@code null}.
		 */
		public static long nextLong(final Random random, final long n) {
			if (n <= 0) {
				throw new IllegalArgumentException(format(
					"n is smaller than one: %d", n
				));
			}

			long bits;
			long result;
			do {
				bits = random.nextLong() & 0x7fffffffffffffffL;
				result = bits%n;
			} while (bits - result + (n - 1) < 0);

			return result;
		}

		/**
		 * Returns a pseudo-random, uniformly distributed double value between
		 * min (inclusively) and max (exclusively).
		 *
		 * @param random the random engine used for creating the random number.
		 * @param min lower bound for generated float value (inclusively)
		 * @param max upper bound for generated float value (exclusively)
		 * @return a random float greater than or equal to {@code min} and less
		 *         than to {@code max}
		 * @throws java.lang.NullPointerException if the given {@code random}
		 *         engine is {@code null}.
		 */
		public static float nextFloat(
			final Random random,
			final float min, final float max
		) {
			if (min >= max) {
				throw new IllegalArgumentException(format(
					"min >= max: %f >= %f.", min, max
				));
			}

			float value = random.nextFloat();
			if (min < max) {
				value = value*(max - min) + min;
				if (value >= max) {
					value = nextDown(value);
				}
			}

			return value;
		}

		/**
		 * Returns a pseudo-random, uniformly distributed double value between
		 * min (inclusively) and max (exclusively).
		 *
		 * @param random the random engine used for creating the random number.
		 * @param min lower bound for generated double value (inclusively)
		 * @param max upper bound for generated double value (exclusively)
		 * @return a random double greater than or equal to {@code min} and less
		 *         than to {@code max}
		 * @throws java.lang.NullPointerException if the given {@code random}
		 *         engine is {@code null}.
		 */
		public static double nextDouble(
			final Random random,
			final double min, final double max
		) {
			if (min >= max) {
				throw new IllegalArgumentException(format(
					"min >= max: %f >= %f.", min, max
				));
			}

			double value = random.nextDouble();
			if (min < max) {
				value = value*(max - min) + min;
				if (value >= max) {
					value = nextDown(value);
				}
			}

			return value;
		}

		/**
		 * Returns a pseudo-random, uniformly distributed int value between 0
		 * (inclusive) and the specified value (exclusive), drawn from the given
		 * random number generator's sequence.
		 *
		 * @param random the random engine used for creating the random number.
		 * @param n the bound on the random number to be returned. Must be
		 *        positive.
		 * @return the next pseudo-random, uniformly distributed int value
		 *         between 0 (inclusive) and n (exclusive) from the given random
		 *         number generator's sequence
		 * @throws IllegalArgumentException if n is smaller than 1.
		 * @throws java.lang.NullPointerException if the given {@code random}
		 *         engine of the maximal value {@code n} is {@code null}.
		 */
		public static BigInteger nextBigInteger(
			final Random random,
			final BigInteger n
		) {
			if (n.compareTo(BigInteger.ONE) < 0) {
				throw new IllegalArgumentException(format(
					"n is smaller than one: %d", n
				));
			}

			BigInteger result = null;
			if (n.bitLength() <= Integer.SIZE - 1) {
				result = BigInteger.valueOf(random.nextInt(n.intValue()));
			} else if (n.bitLength() <= Long.SIZE - 1) {
				result = BigInteger.valueOf(nextLong(random, n.longValue()));
			} else {
				do {
					result = new BigInteger(n.bitLength(), random);
				} while (result.compareTo(n) >= 0);
			}

			return result;
		}

		/**
		 * Returns a pseudo-random, uniformly distributed int value between min
		 * and max (min and max included).
		 *
		 * @param random the random engine to use for calculating the random
		 *        long value
		 * @param min lower bound for generated long integer (inclusively)
		 * @param max upper bound for generated long integer (inclusively)
		 * @return a random long integer greater than or equal to {@code min}
		 *         and less than or equal to {@code max}
		 * @throws IllegalArgumentException if {@code min >= max}
		 * @throws java.lang.NullPointerException if one of the given parameters
		 *         are {@code null}.
		 */
		public static BigInteger nextBigInteger(
			final Random random,
			final BigInteger min, final BigInteger max
		) {
			if (min.compareTo(max) >= 0) {
				throw new IllegalArgumentException(format(
					"min >= max: %d >= %d.", min, max
				));
			}

			return nextBigInteger(random, max.subtract(min).add(BigInteger.ONE))
						.add(min);
		}

		/**
		 * Create a new <em>seed</em> byte array of the given length.
		 *
		 * @see #seed(byte[])
		 * @see #seed()
		 *
		 * @param length the length of the returned byte array.
		 * @return a new <em>seed</em> byte array of the given length
		 * @throws NegativeArraySizeException if the given length is smaller
		 *         than zero.
		 */
		public static byte[] seedBytes(final int length) {
			return seed(new byte[length]);
		}

		/**
		 * Fills the given byte array with random bytes, created by successive
		 * calls of the {@link #seed()} method.
		 *
		 * @see #seed()
		 *
		 * @param seed the byte array seed to fill with random bytes.
		 * @return the given byte array, for method chaining.
		 * @throws NullPointerException if the {@code seed} array is
		 *         {@code null}.
		 */
		public static byte[] seed(final byte[] seed) {
			for (int i = 0, len = seed.length; i < len;) {
				int n = Math.min(len - i, Long.SIZE/Byte.SIZE);

				for (long x = seed(); n-- > 0; x >>= Byte.SIZE) {
					seed[i++] = (byte)x;
				}
			}

			return seed;
		}

		/**
		 * Calculating a 64 bit seed value which can be used for initializing
		 * PRNGs. This method uses a combination of {@code System.nanoTime()}
		 * and {@code new Object().hashCode()} calls to create a reasonable safe
		 * seed value:
		 * <p>
		 * [code]
		 * public static long seed() {
		 *     return seed(System.nanoTime());
		 * }
		 * [/code]
		 * <p>
		 * This method passes all of the statistical tests of the
		 * <a href="http://www.phy.duke.edu/~rgb/General/dieharder.php">
		 * dieharder</a> test suite&mdash;executed on a linux machine with
		 * JDK version 1.7. <em>Since there is no prove that this will the case
		 * for every Java version and OS, it is recommended to only use this
		 * method for seeding other PRNGs.</em>
		 *
		 * @see #seed(long)
		 *
		 * @return the random seed value.
		 */
		public static long seed() {
			return seed(System.nanoTime());
		}

		/**
		 * Uses the given {@code base} value to create a reasonable safe seed
		 * value. This is done by combining it with values of
		 * {@code new Object().hashCode()}:
		 * <p>
		 * [code]
		 * public static long seed(final long base) {
		 *     final long objectHashSeed = ((long)(new Object().hashCode()) &lt;&lt; 32) |
		 *                                         new Object().hashCode();
		 *     long seed = base ^ objectHashSeed;
		 *     seed ^= seed &lt;&lt; 17;
		 *     seed ^= seed &gt;&gt;&gt; 31;
		 *     seed ^= seed &lt;&lt; 8;
		 *     return seed;
		 * }
		 * [/code]
		 *
		 * @param base the base value of the seed to create
		 * @return the created seed value.
		 */
		public static long seed(final long base) {
			return mix(base, objectHashSeed());
		}

		private static long mix(final long a, final long b) {
			long c = a^b;
			c ^= c << 17;
			c ^= c >>> 31;
			c ^= c << 8;
			return c;
		}

		private static long objectHashSeed() {
			return ((long)(new Object().hashCode()) << 32) |
							new Object().hashCode();
		}

	}

}
