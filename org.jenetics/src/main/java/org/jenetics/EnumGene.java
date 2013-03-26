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
package org.jenetics;

import static org.jenetics.util.object.eq;
import static org.jenetics.util.object.hashCodeOf;

import java.util.function.Function;
import java.util.function.Supplier;

import javolution.context.ObjectFactory;

import org.jenetics.util.Array;
import org.jenetics.util.Factory;
import org.jenetics.util.ISeq;
import org.jenetics.util.RandomRegistry;
import org.jenetics.util.object;

/**
 * Gene which holds enumerable (countable) genes. Will be used for combinatorial
 * problems in combination with the {@link PermutationChromosome}.
 * <p/>
 * The following code shows how to create a combinatorial genotype factory which
 * can be used when creating an {@link GeneticAlgorithm} instance.
 * [code]
 * ISeq<Integer> alleles = Array.box(1, 2, 3, 4, 5, 6, 7, 8).toISeq();
 * Factory<Genotype<EnumGene<Integer>>> gtf = Genotype.valueOf(
 *     PermutationChromosome.valueOf(alleles)
 * );
 * [/code]
 *
 * The following code shows the assurances of the {@code EnumGene}.
 * [code]
 * ISeq<Integer> alleles = Array.box(1, 2, 3, 4, 5, 6, 7, 8).toISeq();
 * EnumGene<Integer> gene = EnumGene.valueOf(alleles, 5);
 *
 * assert(gene.getAlleleIndex() == 5);
 * assert(gene.getAllele() == gene.getValidAlleles().get(5));
 * [/code]
 *
 * @see PermutationChromosome
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date$</em>
 */
public final class EnumGene<A> implements Gene<A, EnumGene<A>> {

	private static final long serialVersionUID = 1L;

	private ISeq<A> _validAlleles;
	private int _alleleIndex = -1;

	EnumGene() {
	}

	/**
	 * Return sequence of the valid alleles where this gene is a part of.
	 *
	 * @return the sequence of the valid alleles.
	 */
	public ISeq<A> getValidAlleles() {
		return _validAlleles;
	}

	/**
	 * Return the index of the allele this gene is representing.
	 *
	 * @return the index of the allele this gene is representing.
	 */
	public int getAlleleIndex() {
		return _alleleIndex;
	}

	@Override
	public A getAllele() {
		return _validAlleles.get(_alleleIndex);
	}

	@Override
	public EnumGene<A> copy() {
		final EnumGene<A> gene = new EnumGene<>();
		gene._validAlleles = _validAlleles;
		gene._alleleIndex = _alleleIndex;
		return gene;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public EnumGene<A> newInstance() {
		@SuppressWarnings("unchecked")
		final EnumGene<A> gene = FACTORY.object();

		gene._alleleIndex = RandomRegistry.getRandom().nextInt(_validAlleles.length());
		gene._validAlleles = _validAlleles;
		return gene;
	}

	@Override
	public int hashCode() {
		return hashCodeOf(EnumGene.class)
				.and(_alleleIndex)
				.and(_validAlleles).value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		final EnumGene<?> pg = (EnumGene<?>)obj;
		return eq(_alleleIndex, pg._alleleIndex) &&
				eq(_validAlleles, pg._validAlleles);
	}

	@Override
	public String toString() {
		return object.str(getAllele());
	}

	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	static <T> Function<Integer, EnumGene<T>> ToGene(
		final ISeq<T> validAlleles
	) {
		return index -> valueOf(validAlleles, index);
	}

	static <T> Supplier<EnumGene<T>> Gene(final ISeq<T> validAlleles) {
		return new Supplier<EnumGene<T>>() {
			private int _index = 0;
			@Override
			public EnumGene<T> get() {
				return EnumGene.valueOf(validAlleles, _index++);
			}
		};
	}


	@SuppressWarnings("rawtypes")
	private static final ObjectFactory<EnumGene>
	FACTORY = new ObjectFactory<EnumGene>() {
		@Override
		protected EnumGene create() {
			return new EnumGene();
		}
	};

	public static <G> EnumGene<G> valueOf(
		final G[] validAlleles,
		final int alleleIndex
	) {
		return valueOf(Array.valueOf(validAlleles).toISeq(), alleleIndex);
	}

	public static <G> EnumGene<G> valueOf(
		final ISeq<G> validAlleles,
		final int alleleIndex
	) {
		if (validAlleles.length() == 0) {
			throw new IllegalArgumentException(
				"Array of valid alleles must be greater than zero."
			);
		}

		if (alleleIndex < 0 || alleleIndex >= validAlleles.length()) {
			throw new IndexOutOfBoundsException(String.format(
				"Allele index is not in range [0, %d).", alleleIndex
			));
		}

		@SuppressWarnings("unchecked")
		final EnumGene<G> gene = FACTORY.object();

		gene._validAlleles = validAlleles;
		gene._alleleIndex = alleleIndex;
		return gene;
	}

	public static <G> EnumGene<G> valueOf(final G[] validAlleles) {
		return valueOf(Array.valueOf(validAlleles).toISeq());
	}

	public static <G> EnumGene<G> valueOf(final ISeq<G> validAlleles) {
		if (validAlleles.length() == 0) {
			throw new IllegalArgumentException(
				"Array of valid alleles must be greater than zero."
			);
		}

		@SuppressWarnings("unchecked")
		final EnumGene<G> gene = FACTORY.object();
		gene._validAlleles = validAlleles;
		gene._alleleIndex = RandomRegistry.getRandom().nextInt(validAlleles.length());
		return gene;
	}

}





