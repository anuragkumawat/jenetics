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

import java.util.Comparator;

/**
 * This {@code enum} determines whether the GA should maximize or minimize the
 * fitness function.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date$</em>
 */
public enum Optimize {

	/**
	 * GA minimization
	 */
	MINIMUM {
		@Override
		public <T extends Comparable<? super T>>
		int compare(final T o1, final T o2)
		{
			return o2.compareTo(o1);
		}
	},

	/**
	 * GA maximization
	 */
	MAXIMUM {
		@Override
		public <T extends Comparable<? super T>>
		int compare(final T o1, final T o2)
		{
			return o1.compareTo(o2);
		}
	};

	/**
	 * Compares two comparable objects. Returns a negative integer, zero, or a
	 * positive integer as the first argument is better than, equal to, or worse
	 * than the second.
	 *
	 * @param <T> the comparable type
	 * @param o1 the first object to be compared.
	 * @param o2 the second object to be compared.
	 * @return a negative integer, zero, or a positive integer as the first
	 *          argument is better than, equal to, or worse than the second.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public abstract <T extends Comparable<? super T>>
	int compare(final T o1, final T o2);

	/**
	 * Create an appropriate comparator of the given optimization strategy. A
	 * collection of comparable objects with the returned comparator will be
	 * sorted in <b>descending</b> order, according to the given definition
	 * of <i>better</i> and <i>worse</i>.
	 *
	 * [code]
	 * Population<Float64Gene, Float64> population = ...
	 * population.populationSort(Optimize.MINIMUM.<Float64>descending());
	 * [/code]
	 *
	 * The code example above will populationSort the population according it's fitness
	 * values in ascending order, since lower values are <i>better</i> in this
	 * case.
	 *
	 * @param <T> the type of the objects to compare.
	 * @return a new {@link Comparator} for the type {@code T}.
	 */
	public <T extends Comparable<? super T>> Comparator<T> descending() {
		return new Comparator<T>() {
			@Override
			public int compare(final T o1, final T o2) {
				return Optimize.this.compare(o2, o1);
			}
		};
	}

	/**
	 * Create an appropriate comparator of the given optimization strategy. A
	 * collection of comparable objects with the returned comparator will be
	 * sorted in <b>ascending</b> order, according to the given definition
	 * of <i>better</i> and <i>worse</i>.
	 *
	 * [code]
	 * Population<Float64Gene, Float64> population = ...
	 * population.populationSort(Optimize.MINIMUM.<Float64>ascending());
	 * [/code]
	 *
	 * The code example above will populationSort the population according it's fitness
	 * values in descending order, since lower values are <i>better</i> in this
	 * case.
	 *
	 * @param <T> the type of the objects to compare.
	 * @return a new {@link Comparator} for the type {@code T}.
	 */
	public <T extends Comparable<? super T>> Comparator<T> ascending() {
		return new Comparator<T>() {
			@Override
			public int compare(final T o1, final T o2) {
				return Optimize.this.compare(o1, o2);
			}
		};
	}

	/**
	 * Return the best value, according to this optimization direction.
	 *
	 * @param <C> the fitness value type.
	 * @param a the first value.
	 * @param b the second value.
	 * @return the best value. If both values are equal the first one is returned.
	 */
	public <C extends Comparable<? super C>> C best(final C a, final C b) {
		C best = a;
		if (compare(b, best) > 0) {
			best = b;
		}
		return best;
	}

	/**
	 * Return the worst value, according to this optimization direction.
	 *
	 * @param <C> the fitness value type.
	 * @param a the first value.
	 * @param b the second value.
	 * @return the worst value. If both values are equal the first one is returned.
	 */
	public <C extends Comparable<? super C>> C worst(final C a, final C b) {
		C worst = a;
		if (compare(b, worst) < 0) {
			worst = b;
		}
		return worst;
	}

}
