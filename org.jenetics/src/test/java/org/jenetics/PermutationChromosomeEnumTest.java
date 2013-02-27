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

import org.jenetics.util.Array;
import org.jenetics.util.Factory;
import org.jenetics.util.ISeq;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class PermutationChromosomeEnumTest
	extends ChromosomeTester<EnumGene<PermutationEnum>>
{


	private final Factory<Chromosome<EnumGene<PermutationEnum>>>
	_factory = new Factory<Chromosome<EnumGene<PermutationEnum>>>() {
		private final ISeq<PermutationEnum> _alleles =
			Array.valueOf(PermutationEnum.values()).toISeq();

		@Override
		public PermutationChromosome<PermutationEnum> newInstance() {
			return new PermutationChromosome<>(_alleles);
		}
	};

	@Override
	protected Factory<Chromosome<EnumGene<PermutationEnum>>> getFactory() {
		return _factory;
	}

}
