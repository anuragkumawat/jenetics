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

/**
 * Interface for accumulating values of a given type. Here an usage example:
 *
 * [code]
 * final MinMax<Double> minMax = new MinMax<>();
 * final Variance<Double> variance = new Variance<>();
 * final Quantile<Double> quantile = new Quantile<>();
 *
 * final List<Double> values = ...;
 * accumulators.accumulate(values, minMax, variance, quantile);
 * [/code]
 *
 * @see accumulators
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date: 2012-11-06 $</em>
 */
public interface Accumulator<T> {

	/**
	 * Accumulate the given value.
	 *
	 * @param value the value to accumulate.
	 */
	public void accumulate(final T value);

}