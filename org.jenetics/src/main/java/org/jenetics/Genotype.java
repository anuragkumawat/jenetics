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
import static org.jenetics.util.object.eq;
import static org.jenetics.util.object.hashCodeOf;

import java.util.Iterator;

import javolution.lang.Immutable;
import javolution.lang.Realtime;
import javolution.text.Text;
import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import org.jenetics.util.Factory;
import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;
import org.jenetics.util.Seq;
import org.jenetics.util.Verifiable;

/**
 * The central class the GA is working with, is the {@code Genotype}. It is the
 * structural representative of an individual. This class is the encoded problem
 * solution with one to many {@link Chromosome}.
 * <p><div align="center">
 * <img src="doc-files/Genotype.svg" width="400" height="252" >
 * </p></div>
 * The chromosomes of a genotype doesn't have to have necessarily the same size.
 * It is only required that all genes are from the same type and the genes within
 * a chromosome have the same constraints; e. g. the same min- and max values
 * for number genes.
 *
 * [code]
 * final Genotype〈Float64Gene〉 genotype = Genotype.valueOf(
 *     new Float64Chromosome(0.0, 1.0, 8),
 *     new Float64Chromosome(1.0, 2.0, 10),
 *     new Float64Chromosome(0.0, 10.0, 9),
 *     new Float64Chromosome(0.1, 0.9, 5)
 * );
 * [/code]
 * The code snippet above creates a genotype with the same structure as shown in
 * the figure above. In this example the {@link Float64Gene} has been chosen as
 * gene type.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date$</em>
 */
public final class Genotype<G extends Gene<?, G>>
	implements
		Factory<Genotype<G>>,
		Iterable<Chromosome<G>>,
		Verifiable,
		XMLSerializable,
		Realtime,
		Immutable
{
	private static final long serialVersionUID = 2L;

	private final ISeq<Chromosome<G>> _chromosomes;
	private final int _ngenes;

	//Caching isValid value.
	private volatile Boolean _valid = null;

	private Genotype(final ISeq<Chromosome<G>> chromosomes, final int ngenes) {
		_chromosomes = chromosomes;
		_ngenes = ngenes;
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
	 * @throws IndexOutOfBoundsException if (index < 0 || index >= _length).
	 */
	public Chromosome<G> getChromosome(final int index) {
		assert(_chromosomes != null);
		assert(_chromosomes.get(index) != null);

		return _chromosomes.get(index);
	}

	/**
	 * Return the first chromosome. This is a shortcut for
	 * [code]
	 * final Genotype〈Float64Gene〉 gt = ...
	 * final Chromosome〈Float64Gene〉 chromosome = gt.getChromosome(0);
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
	 * final Genotype〈Float64Gene〉 gt = ...
	 * final Float64Gene gene = gt.getChromosome(0).getGene(0);
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
			_valid = _chromosomes.forAll(c -> c.isValid());
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
		final MSeq<Chromosome<G>> chromosomes = MSeq.valueOf(length());
		for (int i = 0, n = length(); i < n; ++i) {
			chromosomes.set(i, _chromosomes.get(i).newInstance());
		}

		return new Genotype<>(chromosomes.toISeq(), _ngenes);
	}

	Genotype<G> newInstance(final ISeq<Chromosome<G>> chromosomes) {
		return new Genotype<>(chromosomes, _ngenes);
	}

	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).and(_chromosomes).value();
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
	public Text toText() {
		return new Text(_chromosomes.toString());
	}

	@Override
	public String toString() {
		return _chromosomes.toString();
	}

	/* *************************************************************************
	 *  Static factory methods
	 * ************************************************************************/

	/**
	 * Create a new Genotype from a given array of <code>Chromosomes</code>.
	 * The <code>Chromosome</code> array <code>c</code> is cloned.
	 *
	 * @param chromosomes The {@code Chromosome} array the {@code Genotype}
	 *         consists of.
	 * @throws NullPointerException if {@code chromosomes} is null or one of its
	 *         element.
	 * @throws IllegalArgumentException if {@code chromosome.length == 0}.
	 */
	public static <G extends Gene<?, G>> Genotype<G> valueOf(
		final ISeq<? extends Chromosome<G>> chromosomes
	) {
		requireNonNull(chromosomes, "Chromosomes");
		if (chromosomes.length() == 0) {
			throw new IllegalArgumentException("Chromosomes must be given.");
		}

		@SuppressWarnings("unchecked")
		ISeq<Chromosome<G>> c = (ISeq<Chromosome<G>>)chromosomes;
		return new Genotype<>(c, ngenes(chromosomes));
	}

	/**
	 * Create a new Genotype from a given array of {@code Chromosomes}.
	 *
	 * @param chromosomes The {@code Chromosome} array the {@code Genotype}
	 *         consists of.
	 * @throws NullPointerException if {@code chromosomes} is null or one of its
	 *         element.
	 * @throws IllegalArgumentException if {@code chromosome.length == 0}.
	 */
	@SafeVarargs
	public static <G extends Gene<?, G>> Genotype<G> valueOf(
		final Chromosome<G>... chromosomes
	) {
		final ISeq<Chromosome<G>> seq = ISeq.valueOf(chromosomes);
		if (!seq.forAll(o -> o != null)) {
			throw new NullPointerException("One of the given chromosomes is null.");
		}

		return valueOf(seq);
	}

	/* *************************************************************************
	 *  XML object serialization
	 * ************************************************************************/

	@SuppressWarnings({"unchecked", "rawtypes"})
	static final XMLFormat<Genotype>
	XML = new XMLFormat<Genotype>(Genotype.class)
	{
		private static final String LENGTH = "length";
		private static final String NGENES = "ngenes";

		@Override
		public Genotype newInstance(
			final Class<Genotype> cls, final InputElement xml
		)
			throws XMLStreamException
		{
			final int length = xml.getAttribute(LENGTH, 0);
			final int ngenes = xml.getAttribute(NGENES, 0);
			final MSeq<Chromosome> chromosomes = MSeq.valueOf(length);
			for (int i = 0; i < length; ++i) {
				final Chromosome<?> c = xml.getNext();
				chromosomes.set(i, c);
			}

			return new Genotype(chromosomes.toISeq(), ngenes);
		}
		@Override
		public void write(final Genotype gt, final OutputElement xml)
			throws XMLStreamException
		{
			xml.setAttribute(LENGTH, gt.length());
			xml.setAttribute(NGENES, gt.getNumberOfGenes());
			for (int i = 0; i < gt.length(); ++i) {
				xml.add(gt._chromosomes.get(i));
			}
		}
		@Override
		public void read(final InputElement xml, final Genotype gt) {
		}
	};
}





