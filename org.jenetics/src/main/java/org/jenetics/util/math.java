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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.util;

import static java.lang.Math.abs;
import static java.lang.Math.exp;
import static java.lang.String.format;

import java.util.Random;

/**
 * This object contains mathematical helper functions.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.3 &mdash; <em>$Date$</em>
 */
public final class math extends StaticObject {
	private math() {}

	/**
	 * Add to long values and throws an ArithmeticException in the case of an
	 * overflow.
	 *
	 * @param a the first summand.
	 * @param b the second summand.
	 * @return the sum of the given values.
	 * @throws ArithmeticException if the summation would lead to an overflow.
	 */
	public static long plus(final long a, final long b) {
		final long z = a + b;
		if (((a^z) & (b^z)) < 0) {
			throw new ArithmeticException(format("Overflow: %d + %d", a, b));
		}

		return z;
	}

	/**
	 * Subtracts to long values and throws an ArithmeticException in the case of
	 * an overflow.
	 *
	 * @param a the minuend.
	 * @param b the subtrahend.
	 * @return the difference of the given values.
	 * @throws ArithmeticException if the subtraction would lead to an overflow.
	 */
	public static long minus(final long a, final long b) {
		final long z = a - b;
		if (((a^b) & (a^z)) < 0) {
			throw new ArithmeticException(format("Overflow: %d - %d", a, b));
		}

		return z;
	}

	/**
	 * Implementation of the <a href="http://en.wikipedia.org/wiki/Kahan_summation_algorithm">
	 * Kahan summation algorithm</a>.
	 *
	 * @param values the values to sum up.
	 * @return the sum of the given {@code values}.
	 * @throws NullPointerException if the given array is {@code null}.
	 *
	 * @deprecated Use {@link math.statistics#sum(double[])} instead.
	 */
	@Deprecated
	public static double sum(final double[] values) {
		return statistics.sum(values);
	}

	/**
	 * Add the values of the given array.
	 *
	 * @param values the values to add.
	 * @return the values sum.
	 * @throws NullPointerException if the values are null;
	 *
	 * @deprecated Use {@link math.statistics#sum(long[])} instead.
	 */
	@Deprecated
	public static long sum(final long[] values) {
		return statistics.sum(values);
	}

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
	 * Return the minimum value of the given double array.
	 *
	 * @param values the double array.
	 * @return the minimum value or {@link Double#NaN} if the given array is
	 *         empty.
	 * @throws NullPointerException if the given array is {@code null}.
	 *
	 * @deprecated Use {@link math.statistics#min(double[])} instead.
	 */
	@Deprecated
	public static double min(final double[] values) {
		return statistics.min(values);
	}

	/**
	 * Return the maximum value of the given double array.
	 *
	 * @param values the double array.
	 * @return the maximum value or {@link Double#NaN} if the given array is
	 *         empty.
	 * @throws NullPointerException if the given array is {@code null}.
	 *
	 * @deprecated Use {@link math.statistics#max(double[])} instead.
	 */
	@Deprecated
	public static double max(final double[] values) {
		return statistics.max(values);
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
	 * Component wise multiplication of the given double array.
	 *
	 * @param values the double values to multiply.
	 * @param multiplier the multiplier.
	 * @throws NullPointerException if the given double array is {@code null}.
	 */
	public static void times(final double[] values, final double multiplier) {
		for (int i = values.length; --i >= 0;) {
			values[i] *= multiplier;
		}
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

	static int gcd(final int a, final int b) {
		int x = a;
		int y = b;
		int mod = x%y;

		while (mod != 0) {
			x = y;
			y = mod;
			mod = x%y;
		}

		return y;
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
		return minus(ulpPosition(a), ulpPosition(b));
	}

	/**
	 * Calculating the <a href="http://en.wikipedia.org/wiki/Unit_in_the_last_place">ULP</a>
	 * position of a double number.
	 *
	 * [code]
	 * double a = 0.0;
	 * for (int i = 0; i < 10; ++i) {
	 *     a = Math.nextAfter(a, Double.POSITIVE_INFINITY);
	 * }
	 *
	 * for (int i = 0; i < 19; ++i) {
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
		long t = Double.doubleToLongBits(a);
		if (t < 0) {
			t = Long.MIN_VALUE - t;
		}
		return t;
	}

	static final class special extends StaticObject {
		private special() {}

		/**
		 * Return the <i>error function</i> of {@code z}. The fractional error
		 * of this implementation is less than 1.2E-7.
		 *
		 * @param z the value to calculate the error function for.
		 * @return the error function for {@code z}.
		 */
		static double erf(final double z) {
			final double t = 1.0/(1.0 + 0.5*abs(z));

			// Horner's method
			final double result = 1 - t*exp(
					-z*z - 1.26551223 +
					t*( 1.00002368 +
					t*( 0.37409196 +
					t*( 0.09678418 +
					t*(-0.18628806 +
					t*( 0.27886807 +
					t*(-1.13520398 +
					t*( 1.48851587 +
					t*(-0.82215223 +
					t*(0.17087277))))))))));

			return z >= 0 ? result : -result;
		}

		/**
		 * TODO: Implement gamma function.
		 *
		 * @param x
		 * @return
		 */
		static double Γ(final double x) {
			return x;
		}
		
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
	 * Mathematical functions regarding probabilities.
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.1
	 * @version 1.3 &mdash; <em>$Date$</em>
	 */
	static final class probability extends StaticObject {
		private probability() {}

		static final long INT_RANGE = pow(2, 32) - 1;

		/**
		 * Maps the probability, given in the range {@code [0, 1]}, to an
		 * integer in the range {@code [Integer.MIN_VALUE, Integer.MAX_VALUE]}.
		 *
		 * @see {@link #toInt(double)}
		 * @see {@link #toFloat(int)}
		 *
		 * @param probability the probability to widen.
		 * @return the widened probability.
		 */
		static int toInt(final float probability) {
			return Math.round(INT_RANGE*probability + Integer.MIN_VALUE);
		}

		/**
		 * Maps the probability, given in the range {@code [0, 1]}, to an
		 * integer in the range {@code [Integer.MIN_VALUE, Integer.MAX_VALUE]}.
		 *
		 * @see {@link #toInt(float)}
		 * @see {@link #toFloat(int)}
		 *
		 * @param probability the probability to widen.
		 * @return the widened probability.
		 */
		static int toInt(final double probability) {
			return (int)(Math.round(INT_RANGE*probability) + Integer.MIN_VALUE);
		}

		/**
		 * Maps the <i>integer</i> probability, within the range
		 * {@code [Integer.MIN_VALUE, Integer.MAX_VALUE]} back to a float
		 * probability within the range {@code [0, 1]}.
		 *
		 * @see {@link #toInt(float)}
		 * @see {@link #toInt(double)}
		 *
		 * @param probability the <i>integer</i> probability to map.
		 * @return the mapped probability within the range {@code [0, 1]}.
		 */
		static float toFloat(final int probability) {
			final long value = (long)probability + Integer.MAX_VALUE;
			return (float)(value/(double)INT_RANGE);
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
		 * Returns a pseudorandom, uniformly distributed int value between min
		 * and max (min and max included).
		 *
		 * @param min lower bound for generated integer
		 * @param max upper bound for generated integer
		 * @return a random integer greater than or equal to {@code min} and
		 *         less than or equal to {@code max}
		 * @throws IllegalArgumentException if {@code min >= max}
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
		 * Returns a pseudorandom, uniformly distributed int value between min
		 * and max (min and max included).
		 *
		 * @param min lower bound for generated long integer
		 * @param max upper bound for generated long integer
		 * @return a random long integer greater than or equal to {@code min}
		 *         and less than or equal to {@code max}
		 * @throws IllegalArgumentException if {@code min >= max}
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
		 * Returns a pseudorandom, uniformly distributed int value between 0
		 * (inclusive) and the specified value (exclusive), drawn from the given
		 * random number generator's sequence.
		 *
		 * @param random the random engine used for creating the random number.
		 * @param n the bound on the random number to be returned. Must be
		 *        positive.
		 * @return the next pseudorandom, uniformly distributed int value
		 *         between 0 (inclusive) and n (exclusive) from the given random
		 *         number generator's sequence
		 * @throws IllegalArgumentException if n is smaller than 1.
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
		 * Returns a pseudorandom, uniformly distributed double value between
		 * min (inclusively) and max (exclusively).
		 *
		 * @param random the random engine used for creating the random number.
		 * @param min lower bound for generated float value
		 * @param max upper bound for generated float value
		 * @return a random float greater than or equal to {@code min} and less
		 *         than to {@code max}
		 */
		public static float nextFloat(
			final Random random,
			final float min, final float max
		) {
			return random.nextFloat()*(max - min) + min;
		}

		/**
		 * Returns a pseudorandom, uniformly distributed double value between
		 * min (inclusively) and max (exclusively).
		 *
		 * @param random the random engine used for creating the random number.
		 * @param min lower bound for generated double value
		 * @param max upper bound for generated double value
		 * @return a random double greater than or equal to {@code min} and less
		 *         than to {@code max}
		 */
		public static double nextDouble(
			final Random random,
			final double min, final double max
		) {
			return random.nextDouble()*(max - min) + min;
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
		 * <p/>
		 * [code]
		 * public static long seed() {
		 *     return seed(System.nanoTime());
		 * }
		 * [/code]
		 * <p/>
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
		 * <p/>
		 * [code]
		 * public static long seed(final long base) {
		 *     final long objectHashSeed = ((long)(new Object().hashCode()) << 32) |
		 *                                         new Object().hashCode();
		 *     long seed = base ^ objectHashSeed;
		 *     seed ^= seed << 17;
		 *     seed ^= seed >>> 31;
		 *     seed ^= seed << 8;
		 *     return seed;
		 * }
		 * [/code]
		 *
		 * @param base the base value of the seed to create
		 * @return the created seed value.
		 */
		public static long seed(final long base) {
			long seed = base ^ objectHashSeed();
			seed ^= seed << 17;
			seed ^= seed >>> 31;
			seed ^= seed << 8;
			return seed;
		}


		private static long objectHashSeed() {
			return ((long)(new Object().hashCode()) << 32) |
							new Object().hashCode();
		}


		/*
		 * Conversion methods used by the 'Random' engine from the JDK.
		 */

		static float toFloat(final int a) {
			return (a >>> 8)/((float)(1 << 24));
		}

		static float toFloat(final long a) {
			return (int)(a >>> 40)/((float)(1 << 24));
		}

		static double toDouble(final long a) {
			return (((a >>> 38) << 27) + (((int)a) >>> 5))/(double)(1L << 53);
		}

		static double toDouble(final int a, final int b) {
			return (((long)(a >>> 6) << 27) + (b >>> 5))/(double)(1L << 53);
		}

		/*
		 * Conversion methods used by the Apache Commons BitStreamGenerator.
		 */

		static float toFloat2(final int a) {
			return (a >>> 9)*0x1.0p-23f;
		}

		static float toFloat2(final long a) {
			return (int)(a >>> 41)*0x1.0p-23f;
		}

		static double toDouble2(final long a) {
			return (a & 0xFFFFFFFFFFFFFL)*0x1.0p-52d;
		}

		static double toDouble2(final int a, final int b) {
			return (((long)(a >>> 6) << 26) | (b >>> 6))*0x1.0p-52d;
		}

	}

}




