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

import static org.jenetics.EnumGene.Gene;
import static org.jenetics.util.factories.Int;
import static org.jenetics.util.functions.StringToInteger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import org.jenetics.internal.util.HashBuilder;
import org.jenetics.internal.util.cast;
import org.jenetics.internal.util.jaxb;
import org.jenetics.internal.util.model;

import org.jenetics.util.Array;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;
import org.jenetics.util.ISeq;
import org.jenetics.util.bit;


/**
 * The mutable methods of the {@link AbstractChromosome} has been overridden so
 * that no invalid permutation will be created.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.6 &mdash; <em>$Date$</em>
 */
@XmlJavaTypeAdapter(PermutationChromosome.Model.Adapter.class)
public final class PermutationChromosome<T>
	extends AbstractChromosome<EnumGene<T>>
	implements XMLSerializable
{
	private static final long serialVersionUID = 1L;

	private ISeq<T> _validAlleles;

	/*
	 * TODO: Refactor this to the default constructor in version 2.0. Currently 
	 * not possible, because this would be an incompatible change.
	 */
	PermutationChromosome(
		final ISeq<EnumGene<T>> genes,
		final boolean internal
	) {
		super(genes);
		_validAlleles = genes.get(0).getValidAlleles();
		_valid = true;
	}

	/**
	 * Create a new, random chromosome with the given valid alleles.
	 *
	 * @param validAlleles the valid alleles used for this permutation arrays.
	 */
	public PermutationChromosome(final ISeq<? extends T> validAlleles) {
		this(
			new Array<EnumGene<T>>(
				validAlleles.length()
			).fill(Gene(validAlleles)).shuffle().toISeq(),
			true
		);
		_validAlleles = cast.apply(validAlleles);
	}

	public ISeq<T> getValidAlleles() {
		return _validAlleles;
	}

	/**
	 * Check if this chromosome represents still a valid permutation.
	 */
	@Override
	public boolean isValid() {
		if (_valid == null) {
			byte[] check = new byte[length()/8 + 1];
			Arrays.fill(check, (byte)0);

			boolean valid = super.isValid();
			for (int i = 0; i < length() && valid; ++i) {
				final int value = _genes.get(i).getAlleleIndex();
				if (value >= 0 && value < length()) {
					if (bit.get(check, value)) {
						valid = false;
					} else {
						bit.set(check, value, true);
					}
				} else {
					valid = false;
				}
			}

			_valid = valid;
		}

		return _valid;
	}

	/**
	 * Return a more specific view of this chromosome factory.
	 *
	 * @return a more specific view of this chromosome factory.
	 *
	 * @deprecated No longer needed after adding new factory methods to the
	 *             {@link Array} class.
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public Factory<PermutationChromosome<T>> asFactory() {
		return (Factory<PermutationChromosome<T>>)(Object)this;
	}

	/**
	 * Create a new, <em>random</em> chromosome.
	 */
	@Override
	public PermutationChromosome<T> newInstance() {
		return new PermutationChromosome<>(_validAlleles);
	}

	@Override
	public PermutationChromosome<T> newInstance(final ISeq<EnumGene<T>> genes) {
		return new PermutationChromosome<>(genes, true);
	}

	@Override
	public int hashCode() {
		return HashBuilder.of(getClass())
				.and(super.hashCode())
				.value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		final StringBuilder out = new StringBuilder();
		out.append(_genes.get(0).getAllele());
		for (int i = 1; i < length(); ++i) {
			out.append("|").append(_genes.get(i).getAllele());
		}
		return out.toString();
	}

	/**
	 * Create a new PermutationChromosome from the given genes.
	 *
	 * @param genes the genes of this chromosome.
	 * @return a new PermutationChromosome from the given genes.
	 *
	 * @deprecated Use {@link #of(org.jenetics.util.ISeq)} instead.
	 */
	@Deprecated
	public static <T> PermutationChromosome<T> valueOf(
		final ISeq<EnumGene<T>> genes
	) {
		return new PermutationChromosome<>(genes, true);
	}

	/**
	 * Create a new PermutationChromosome from the given genes.
	 *
	 * @param genes the genes of this chromosome.
	 * @return a new PermutationChromosome from the given genes.
	 */
	public static <T> PermutationChromosome<T> of(final ISeq<EnumGene<T>> genes) {
		return new PermutationChromosome<>(genes, true);
	}

	/**
	 * Create a integer permutation chromosome with the given length.
	 *
	 * @param length the chromosome length.
	 * @return a integer permutation chromosome with the given length.
	 */
	@SuppressWarnings("deprecation")
	public static PermutationChromosome<Integer> ofInteger(final int length) {
		final ISeq<Integer> alleles = new Array<Integer>(length).fill(Int()).toISeq();
		return new PermutationChromosome<>(alleles);
	}

	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private void writeObject(final ObjectOutputStream out)
		throws IOException
	{
		out.defaultWriteObject();

		out.writeObject(_validAlleles);
		for (EnumGene<?> gene : _genes) {
			out.writeInt(gene.getAlleleIndex());
		}
	}

	@SuppressWarnings("unchecked")
	private void readObject(final ObjectInputStream in)
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		_validAlleles = (ISeq<T>)in.readObject();

		final Array<EnumGene<T>> genes = new Array<>(_validAlleles.length());
		for (int i = 0; i < _validAlleles.length(); ++i) {
			genes.set(i, new EnumGene<>(in.readInt(), _validAlleles));
		}

		_genes = genes.toISeq();
	}

	/* *************************************************************************
	 *  XML object serialization
	 * ************************************************************************/

	@SuppressWarnings("rawtypes")
	static final XMLFormat<PermutationChromosome>
		XML = new XMLFormat<PermutationChromosome>(PermutationChromosome.class) {

		private static final String LENGTH = "length";
		private static final String ALLELE_INDEXES = "allele-indexes";

		@SuppressWarnings("unchecked")
		@Override
		public PermutationChromosome newInstance(
			final Class<PermutationChromosome> cls,
			final InputElement xml
		)
			throws XMLStreamException
		{
			final int length = xml.getAttribute(LENGTH, 0);
			final Array<Object> alleles = new Array<>(length);
			for (int i = 0; i < length; ++i) {
				alleles.set(i, xml.getNext());
			}

			final ISeq<Object> ialleles = alleles.toISeq();

			final Array<Integer> indexes = Array.of(
				xml.get(ALLELE_INDEXES, String.class
				).split(",")).map(StringToInteger);

			final Array<Object> genes = new Array<>(length);
			for (int i = 0; i < length; ++i) {
				genes.set(i, new EnumGene<>(indexes.get(i), ialleles));
			}

			return new PermutationChromosome(genes.toISeq(), true);
		}

		@Override
		public void write(
			final PermutationChromosome chromosome,
			final OutputElement xml
		)
			throws XMLStreamException
		{
			xml.setAttribute(LENGTH, chromosome.length());
			for (Object allele : chromosome.getValidAlleles()) {
				xml.add(allele);
			}

			final PermutationChromosome<?> pc = chromosome;
			final String indexes = pc.toSeq().map(new Function<Object, Integer>() {
				@Override public Integer apply(final Object value) {
					return ((EnumGene<?>)value).getAlleleIndex();
				}
			}).toString(",");
			xml.add(indexes, ALLELE_INDEXES);
		}
		@Override
		public void read(
			final InputElement element,
			final PermutationChromosome chromosome
		) {
		}
	};

	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/


	@XmlRootElement(name = "org.jenetics.PermutationChromosome")
	@XmlType(name = "org.jenetics.PermutationChromosome")
	@XmlAccessorType(XmlAccessType.FIELD)
	@SuppressWarnings({"unchecked", "rawtypes"})
	static final class Model {

		@XmlAttribute
		public int length;

		@XmlAnyElement
		public List<Object> genes;

		@XmlJavaTypeAdapter(jaxb.JavolutionElementAdapter.class)
		@XmlElement(name = "allele-indexes")
		public Object indexes;

		@model.ValueType(PermutationChromosome.class)
		@model.ModelType(Model.class)
		public static final class Adapter
			extends XmlAdapter<Model, PermutationChromosome>
		{
			@Override
			public Model marshal(final PermutationChromosome pc)
				throws Exception
			{
				final Model model = new Model();
				model.length = pc.length();
				model.genes = pc.getValidAlleles()
					.map(jaxb.Marshaller(pc.getValidAlleles().get(0))).asList();
				model.indexes = jaxb.marshal(pc.toSeq().map(new Function<Object, Integer>() {
					@Override public Integer apply(final Object value) {
						return ((EnumGene<?>)value).getAlleleIndex();
					}
				}).toString(","));
				return model;
			}

			@Override
			public PermutationChromosome unmarshal(final Model model)
				throws Exception
			{
				final ISeq seq = Array.of(model.genes)
					.map(jaxb.Unmarshaller).toISeq();
				final Array<Integer> indexes = Array
					.of(model.indexes.toString().split(","))
					.map(StringToInteger);

				final Array<Object> genes = new Array<>(seq.length());
				for (int i = 0; i < seq.length(); ++i) {
					genes.set(i, new EnumGene(indexes.get(i), seq));
				}

				return new PermutationChromosome(genes.toISeq(), true);
			}
		}

		public static final Adapter Adapter = new Adapter();
	}

}
