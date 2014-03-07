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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.function.Function;

/**
 * Static helper methods concerning arrays.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version @__version__@ &mdash; <em>$Date$</em>
 */
public final class arrays extends StaticObject {
	private arrays() {}

	/**
	 * Swap two elements of an given array.
	 *
	 * @param <T> the array type.
	 * @param array the array
	 * @param i index of the first array element.
	 * @param j index of the second array element.
	 * @throws IndexOutOfBoundsException if <tt>i &lt; 0</tt> or
	 *			<tt>j &lt; 0</tt> or <tt>i &gt; a.length</tt> or
	 *			<tt>j &gt; a.length</tt>
	 * @throws NullPointerException if the give array is {@code null}.
	 *
	 * @deprecated Not used in the <i>Jenetics</i> library. Will be removed.
	 */
	@Deprecated
	public static <T> void swap(final T[] array, final int i, final int j) {
		final T old = array[i];
		array[i] = array[j];
		array[j] = old;
	}

	/**
	 * Calls the populationSort method on the {@link Arrays} class.
	 *
	 * @throws NullPointerException if the give array is {@code null}.
	 * @throws UnsupportedOperationException if the array is sealed
	 * 		  ({@code array.isSealed() == true}).
	 */
	public static <T extends Object & Comparable<? super T>> MSeq<T>
	sort(final MSeq<T> array)
	{
		Collections.sort(array.asList());
		return array;
	}

	/**
	 * Randomize the {@code array} using the given {@link Random} object. The used
	 * shuffling algorithm is from D. Knuth TAOCP, Seminumerical Algorithms,
	 * Third edition, page 142, Algorithm S (Selection sampling technique).
	 *
	 * @param array the {@code array} to randomize.
	 * @throws NullPointerException if the give array is {@code null}.
	 *
	 * @deprecated Not used in the <i>Jenetics</i> library. Will be removed.
	 */
	@Deprecated
	public static <T> T[] shuffle(final T[] array) {
		return shuffle(array, RandomRegistry.getRandom());
	}

	/**
	 * Randomize the {@code array} using the given {@link Random} object. The used
	 * shuffling algorithm is from D. Knuth TAOCP, Seminumerical Algorithms,
	 * Third edition, page 142, Algorithm S (Selection sampling technique).
	 *
	 * @param array the {@code array} to randomize.
	 * @param random the {@link Random} object to use for randomize.
	 * @param <T> the component type of the array to randomize.
	 * @throws NullPointerException if the give array or the random object is
	 *         {@code null}.
	 *
	 * @deprecated Not used in the <i>Jenetics</i> library. Will be removed.
	 */
	@Deprecated
	public static <T> T[] shuffle(final T[] array, final Random random) {
		for (int j = array.length - 1; j > 0; --j) {
			swap(array, j, random.nextInt(j + 1));
		}

		return array;
	}

	/**
	 * Randomize the {@code array} using the given {@link Random} object. The used
	 * shuffling algorithm is from D. Knuth TAOCP, Seminumerical Algorithms,
	 * Third edition, page 142, Algorithm S (Selection sampling technique).
	 *
	 * @param array the {@code array} to randomize.
	 * @throws NullPointerException if the give array is {@code null}.
	 *
	 * @deprecated Not used in the <i>Jenetics</i> library. Will be removed.
	 */
	@Deprecated
	public static <T> MSeq<T> shuffle(final MSeq<T> array) {
		return shuffle(array, RandomRegistry.getRandom());
	}

	/**
	 * Randomize the {@code array} using the given {@link Random} object. The used
	 * shuffling algorithm is from D. Knuth TAOCP, Seminumerical Algorithms,
	 * Third edition, page 142, Algorithm S (Selection sampling technique).
	 *
	 * @param array the {@code array} to randomize.
	 * @param random the {@link Random} object to use for randomize.
	 * @param <T> the component type of the array to randomize.
	 * @throws NullPointerException if the give array or the random object is
	 *          {@code null}.
	 *
	 * @deprecated Not used in the <i>Jenetics</i> library. Will be removed.
	 */
	@Deprecated
	public static <T> MSeq<T> shuffle(final MSeq<T> array, final Random random) {
		for (int j = array.length() - 1; j > 0; --j) {
			array.swap(j, random.nextInt(j + 1));
		}

		return array;
	}

	/**
	 * Reverses the part of the array determined by the to indexes.
	 *
	 * @param <T> the array type.
	 * @param array the array to reverse
	 * @param from the first index (inclusive)
	 * @param to the second index (exclusive)
	 * @throws IllegalArgumentException if <tt>from &gt; to</tt>
	 * @throws IndexOutOfBoundsException if <tt>from &lt; 0</tt> or
	 *          <tt>to &gt; a.length</tt>
	 * @throws NullPointerException if the give array is {@code null}.
	 *
	 * @deprecated Not used in the <i>Jenetics</i> library. Will be removed.
	 */
	@Deprecated
	public static <T> T[] reverse(final T[] array, final int from, final int to) {
		rangeCheck(array.length, from, to);

		int i = from;
		int j = to;
		while (i < j) {
			swap(array, i++, --j);
		}

		return array;
	}

	/**
	 * Reverses the given array in place.
	 *
	 * @param <T> the array type.
	 * @param array the array to reverse.
	 * @throws NullPointerException if the give array is {@code null}.
	 *
	 * @deprecated Not used in the <i>Jenetics</i> library. Will be removed.
	 */
	@Deprecated
	public static <T> T[] reverse(final T[] array) {
		return reverse(array, 0, array.length);
	}

	@Deprecated
	static void reverse(final byte[] array) {
		int i = 0;
		int j = array.length;
		while (i < j) {
			_swap(array, i++, --j);
		}
	}
	private static void _swap(final byte[] array, final int i, final int j) {
		final byte old = array[i];
		array[i] = array[j];
		array[j] = old;
	}

	private static void rangeCheck(int length, int from, int to) {
		if (from > to) {
			throw new IllegalArgumentException(
				"fromIndex(" + from + ") > toIndex(" + to+ ")"
			);
		}
		if (from < 0) {
			throw new ArrayIndexOutOfBoundsException(from);
		}
		if (to > length) {
			throw new ArrayIndexOutOfBoundsException(to);
		}
	}

	/**
	 * Return a array with the indexes of the partitions of an array with the
	 * given size. The length of the returned array is {@code min(size, prts) + 1}.
	 * <p/>
	 * Some examples:
	 * <pre>
	 * 	 partition(10, 3): [0, 3, 6, 10]
	 * 	 partition(15, 6): [0, 2, 4, 6, 9, 12, 15]
	 * 	 partition(5, 10): [0, 1, 2, 3, 4, 5]
	 * </pre>
	 *
	 * The following examples prints the start index (inclusive) and the end
	 * index (exclusive) of the {@code partition(15, 6)}.
	 * [code]
	 * int[] parts = partition(15, 6);
	 * for (int i = 0; i < parts.length - 1; ++i) {
	 *     System.out.println(i + ": " + parts[i] + "\t" + parts[i + 1]);
	 * }
	 * [/code]
	 * <pre>
	 * 	 0: 0 	2
	 * 	 1: 2 	4
	 * 	 2: 4 	6
	 * 	 3: 6 	9
	 * 	 4: 9 	12
	 * 	 5: 12	15
	 * </pre>
	 *
	 * This example shows how this can be used in an concurrent environment:
	 * [code]
	 * try (final Concurrency c = Concurrency.start()) {
	 *     final int[] parts = arrays.partition(population.size(), _maxThreads);
	 *
	 *     for (int i = 0; i < parts.length - 1; ++i) {
	 *         final int part = i;
	 *         c.execute(new Runnable() { @Override public void run() {
	 *             for (int j = parts[part + 1]; --j >= parts[part];) {
	 *                 population.get(j).evaluate();
	 *             }
	 *         }});
	 *     }
	 * }
	 * [/code]
	 *
	 * @param size the size of the array to partition.
	 * @param parts the number of parts the (virtual) array should be partitioned.
	 * @return the partition array with the length of {@code min(size, parts) + 1}.
	 * @throws IllegalArgumentException if {@code size} or {@code p} is less than one.
	 */
	public static int[] partition(final int size, final int parts) {
		if (size < 1) {
			throw new IllegalArgumentException(
				"Size must greater than zero: " + size
			);
		}
		if (parts < 1) {
			throw new IllegalArgumentException(
				"Number of partitions must greater than zero: " + parts
			);
		}

		final int pts = Math.min(size, parts);
		final int[] partition = new int[pts + 1];

		final int bulk = size/pts;
		final int rest = size%pts;
		assert ((bulk*pts + rest) == size);

		for (int i = 0, n = pts - rest; i < n; ++i) {
			partition[i] = i*bulk;
		}
		for (int i = 0, n = rest + 1; i < n; ++i) {
			partition[pts - rest + i] = (pts - rest)*bulk + i*(bulk + 1);
		}

		return partition;
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
	 *
	 * @deprecated Use {@link math#subset(int, int)} instead.
	 */
	@Deprecated
	public static int[] subset(final int n, final int k) {
		return math.subset(n, k);
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
	 *          {@code n*k} will cause an integer overflow.
	 * @return the subset array.
	 *
	 * @deprecated Use {@link math#subset(int, int, Random)} instead.
	 */
	@Deprecated
	public static int[] subset(final int n, final int k, final Random random) {
		return math.subset(n, k, random);
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
	 *          {@code sub.length == 0} or {@code n*sub.length} will cause an
	 *          integer overflow.
	 *
	 * @deprecated Use {@link math#subset(int, int[])} instead.
	 */
	@Deprecated
	public static void subset(final int n, final int sub[]) {
		math.subset(n, sub);
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
	 * @throws NullPointerException if {@code sub} or {@code random} is
	 *         {@code null}.
	 * @throws IllegalArgumentException if {@code n < sub.length},
	 *         {@code sub.length == 0} or {@code n*sub.length} will cause an
	 *         integer overflow.
	 *
	 * @deprecated Use {@link math#subset(int, int[], Random)} instead.
	 */
	@Deprecated
	public static int[] subset(final int n, final int sub[], final Random random) {
		return math.subset(n, sub, random);
	}


	/**
	 * Returns the index of the first occurrence of the specified element in
	 * the {@code array}, or -1 if the {@code array} does not contain the element.
	 * @param array the array to search.
	 * @param start the start index of the search.
	 * @param element the element to search for.
	 * @return the index of the first occurrence of the specified element in the
	 *          given {@code array}, of -1 if the {@code array} does not contain
	 *          the element.
	 * @throws NullPointerException if the given {@code array} is {@code null}.
	 * @throws IndexOutOfBoundsException for an illegal end point index value
	 *          (start < 0 || end > length || start > end)
	 *
	 * @deprecated Not used in the <i>Jenetics</i> library. Will be removed.
	 */
	@Deprecated
	public static int indexOf(
		final Object[] array, final int start, final int end,
		final Object element
	) {
		requireNonNull(array, "Array");
		if (start < 0 || end > array.length || start > end) {
			throw new IndexOutOfBoundsException(format(
				"Invalid index range: [%d, %s]", start, end
			));
		}

		int index = -1;
		if (element != null) {
			for (int i = start; i < end && index == -1; ++i) {
				if (element.equals(array[i])) {
					index = i;
				}
			}
		} else {
			for (int i = start; i < end && index == -1; ++i) {
				if (array[i] == null) {
					index = i;
				}
			}
		}

		return index;
	}


	/**
	 * Returns the index of the first occurrence of the specified element in
	 * the {@code array}, or -1 if the {@code array} does not contain the element.
	 * @param array the array to search.
	 * @param element the element to search for.
	 * @return the index of the first occurrence of the specified element in the
	 *          given {@code array}, of -1 if the {@code array} does not contain
	 *          the element.
	 * @throws NullPointerException if the given {@code array} is {@code null}.
	 *
	 * @deprecated Not used in the <i>Jenetics</i> library. Will be removed.
	 */
	@Deprecated
	public static int indexOf(final Object[] array, final Object element) {
		return indexOf(array, 0, array.length, element);
	}

	/**
	 * Map the array from type A to an other array of type B.
	 *
	 * @param <A> the source type.
	 * @param <B> the target type.
	 * @param a the source array.
	 * @param b the target array. If the given array is to short a new array
	 *        with the right size is created, mapped and returned. If the given
	 *        array is long enough <i>this</i> array is returned.
	 * @param converter the converter needed for mapping from type A to type B.
	 * @return the mapped array. If {@code b} is long enough {@code b} is
	 *         returned otherwise a new created array.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public static <A, B> B[] map(
		final A[] a,
		final B[] b,
		final Function<? super A, ? extends B> converter
	) {
		requireNonNull(a, "Source array");
		requireNonNull(b, "Target array");
		requireNonNull(converter, "Converter");

		B[] result = b;
		if (b.length < a.length) {
			@SuppressWarnings("unchecked")
			final B[] r = (B[])java.lang.reflect.Array.newInstance(
				b.getClass().getComponentType(), a.length
			);
			result = r;
		}

		for (int i = 0; i < result.length; ++i) {
			result[i] = converter.apply(a[i]);
		}

		return result;
	}

}
