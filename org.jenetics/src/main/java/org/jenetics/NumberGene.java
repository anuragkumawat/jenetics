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
package org.jenetics;

import static java.util.Objects.requireNonNull;
import static org.jenetics.internal.util.object.eq;

import javolution.text.Text;
import javolution.text.TextBuilder;
import javolution.xml.XMLSerializable;

import org.jscience.mathematics.number.Number;

import org.jenetics.internal.util.HashBuilder;

import org.jenetics.util.Mean;

/**
 * Abstract base class for implementing concrete NumberGenes.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.2 &mdash; <em>$Date$</em>
 *
 * @deprecated Use {@link AbstractNumericGene} instead. This classes
 *             uses the <i>JScience</i> library, which will be removed in the
 *             next major version.
 */
@Deprecated
public abstract class NumberGene<
	N extends Number<N>,
	G extends NumberGene<N, G>
>
	extends Number<G>
	implements
		NumericGene<N, G>,
		Mean<G>,
		XMLSerializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * The minimum value of this {@code NumberGene}. This field is marked
	 * as transient and must serialized manually by sub classes.
	 */
	protected transient N _min;

	/**
	 * The maximum value of this {@code NumberGene}. This field is marked
	 * as transient and must serialized manually by sub classes.
	 */
	protected transient N _max;

	/**
	 * The value of this {@code NumberGene}. This field is marked
	 * as transient and must serialized manually by sub classes.
	 */
	protected transient N _value;

	private transient boolean _valid = true;

	protected NumberGene() {
	}

	/**
	 * Boxes a given Java number into the required number object.
	 *
	 * @param value the Java number to box.
	 * @return the boxed number.
	 */
	protected abstract N box(final java.lang.Number value);

	/**
	 * Create a new gene from the given {@code value}.
	 *
	 * @param value the value of the new gene.
	 * @return a new gene with the given value.
	 */
	public abstract G newInstance(final N value);

	@Override
	public G copy() {
		return newInstance(_value);
	}

	/**
	 * Create a new NumberGene with the same limits and the given value.
	 *
	 * @param value The value of the new NumberGene.
	 * @return The new NumberGene.
	 * @throws NullPointerException if the given {@code value} is {@code null}.
	 */
	public G newInstance(final java.lang.Number value) {
		return newInstance(box(value));
	}

	/**
	 * Set the {@code NumberGene}.
	 *
	 * @param value The value of the number gene.
	 * @param min The allowed min value of the gene.
	 * @param max The allows max value of the gene.
	 * @throws NullPointerException if one of the given number is null.
	 */
	protected void set(final N value, final N min, final N max) {
		_min = requireNonNull(min, "Min value");
		_max = requireNonNull(max, "Max value");
		_value = requireNonNull(value, "Gene value");
		_valid = _value.compareTo(_min) >= 0 && _value.compareTo(_max) <= 0;
	}

	/**
	 * Test whether this is a valid NumberGene and its value is within the
	 * interval closed interval [min, max].
	 *
	 * @return if this gene is valid, which means the gene value is within the
	 *          closed interval [min, max].
	 */
	@Override
	public boolean isValid() {
		return _valid;
	}

	/**
	 * Return the number value of this gene.
	 *
	 * @return the number value of this gene.
	 */
	public N getNumber() {
		return _value;
	}

	@Override
	public N getAllele() {
		return _value;
	}

	/**
	 * Return the allowed min value.
	 *
	 * @return The allowed min value.
	 */
	public N getMin() {
		return _min;
	}

	/**
	 * Return the allowed max value.
	 *
	 * @return The allowed max value.
	 */
	public N getMax() {
		return _max;
	}

	 @Override
	public double doubleValue() {
		return _value.doubleValue();
	 }

	 @Override
	public long longValue() {
		return _value.longValue();
	 }

	@Override
	public boolean isLargerThan(final G that) {
		return _value.isLargerThan(that._value);
	}

	@Override
	public G plus(final G that) {
		return newInstance(_value.plus(that._value));
	}

	@Override
	public G opposite() {
		return newInstance(_value.opposite());
	}

	@Override
	public G times(final G that) {
		return newInstance(_value.times(that._value));
	}

	/**
	 * Remind that this method is not consistent with the {@link #equals(Object)}
	 * method. Since this method only compares the {@code value} and the
	 * {@code equals} method also takes the {@code min} and {@code max} value
	 * into account.
	 * [code]
	 * final NumberGene〈?, ?〉 ng1 = ...
	 * final NumberGene〈?, ?〉 ng2 = ...
	 *
	 * if (ng1.equals(ng2) {
	 *     // Holds for every ng1 and ng2.
	 *     assert(ng1.compareTo(ng2) == 0);
	 * }
	 * if (ng1.compareTo(ng2) == 0) {
	 *     // Doesn't hold for every ng1 and ng2.
	 *     assert(ng1.equals(ng2));
	 * }
	 * [/code]
	 */
	@Override
	public int compareTo(final G that) {
		return _value.compareTo(that._value);
	}

	@Override
	public int hashCode() {
		return HashBuilder.of(getClass()).and(_value).and(_min).and(_max).value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		}

		final NumberGene<?, ?> gene = (NumberGene<?, ?>)obj;
		return eq(_value, gene._value) &&
				eq(_min, gene._min) &&
				eq(_max, gene._max);
	}

	@Override
	public Text toText() {
		TextBuilder out = new TextBuilder();
		out.append("[").append(_value).append("]");
		return out.toText();
	}

}
