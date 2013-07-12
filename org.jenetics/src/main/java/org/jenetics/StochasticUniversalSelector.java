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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.jenetics.util.object.hashCodeOf;

import org.jenetics.util.RandomRegistry;


/**
 * <code>StochasticUniversalSelector</code> is a method for selecting a
 * population according to some given probability in a way that minimize chance
 * fluctuations. It can be viewed as a type of roulette game where now we have
 * P equally spaced points which we spin.
 *
 * <p><div align="center">
 * <img src="doc-files/StochasticUniversalSelection.svg" width="400" />
 * </p></div>
 *
 * The figure above shows how the stochastic-universal selection works; <i>n</i>
 * is the number of individuals to select.
 *
 * @see <a href="https://secure.wikimedia.org/wikipedia/en/wiki/Stochastic_universal_sampling">
 *           Wikipedia: Stochastic universal sampling
 *      </a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date$</em>
 */
public class StochasticUniversalSelector<
	G extends Gene<?, G>,
	N extends Number & Comparable<? super N>
>
	extends RouletteWheelSelector<G, N>
{

	public StochasticUniversalSelector() {
	}

	/**
	 * This method sorts the population in descending order while calculating the
	 * selection probabilities. (The method {@link Population#populationSort()} is called
	 * by this method.)
	 */
	@Override
	public Population<G, N> select(
		final Population<G, N> population,
		final int count,
		final Optimize opt
	) {
		requireNonNull(population, "Population");
		if (count < 0) {
			throw new IllegalArgumentException(
				"Selection count must be greater or equal then zero, but was " +
				count
			);
		}

		final Population<G, N> selection = new Population<>(count);
		if (count == 0) {
			return selection;
		}

		final double[] probabilities = probabilities(population, count, opt);
		assert (population.size() == probabilities.length);

		//Calculating the equally spaces random points.
		final double delta = 1.0/count;
		final double[] points = new double[count];
		points[0] = RandomRegistry.getRandom().nextDouble()*delta;
		for (int i = 1; i < count; ++i) {
			points[i] = delta*i;
		}

		int j = 0;
		double prop = 0;
		for (int i = 0; i < count; ++i) {
			while (points[i] > prop) {
				prop += probabilities[j];
				++j;
			}
			selection.add(population.get(j));
		}

		return selection;
	}

	@Override
	protected double[] probabilities(
		final Population<G, N> population,
		final int count
	) {
		population.populationSort();
		return super.probabilities(population, count);
	}

	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).and(super.hashCode()).value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		}

		return super.equals(obj);
	}

	@Override
	public String toString() {
		return format("%s", getClass().getSimpleName());
	}

}






