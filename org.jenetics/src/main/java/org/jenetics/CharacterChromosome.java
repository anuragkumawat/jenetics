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

import static java.lang.String.format;
import static org.jenetics.util.object.eq;
import static org.jenetics.util.object.hashCodeOf;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.function.Supplier;

import javolution.text.CharArray;
import javolution.xml.XMLFormat;
import javolution.xml.XMLSerializable;
import javolution.xml.stream.XMLStreamException;

import org.jenetics.util.Array;
import org.jenetics.util.CharSeq;
import org.jenetics.util.Factory;
import org.jenetics.util.ISeq;

/**
 * CharacterChromosome which represents character sequences.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date$</em>
 */
public class CharacterChromosome
	extends
		AbstractChromosome<CharacterGene>
	implements
		CharSequence,
		XMLSerializable
{
	private static final long serialVersionUID = 1L;

	private transient CharSeq _validCharacters;

	/**
	 * Create a new chromosome with the {@link CharacterGene#DEFAULT_CHARACTERS}
	 * char set as valid characters.
	 *
	 * @param length the {@code length} of the new chromosome.
	 * @throws IllegalArgumentException if the {@code length} is smaller than
	 *         one.
	 */
	public CharacterChromosome(final int length) {
		super(
			new Array<CharacterGene>(length).fill(
					() -> CharacterGene.valueOf(CharacterGene.DEFAULT_CHARACTERS)
				).toISeq()
		);
		_validCharacters = CharacterGene.DEFAULT_CHARACTERS;
		_valid = true;
	}

	/**
	 * Create a new chromosome with the {@code validCharacters} char set as
	 * valid characters.
	 *
	 * @param validCharacters the valid characters for this chromosome.
	 * @param length the length of the new chromosome.
	 * @throws NullPointerException if the {@code validCharacters} is
	 *         {@code null}.
	 * @throws IllegalArgumentException if the {@code length} is smaller than
	 *         one.
	 */
	public CharacterChromosome(final CharSeq validCharacters, final int length) {
		super(
			new Array<CharacterGene>(length).fill(
					() -> CharacterGene.valueOf(validCharacters)
				).toISeq()
		);
		_validCharacters = validCharacters;
		_valid = true;
	}

	/**
	 * Create a new chromosome from the given {@code genes} array. The genes
	 * array is copied, so changes to the given genes array doesn't effect the
	 * genes of this chromosome.
	 *
	 * @param genes the genes that form the chromosome.
	 * @throws NullPointerException if the given gene array is {@code null}.
	 * @throws IllegalArgumentException if the length of the gene array is
	 *         smaller than one.
	 */
	public CharacterChromosome(final ISeq<CharacterGene> genes) {
		super(genes);
		_validCharacters = genes.get(0).getValidCharacters();
	}

	/**
	 * Create a new chromosome from the given genes (given as string).
	 *
	 * @param genes the character genes.
	 * @param validCharacters the valid characters.
	 * @throws IllegalArgumentException if not all genes are in the set of valid
	 *         characters or the genes string is empty.
	 */
	public CharacterChromosome(final String genes, final CharSeq validCharacters) {
		super(
			new Array<CharacterGene>(genes.length()).fill(new Supplier<CharacterGene>() {
				private int _index = 0;
				@Override
				public CharacterGene get() {
					final char c = genes.charAt(_index++);
					if (!validCharacters.contains(c)) {
						throw new IllegalArgumentException(format(
								"Character '%s' not in valid characters %s",
								c, validCharacters
							));
					}
					return CharacterGene.valueOf(c, validCharacters);
				}
			}).toISeq()
		);

		_validCharacters = validCharacters;
	}

	/**
	 * Create a new chromosome from the given genes (given as string).
	 *
	 * @param genes the character genes.
	 * @throws IllegalArgumentException if not all genes are in the set of valid
	 *         characters or the genes is an empty string.
	 */
	public CharacterChromosome(final String genes) {
		this(genes, CharacterGene.DEFAULT_CHARACTERS);
	}

	@Override
	public char charAt(final int index) {
		return getGene(index).getAllele();
	}

	@Override
	public CharacterChromosome subSequence(final int start, final int end) {
		return new CharacterChromosome(_genes.subSeq(start, end));
	}

	/**
	 * @throws NullPointerException if the given gene array is {@code null}.
	 */
	@Override
	public CharacterChromosome newInstance(final ISeq<CharacterGene> genes) {
		return new CharacterChromosome(genes);
	}

	/**
	 * Create a new, <em>random</em> chromosome.
	 */
	@Override
	public CharacterChromosome newInstance() {
		return new CharacterChromosome(_validCharacters, length());
	}

	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).
				and(super.hashCode()).
				and(_validCharacters).value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		final CharacterChromosome cc = (CharacterChromosome)obj;
		return super.equals(obj) && eq(_validCharacters, cc._validCharacters);
	}

	@Override
	public String toString() {
		final StringBuilder out = new StringBuilder();
		for (CharacterGene gene : this) {
			out.append(gene);
		}
		return out.toString();
	}

	/* *************************************************************************
	 *  XML object serialization
	 * ************************************************************************/

	static final XMLFormat<CharacterChromosome>
	XML = new XMLFormat<CharacterChromosome>(CharacterChromosome.class)
	{
		private static final String LENGTH = "length";
		private static final String VALID_CHARS = "valid-characters";

		@Override
		public CharacterChromosome newInstance(
			final Class<CharacterChromosome> cls, final InputElement xml
		)
			throws XMLStreamException
		{
			final int length = xml.getAttribute(LENGTH, 0);
			final CharSeq validCharacters = new CharSeq(xml.getAttribute(
					VALID_CHARS, CharacterGene.DEFAULT_CHARACTERS.toString()
				));

			final Array<CharacterGene> array = new Array<>(length);
			final CharArray values = xml.getText();
			for (int i = 0; i < length; ++i) {
				array.set(i, CharacterGene.valueOf(values.charAt(i), validCharacters));
			}
			return new CharacterChromosome(array.toISeq());
		}
		@Override
		public void write(final CharacterChromosome chromosome, final OutputElement xml)
			throws XMLStreamException
		{
			xml.setAttribute(LENGTH, chromosome.length());
			xml.setAttribute(VALID_CHARS, chromosome._validCharacters.toString());
			final StringBuilder out = new StringBuilder(chromosome.length());
			for (CharacterGene gene : chromosome) {
				out.append(gene.getAllele().charValue());
			}
			xml.addText(out.toString());
		}
		@Override
		public void read(final InputElement element, final CharacterChromosome chromosome) {
		}

	};

	/* *************************************************************************
	 *  Java object serialization
	 * ************************************************************************/

	private void writeObject(final ObjectOutputStream out)
		throws IOException
	{
		out.defaultWriteObject();

		out.writeInt(length());
		out.writeObject(_validCharacters);

		for (CharacterGene gene : _genes) {
			out.writeChar(gene.getAllele().charValue());
		}
	}

	private void readObject(final ObjectInputStream in)
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		final int length = in.readInt();
		_validCharacters = (CharSeq)in.readObject();

		final Array<CharacterGene> genes = new Array<>(length);
		for (int i = 0; i < length; ++i) {
			final CharacterGene gene = CharacterGene.valueOf(
				in.readChar(),
				_validCharacters
			);
			genes.set(i, gene);
		}

		_genes = genes.toISeq();
	}

}



