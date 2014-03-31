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

import static org.jenetics.internal.util.object.Verify;
import static org.jenetics.internal.util.object.eq;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jenetics.internal.util.HashBuilder;
import org.jenetics.internal.util.cast;
import org.jenetics.internal.util.jaxb;

import org.jenetics.util.Array;
import org.jenetics.util.Factory;
import org.jenetics.util.Function;
import org.jenetics.util.ISeq;
import org.jenetics.util.Seq;
import org.jenetics.util.Verifiable;

/**
 * The central class the GA is working with, is the {@code Genotype}. It is the
 * structural representative of an individual. This class is the encoded problem
 * solution with one to many {@link Chromosome}.
 * <p>
 * <img alt="Genotype" src="doc-files/Genotype.svg" width="400" height="252" >
 * </p>
 * The chromosomes of a genotype doesn't have to have necessarily the same size.
 * It is only required that all genes are from the same type and the genes within
 * a chromosome have the same constraints; e. g. the same min- and max values
 * for number genes.
 *
 * [code]
 * final Genotype&lt;DoubleGene&gt; genotype = Genotype.of(
 *     DoubleChromosome.of(0.0, 1.0, 8),
 *     DoubleChromosome.of(1.0, 2.0, 10),
 *     DoubleChromosome.of(0.0, 10.0, 9),
 *     DoubleChromosome.of(0.1, 0.9, 5)
 * );
 * [/code]
 * The code snippet above creates a genotype with the same structure as shown in
 * the figure above. In this example the {@link DoubleGene} has been chosen as
 * gene type.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0 &mdash; <em>$Date$</em>
 */
@XmlJavaTypeAdapter(Genotype.Model.Adapter.class)
public final class Genotype<G extends Gene<?, G>>
	implements
		Factory<Genotype<G>>,
		Iterable<Chromosome<G>>,
		Verifiable,
		Serializable
{
	private static final long serialVersionUID = 3L;

	private final ISeq<Chromosome<G>> _chromosomes;
	private final int _ngenes;

	//Caching isValid value.
	private volatile Boolean _valid = null;

	private Genotype(
		final ISeq<? extends Chromosome<G>> chromosomes,
		final int ngenes
	) {
		if (chromosomes.length() == 0) {
			throw new IllegalArgumentException("No chromosomes given.");
		}

		_chromosomes = cast.apply(chromosomes);
		_ngenes = ngenes;
	}

	/**
	 * Create a new Genotype from a given sequence of {@code Chromosomes}.
	 *
	 * @param chromosomes The {@code Chromosome} array the {@code Genotype}
	 *         consists of.
	 * @throws NullPointerException if {@code chromosomes} is null or one of its
	 *         element.
	 * @throws IllegalArgumentException if {@code chromosome.length == 0}.
	 */
	public Genotype(final ISeq<? extends Chromosome<G>> chromosomes) {
		this(chromosomes, ngenes(chromosomes));
	}

	private static int ngenes(final Seq<? extends Chromosome<?>> chromosomes) {
		int ngenes = 0;
		for (int i = chromosomes.length(); --i >= 0;) {
			ngenes += chromosomes.get(i).length();
		}
		return ngenes;
	}

	/**
	 * Return the chromosome at the given index. It is guaranteed, that the
	 * returned chromosome is not null.
	 *
	 * @param index Chromosome index.
	 * @return The Chromosome.
	 * @throws IndexOutOfBoundsException if
	 *         {@code (index < 0 || index >= _length)}.
	 */
	public Chromosome<G> getChromosome(final int index) {
		assert(_chromosomes != null);
		assert(_chromosomes.get(index) != null);

		return _chromosomes.get(index);
	}

	/**
	 * Return the first chromosome. This is a shortcut for
	 * [code]
	 * final Genotype&lt;DoubleGene&gt; gt = ...
	 * final Chromosome&lt;DoubleGene&gt; chromosome = gt.getChromosome(0);
	 * [/code]
	 *
	 * @return The first chromosome.
	 */
	public Chromosome<G> getChromosome() {
		assert(_chromosomes != null);
		assert(_chromosomes.get(0) != null);

		return _chromosomes.get(0);
	}

	/**
	 * Return the first {@link Gene} of the first {@link Chromosome} of this
	 * {@code Genotype}. This is a shortcut for
	 * [code]
	 * final Genotype&lt;DoubleGene&gt; gt = ...
	 * final DoubleGene gene = gt.getChromosome(0).getGene(0);
	 * [/code]
	 *
	 * @return the first {@link Gene} of the first {@link Chromosome} of this
	 *         {@code Genotype}.
	 */
	public G getGene() {
		assert(_chromosomes != null);
		assert(_chromosomes.get(0) != null);

		return _chromosomes.get(0).getGene();
	}


	public ISeq<Chromosome<G>> toSeq() {
		return _chromosomes;
	}

	@Override
	public Iterator<Chromosome<G>> iterator() {
		return _chromosomes.iterator();
	}

	/**
	 * Getting the number of chromosomes of this genotype.
	 *
	 * @return number of chromosomes.
	 */
	public int length() {
		return _chromosomes.length();
	}

	/**
	 * Return the number of genes this genotype consists of. This is the sum of
	 * the number of genes of the genotype chromosomes.
	 *
	 * @return Return the number of genes this genotype consists of.
	 */
	public int getNumberOfGenes() {
		return _ngenes;
	}

	/**
	 * Test if this genotype is valid. A genotype is valid if all its
	 * {@link Chromosome}s are valid.
	 *
	 * @return true if this genotype is valid, false otherwise.
	 */
	@Override
	public boolean isValid() {
		if (_valid == null) {
			_valid = _chromosomes.forAll(Verify);
		}
		return _valid;
	}

	/**
	 * Return a new, random genotype by creating new, random chromosomes (calling
	 * the {@link Chromosome#newInstance()} method) from the chromosomes of this
	 * genotype.
	 */
	@Override
	public Genotype<G> newInstance() {
		final Array<Chromosome<G>> chromosomes = new Array<>(length());
		for (int i = 0; i < length(); ++i) {
			chromosomes.set(i, _chromosomes.get(i).newInstance());
		}

		return new Genotype<>(chromosomes.toISeq(), _ngenes);
	}

	Genotype<G> newInstance(final ISeq<Chromosome<G>> chromosomes) {
		return new Genotype<>(chromosomes, _ngenes);
	}

	@Override
	public int hashCode() {
		return HashBuilder.of(getClass()).and(_chromosomes).value();
	}

	@Override
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof Genotype<?>)) {
			return false;
		}

		final Genotype<?> gt = (Genotype<?>)o;
		return eq(_chromosomes, gt._chromosomes);
	}

	@Override
	public String toString() {
		return _chromosomes.toString();
	}


	/* *************************************************************************
	 *  Property access methods
	 * ************************************************************************/

	/**
	 * Return a converter which access the chromosome array of this genotype.
	 *
	 * @param <T> the gene type
	 * @return a function object which returns the chromosomes for this genotype.
	 */
	public static <T extends Gene<?, T>>
	Function<Genotype<T>, ISeq<Chromosome<T>>> Chromosomes()
	{
		return new Function<Genotype<T>, ISeq<Chromosome<T>>>() {
			@Override public ISeq<Chromosome<T>> apply(final Genotype<T> value) {
				return value.toSeq();
			}
		};
	}

	/**
	 * Return a converter which access the chromosome with the given index of
	 * this genotype.
	 *
	 * @param <T> the gene type
	 * @param index the index of the chromosome
	 * @return a function object which returns the chromosome at the given index.
	 */
	public static <T extends Gene<?, T>>
	Function<Genotype<T>, Chromosome<T>> Chromosome(final int index)
	{
		return new Function<Genotype<T>, Chromosome<T>>() {
			@Override public Chromosome<T> apply(final Genotype<T> value) {
				return value.getChromosome(index);
			}
		};
	}

	/**
	 * Return a converter which access the first chromosome of this genotype.
	 *
	 * @param <T> the gene type
	 * @return a function object which returns the first chromosome of this
	 *         genotype.
	 */
	public static <T extends Gene<?, T>>
	Function<Genotype<T>, Chromosome<T>> Chromosome()
	{
		return new Function<Genotype<T>, Chromosome<T>>() {
			@Override public Chromosome<T> apply(final Genotype<T> value) {
				return value.getChromosome();
			}
		};
	}

	/**
	 * Create a new Genotype from a given array of {@code Chromosomes}.
	 *
	 * @param <G> the gene type
	 * @param chromosomes The {@code Chromosome} array the {@code Genotype}
	 *         consists of.
	 * @return a new {@code Genotype} from the given chromosomes
	 * @throws NullPointerException if {@code chromosomes} is null or one of its
	 *         element.
	 * @throws IllegalArgumentException if {@code chromosome.length == 0}.
	 */
	@SafeVarargs
	public static <G extends Gene<?, G>> Genotype<G> of(
		final Chromosome<G>... chromosomes
	) {
		return new Genotype<>(Array.of(chromosomes).toISeq());
	}

	/* *************************************************************************
	 *  JAXB object serialization
	 * ************************************************************************/

	@XmlRootElement(name = "genotype")
	@XmlType(name = "org.jenetics.Genotype")
	@XmlAccessorType(XmlAccessType.FIELD)
	@SuppressWarnings({"unchecked", "rawtypes"})
	static final class Model {

		@XmlAttribute(name = "length", required = true)
		public int length;

		@XmlAttribute(name = "ngenes", required = true)
		public int ngenes;

		@XmlElement(name = "chromosome", required = true, nillable = false)
		public List chromosomes;

		public static final class Adapter
			extends XmlAdapter<Model, Genotype>
		{
			@Override
			public Model marshal(final Genotype gt) throws Exception {
				final Model model = new Model();
				model.length = gt.length();
				model.ngenes = gt.getNumberOfGenes();
				model.chromosomes = gt.toSeq()
					.map(jaxb.Marshaller(gt.getChromosome()))
					.asList();

				return model;
			}

			@Override
			public Genotype unmarshal(final Model model) throws Exception {
				final ISeq chs = Array.of(model.chromosomes)
					.map(jaxb.Unmarshaller(model.chromosomes.get(0)))
					.toISeq();

				return new Genotype(chs, model.ngenes);
			}
		}

		public static final Adapter ADAPTER = new Adapter();
	}
}
