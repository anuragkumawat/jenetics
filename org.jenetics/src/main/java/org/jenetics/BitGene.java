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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import org.jenetics.internal.util.model.ModelType;
import org.jenetics.internal.util.model.ValueType;

import org.jenetics.util.RandomRegistry;

/**
 * Implementation of a BitGene.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.6 &mdash; <em>$Date$</em>
 */
@XmlJavaTypeAdapter(BitGene.Model.Adapter.class)
public enum BitGene
	implements
		Gene<Boolean, BitGene>,
		Comparable<BitGene>,
		XMLSerializable
{

	FALSE(false),
	TRUE(true);

	private static final long serialVersionUID = 2L;

	public static final BitGene ZERO = FALSE;
	public static final BitGene ONE = TRUE;

	private final boolean _value;

	private BitGene(final boolean value) {
		_value = value;
	}

	/**
	 * Return the value of the BitGene.
	 *
	 * @return The value of the BitGene.
	 */
	public final boolean getBit() {
		return _value;
	}

	/**
	 * Return the {@code boolean} value of this gene.
	 *
	 * @see #getAllele()
	 *
	 * @return the {@code boolean} value of this gene.
	 */
	public boolean booleanValue() {
		return _value;
	}

	@Override
	public Boolean getAllele() {
		return _value;
	}

	/**
	 * Return always {@code true}.
	 *
	 * @return always {@code true}.
	 */
	@Override
	public boolean isValid() {
		return true;
	}

	@Deprecated
	@Override
	public BitGene copy() {
		return this;
	}

	/**
	 * Create a new, <em>random</em> gene.
	 */
	@Override
	public BitGene newInstance() {
		return RandomRegistry.getRandom().nextBoolean() ? TRUE : FALSE;
	}

	/**
	 * Create a new gene from the given {@code value}..
	 *
	 * @since 1.6
	 * @param value the value of the new gene.
	 * @return a new gene with the given value.
	 */
	public BitGene newInstance(final Boolean value) {
		return of(value);
	}

	@Override
	public String toString() {
		return Boolean.toString(_value);
	}

	/**
	 * Return the corresponding {@code BitGene} for the given {@code boolean}
	 * value.
	 *
	 * @param value the value of the returned {@code BitGene}.
	 * @return the {@code BitGene} for the given {@code boolean} value.
	 *
	 * @deprecated Use {@link #of(boolean)} instead.
	 */
	@Deprecated
	public static BitGene valueOf(final boolean value) {
		return of(value);
	}

	/**
	 * Return the corresponding {@code BitGene} for the given {@code boolean}
	 * value.
	 *
	 * @param value the value of the returned {@code BitGene}.
	 * @return the {@code BitGene} for the given {@code boolean} value.
	 */
	public static BitGene of(final boolean value) {
		return value ? TRUE : FALSE;
	}

	/* *************************************************************************
	 *  XML object serialization
	 * ************************************************************************/

	static final XMLFormat<BitGene>
	XML = new XMLFormat<BitGene>(BitGene.class)
	{
		private static final String VALUE = "value";

		@Override
		public BitGene newInstance(
			final Class<BitGene> cls,
			final InputElement element
		)
			throws XMLStreamException
		{
			final boolean value = element.getAttribute(VALUE, true);
			return value ? BitGene.TRUE : BitGene.FALSE;
		}
		@Override
		public void write(final BitGene gene, final OutputElement element)
			throws XMLStreamException
		{
			element.setAttribute(VALUE, gene._value);
		}
		@Override
		public void read(final InputElement element, final BitGene gene) {
		}
	};

	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "org.jenetics.BitGene")
	@XmlType(name = "org.jenetics.BitGene")
	@XmlAccessorType(XmlAccessType.FIELD)
	final static class Model {

		@XmlAttribute
		public boolean value;

		@ValueType(BitGene.class)
		@ModelType(Model.class)
		public final static class Adapter
			extends XmlAdapter<Model, BitGene>
		{
			@Override
			public Model marshal(final BitGene value) {
				final Model m = new Model();
				m.value = value.booleanValue();
				return m;
			}

			@Override
			public BitGene unmarshal(final Model m) {
				return BitGene.of(m.value);
			}
		}
	}

}
