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
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics;

import static org.jenetics.util.object.eq;
import static org.jenetics.util.object.hashCodeOf;

import java.io.Serializable;

import javolution.lang.Immutable;

import org.jscience.mathematics.number.Float64;

import org.jenetics.util.Function;

/**
 * Implements an exponential fitness scaling, whereby all fitness values are
 * modified the following way.
 * <p/><img src="doc-files/exponential-scaler.gif"
 *          alt="f_s=\left(a\cdot f+b \rigth)^c"
 *     />.</p>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date$</em>
 */
public final class ExponentialScaler
	implements
		Function<Float64, Float64>,
		Serializable,
		Immutable
{
	private static final long serialVersionUID = 1L;

	public static final ExponentialScaler SQR_SCALER = new ExponentialScaler(2);
	public static final ExponentialScaler SQRT_SCALER = new ExponentialScaler(0.5);

	private final double _a;
	private final double _b;
	private final double _c;

	/**
	 * Create a new FitnessScaler.
	 *
	 * @param a <pre>fitness = (<strong>a</strong> * fitness + b) ^ c</pre>
	 * @param b <pre>fitness = (a * fitness + <strong>b</strong>) ^ c</pre>
	 * @param c <pre>fitness = (a * fitness + b) ^ <strong>c</strong></pre>
	 */
	public ExponentialScaler(final double a, final double b, final double c) {
		_a = a;
		_b = b;
		_c = c;
	}

	/**
	 * Create a new FitnessScaler.
	 *
	 * @param b <pre>fitness = (1 * fitness + <strong>b</strong>) ^ c</pre>
	 * @param c <pre>fitness = (1 * fitness + b) ^ <strong>c</strong></pre>
	 */
	public ExponentialScaler(final double b, final double c) {
		this(1.0, b, c);
	}

	/**
	 * Create a new FitnessScaler.
	 *
	 * @param c <pre>fitness = (1 * fitness + 0) ^ <strong>c</strong></pre>
	 */
	public ExponentialScaler(final double c) {
		this(0.0, c);
	}


	@Override
	public Float64 apply(final Float64 value) {
		return Float64.valueOf(Math.pow((_a*value.doubleValue() + _b), _c));
	}

	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).and(_a).and(_b).and(_c).value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		}

		final ExponentialScaler selector = (ExponentialScaler)obj;
		return eq(_a, selector._a) && eq(_b, selector._b) && eq(_c, selector._c);
	}

	@Override
	public String toString() {
		return String.format(
				"%s[a=%f, b=%f, c=%f]",
				getClass().getSimpleName(), _a, _b, _c
			);
	}
}
