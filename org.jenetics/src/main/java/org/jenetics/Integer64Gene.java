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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Random;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import javolution.context.ObjectFactory;
import javolution.xml.XMLFormat;
import javolution.xml.stream.XMLStreamException;

import org.jscience.mathematics.number.Integer64;

import org.jenetics.internal.util.model.LongModel;
import org.jenetics.internal.util.model.ModelType;
import org.jenetics.internal.util.model.ValueType;

import org.jenetics.util.Function;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.math;

/**
 * NumberGene implementation which holds a 64 bit integer number.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.6 &mdash; <em>$Date$</em>
 *
 * @deprecated Use {@link org.jenetics.LongGene} instead. This classes
 *             uses the <i>JScience</i> library, which will be removed in the
 *             next major version.
 */
@Deprecated
@XmlJavaTypeAdapter(Integer64Gene.Model.Adapter.class)
public final class Integer64Gene
	extends NumberGene<Integer64, Integer64Gene>
{
	private static final long serialVersionUID = 1L;

	Integer64Gene() {
	}

	@Override
	protected Integer64 box(final java.lang.Number value) {
		return Integer64.valueOf(value.longValue());
	}

	public Integer64Gene divide(final Integer64Gene gene) {
		return newInstance(_value.divide(gene._value));
	}

	@Override
	public Integer64Gene mean(final Integer64Gene that) {
		return newInstance(
			_value.longValue()  +
			(that._value.longValue() - _value.longValue())/2L
		);
	}

	/* *************************************************************************
	 *  Property access methods.
	 * ************************************************************************/

	/**
	 * Converter for accessing the value from a given number gene.
	 */
	public static final Function<Integer64Gene, Integer64> Allele =
		new Function<Integer64Gene, Integer64>() {
			@Override public Integer64 apply(final Integer64Gene value) {
				return value._value;
			}
		};

	/**
	 * Converter for accessing the allele from a given number gene.
	 */
	public static final Function<Integer64Gene, Integer64> Value = Allele;

	/**
	 * Converter for accessing the allowed minimum from a given number gene.
	 */
	public static final Function<Integer64Gene, Integer64> Min =
		new Function<Integer64Gene, Integer64>() {
			@Override public Integer64 apply(final Integer64Gene value) {
				return value._min;
			}
		};

	/**
	 * Converter for accessing the allowed minimum from a given number gene.
	 */
	public static final Function<Integer64Gene, Integer64> Max =
		new Function<Integer64Gene, Integer64>() {
			@Override public Integer64 apply(final Integer64Gene value) {
				return value._value;
			}
		};

	static Function<Integer64, Integer64Gene> Gene(
		final Integer64 min,
		final Integer64 max
	) {
		return new Function<Integer64, Integer64Gene>() {
			@Override
			public Integer64Gene apply(final Integer64 value) {
				return Integer64Gene.valueOf(value, min, max);
			}
		};
	}

	/* *************************************************************************
	 *  Factory methods
	 * ************************************************************************/

	/**
	 * Create a new valid, <em>random</em> gene.
	 */
	@Override
	public Integer64Gene newInstance() {
		return valueOf(_min, _max);
	}

	/**
	 * Create a new {@code Integer64Gene} with the same limits and the given
	 * value.
	 *
	 * @param value the value of the new {@code NumberGene}.
	 * @return the new {@code NumberGene}.
	 */
	public Integer64Gene newInstance(final long value) {
		return valueOf(Integer64.valueOf(value), _min, _max);
	}

	@Override
	public Integer64Gene newInstance(final Integer64 value) {
		return valueOf(value, _min, _max);
	}

	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	private static final ObjectFactory<Integer64Gene> FACTORY =
		new ObjectFactory<Integer64Gene>() {
			@Override protected Integer64Gene create() {
				return new Integer64Gene();
			}
		};

	/**
	 * Create a new random {@code Integer64Gene} with the given value and the
	 * given range. If the {@code value} isn't within the closed interval
	 * [min, max], no exception is thrown. In this case the method
	 * {@link Integer64Gene#isValid()} returns {@code false}.
	 *
	 * @param value the value of the gene.
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (inclusively).
	 * @return the new created gene with the given {@code value}.
	 */
	public static Integer64Gene valueOf(
		final long value,
		final long min,
		final long max
	) {
		return valueOf(
			Integer64.valueOf(value),
			Integer64.valueOf(min),
			Integer64.valueOf(max)
		);
	}

	/**
	 * Create a new random {@code Integer64Gene} with the given value and the
	 * given range. If the {@code value} isn't within the closed interval
	 * [min, max], no exception is thrown. In this case the method
	 * {@link Integer64Gene#isValid()} returns {@code false}.
	 *
	 * @param value the value of the gene.
	 * @param min the minimal valid value of this gene (inclusively).
	 * @param max the maximal valid value of this gene (inclusively).
	 * @return the new created gene with the given {@code value}.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public static Integer64Gene valueOf(
		final Integer64 value,
		final Integer64 min,
		final Integer64 max
	) {
		final Integer64Gene gene = FACTORY.object();
		gene.set(value, min, max);
		return gene;
	}

	/**
	 * Create a new random {@code Integer64Gene}. It is guaranteed that the
	 * value of the {@code Integer64Gene} lies in the closed interval [min, max].
	 *
	 * @param min the minimal value of the {@code Integer64Gene} to create
	 *        (inclusively).
	 * @param max the maximal value of the {@code Integer64Gene} to create
	 *        (inclusively).
	 * @return the new created gene.
	 */
	public static Integer64Gene valueOf(final long min, final long max) {
		return valueOf(Integer64.valueOf(min), Integer64.valueOf(max));
	}

	/**
	 * Create a new random {@code Integer64Gene}. It is guaranteed that the
	 * value of the {@code Integer64Gene} lies in the closed interval [min, max].
	 *
	 * @param min the minimal value of the {@code Integer64Gene} to create
	 *        (inclusively).
	 * @param max the maximal value of the {@code Integer64Gene} to create
	 *        (inclusively).
	 * @return the new created gene.
	 * @throws NullPointerException if one of the arguments is {@code null}.
	 */
	public static Integer64Gene valueOf(
		final Integer64 min,
		final Integer64 max
	) {
		final Random random = RandomRegistry.getRandom();
		final Integer64 value = Integer64.valueOf(
			math.random.nextLong(random, min.longValue(), max.longValue())
		);

		return valueOf(value, min, max);
	}

	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private void writeObject(final ObjectOutputStream out)
		throws IOException
	{
		out.defaultWriteObject();

		out.writeLong(_value.longValue());
		out.writeLong(_min.longValue());
		out.writeLong(_max.longValue());
	}

	private void readObject(final ObjectInputStream in)
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		set(
			Integer64.valueOf(in.readLong()),
			Integer64.valueOf(in.readLong()),
			Integer64.valueOf(in.readLong())
		);
	}

	/* *************************************************************************
	 *  XML object serialization
	 * ************************************************************************/

	static final XMLFormat<Integer64Gene>
		XML = new XMLFormat<Integer64Gene>(Integer64Gene.class)
	{
		private static final String MIN = "min";
		private static final String MAX = "max";

		@Override
		public Integer64Gene newInstance(
			final Class<Integer64Gene> cls, final InputElement element
		)
			throws XMLStreamException
		{
			final long min = element.getAttribute(MIN, 0L);
			final long max = element.getAttribute(MAX, 100L);
			final long value = element.<Long>getNext();
			return Integer64Gene.valueOf(value, min, max);
		}
		@Override
		public void write(final Integer64Gene gene, final OutputElement element)
			throws XMLStreamException
		{
			element.setAttribute(MIN, gene.getMin().longValue());
			element.setAttribute(MAX, gene.getMax().longValue());
			element.add(gene.getAllele().longValue());
		}
		@Override
		public void read(final InputElement e, final Integer64Gene g) {
		}
	};

	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "org.jenetics.Integer64Gene")
	@XmlType(name = "org.jenetics.Integer64Gene")
	@XmlAccessorType(XmlAccessType.FIELD)
	final static class Model {

		@XmlAttribute
		public long min;

		@XmlAttribute
		public long max;

		@XmlJavaTypeAdapter(LongModel.Adapter.class)
		@XmlElement(name= "java.lang.Long")
		public Long value;

		@ValueType(Integer64Gene.class)
		@ModelType(Model.class)
		public final static class Adapter
			extends XmlAdapter<Model, Integer64Gene>
		{
			@Override
			public Model marshal(final Integer64Gene value) {
				final Model m = new Model();
				m.min = value.getMin().longValue();
				m.max = value.getMax().longValue();
				m.value = value.longValue();
				return m;
			}

			@Override
			public Integer64Gene unmarshal(final Model m) {
				return Integer64Gene.valueOf(
					m.value,
					m.min,
					m.max
				);
			}
		}
	}

}
