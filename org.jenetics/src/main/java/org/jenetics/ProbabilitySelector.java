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
package org.jenetics;

import static java.lang.Math.abs;
import static java.lang.Math.pow;
import static org.jenetics.util.math.sum;
import static org.jenetics.util.math.ulpDistance;
import static org.jenetics.util.object.nonNull;

import java.util.Random;

import org.jenetics.util.Factory;
import org.jenetics.util.RandomRegistry;


/**
 * Probability selectors are a variation of fitness proportional selectors and
 * selects individuals from a given population based on it's selection
 * probability <i>P(i)</i>.
 * <p><div align="center">
 * <img src="doc-files/FitnessProportionalSelection.svg" width="400" />
 * </p></div>
 * Fitness proportional selection works as shown in the figure above. The
 * runtime complexity of the implemented probability selectors is
 * <i>O(n+</i>log<i>(n))</i> instead of <i>O(n<sup>2</sup>)</i> as for the naive
 * approach: <i>A binary (index) search is performed on the summed probability
 * array.</i>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date$</em>
 */
public abstract class ProbabilitySelector<
	G extends Gene<?, G>,
	C extends Comparable<? super C>
>
	implements Selector<G, C>
{
	private static final long MAX_ULP_DISTANCE = (long)pow(10, 10);

	protected ProbabilitySelector() {
	}

	@Override
	public Population<G, C> select(
		final Population<G, C> population,
		final int count,
		final Optimize opt
	) {
		nonNull(population, "Population");
		nonNull(opt, "Optimization");
		if (count < 0) {
			throw new IllegalArgumentException(String.format(
				"Selection count must be greater or equal then zero, but was %s.",
				count
			));
		}

		final Population<G, C> selection = new Population<>(count);

		if (count > 0) {
			final double[] probabilities = probabilities(population, count, opt);
			assert (population.size() == probabilities.length) :
				"Population size and probability length are not equal.";
			assert (sum2one(probabilities)) : "Probabilities doesn't sum to one.";

			incremental(probabilities);
			final Factory<Phenotype<G, C>> factory = factory(
				population, probabilities, RandomRegistry.getRandom()
			);

			selection.fill(factory, count);
			assert (count == selection.size());
		}

		return selection;
	}

	private static <
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	>
	Factory<Phenotype<G, C>> factory(
		final Population<G, C> population,
		final double[] probabilities,
		final Random random
	) {
		return new Factory<Phenotype<G, C>>() {
			@Override
			public Phenotype<G, C> newInstance() {
				return select(population, probabilities, random);
			}
		};
	}

	private static <
		G extends Gene<?, G>,
		C extends Comparable<? super C>
	>
	Phenotype<G, C> select(
		final Population<G, C> population,
		final double[] probabilities,
		final Random random
	) {
		final double value = random.nextDouble();
		return population.get(indexOf(probabilities, value));
	}

	/**
	 * This method takes the probabilities from the
	 * {@link #probabilities(Population, int)} method and inverts it if needed.
	 *
	 * @param population The population.
	 * @param count The number of phenotypes to select.
	 * @param opt Determines whether the individuals with higher fitness values
	 *         or lower fitness values must be selected. This parameter determines
	 *         whether the GA maximizes or minimizes the fitness function.
	 * @return Probability array.
	 */
	protected final double[] probabilities(
		final Population<G, C> population,
		final int count,
		final Optimize opt
	) {
		final double[] probabilities = probabilities(population, count);
		if (opt == Optimize.MINIMUM) {
			invert(probabilities);
		}
		return probabilities;
	}

	private static void invert(final double[] probabilities) {
		for (int i = 0; i < probabilities.length; ++i) {
			probabilities[i] = 1.0 - probabilities[i];
		}
	}

	/**
	 * Return an Probability array, which corresponds to the given Population.
	 * The probability array and the population must have the same size. The
	 * population is not sorted. If a subclass needs a sorted population, the
	 * subclass is responsible to sort the population.
	 * <p/>
	 * The implementor always assumes that higher fitness values are better. The
	 * base class inverts the probabilities ({@code p = 1.0 - p }) if the GA is
	 * supposed to minimize the fitness function.
	 *
	 * @param population The <em>unsorted</em> population.
	 * @param count The number of phenotypes to select. <i>This parameter is not
	 *         needed for most implementations.</i>
	 * @return Probability array. The returned probability array must have the
	 *          length {@code population.size()} and <strong>must</strong> sum to
	 *          one. The returned value is checked with
	 *          {@code assert(Math.abs(math.sum(probabilities) - 1.0) < 0.0001)}
	 *          in the base class.
	 */
	protected abstract double[] probabilities(
		final Population<G, C> population,
		final int count
	);

	/**
	 * Check if the given probabilities sum to one.
	 *
	 * @param probabilities the probabilities to check.
	 * @return {@code true} if the sum of the probabilities are within the error
	 *          range, {@code false} otherwise.
	 */
	static boolean sum2one(final double[] probabilities) {
		final double sum = sum(probabilities);
		return abs(ulpDistance(sum, 1.0)) < MAX_ULP_DISTANCE;
	}

	/**
	 * Perform a binary-search on the summed probability array.
	 */
	final static int indexOf(final double[] incremental, final double value) {
		int imin = 0;
		int imax = incremental.length;

		while (imax > imin) {
			int imid = (imin + imax) >>> 1;

			if (imid == 0) {
				return imid;
			} else if (incremental[imid] >= value && incremental[imid - 1] < value) {
				return imid;
			} else if (incremental[imid] <= value) {
				imin = imid + 1;
			} else if (incremental[imid] > value) {
				imax = imid;
			}
		}

		return incremental.length - 1;
	}

	/**
	 * In-place summation of the probability array.
	 */
	final static double[] incremental(final double[] values) {
		for (int i = 1; i < values.length; ++i) {
			values[i] = values[i - 1] + values[i];
		}
		return values;
	}

}

