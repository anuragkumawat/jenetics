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
package org.jenetics.stat;

import static org.jenetics.util.object.eq;
import static org.jenetics.util.object.hashCodeOf;

import org.jenetics.util.AbstractAccumulator;


/**
 * <p>Calculate  the Arithmetic mean from a finite sample of <i>N</i>
 * observations.</p>
 * <p><img src="doc-files/arithmetic-mean.gif"
 *         alt="\bar{x}=\frac{1}{N}\sum_{i=1}^{N}x_i"
 *    />
 * </p>
 *
 * @see <a href="http://mathworld.wolfram.com/ArithmeticMean.html">Wolfram MathWorld: Artithmetic Mean</a>
 * @see <a href="http://en.wikipedia.org/wiki/Arithmetic_mean">Wikipedia: Arithmetic Mean</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date$</em>
 */
public class Mean<N extends Number> extends AbstractAccumulator<N> {

	protected double _mean = Double.NaN;

	public Mean() {
	}

	/**
	 * Return the mean value of the accumulated values.
	 *
	 * @return the mean value of the accumulated values, or {@link java.lang.Double#NaN}
	 *          if {@code getSamples() == 0}.
	 */
	public double getMean() {
		return _mean;
	}

	/**
	 * Return the
	 * <a href="https://secure.wikimedia.org/wikipedia/en/wiki/Standard_error_%28statistics%29">
	 * Standard error
	 * </a> of the calculated mean.
	 *
	 * @return the standard error of the calculated mean.
	 */
	public double getStandardError() {
		double sem = Double.NaN;

		if (_samples > 0) {
			sem = _mean/Math.sqrt(_samples);
		}

		return sem;
	}

	/**
	 * @throws NullPointerException if the given {@code value} is {@code null}.
	 */
	@Override
	public void accumulate(final N value) {
		if (_samples == 0) {
			_mean = 0;
		}

		_mean += (value.doubleValue() - _mean)/(++_samples);
	}

	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).and(super.hashCode()).and(_mean).value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		final Mean<?> mean = (Mean<?>)obj;
		return eq(_mean, mean._mean) && super.equals(mean);
	}

	@Override
	public String toString() {
		return String.format(
			"%s[samples=%d, mean=%f, stderr=%f]",
			getClass().getSimpleName(),
			getSamples(),
			getMean(),
			getStandardError()
		);
	}

	@Override
	public Mean<N> clone() {
		return (Mean<N>)super.clone();
	}

}
