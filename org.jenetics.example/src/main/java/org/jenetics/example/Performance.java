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
 * 	 Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.example;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import java.io.Serializable;
import java.util.function.Function;

import javax.measure.Measure;
import javax.measure.unit.SI;

import org.jscience.mathematics.number.Float64;

import org.jenetics.Float64Chromosome;
import org.jenetics.Float64Gene;
import org.jenetics.Genotype;
import org.jenetics.Phenotype;
import org.jenetics.Population;
import org.jenetics.util.Factory;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version 1.0 &mdash; <em>$Date$</em>
 */
public class Performance {

	private static final class Perf
		implements Function<Genotype<Float64Gene>, Float64>,
					Serializable
	{
		private static final long serialVersionUID = 1L;

		@Override
		public Float64 apply(final Genotype<Float64Gene> genotype) {
			final Float64Gene gene = genotype.getChromosome().getGene(0);
			final double radians = toRadians(gene.doubleValue());
			return Float64.valueOf(Math.log(sin(radians)*cos(radians)));
		}
	}

	public static void main(String[] args) {
		final Perf ff = new Perf();
		final Factory<Genotype<Float64Gene>> gtf = Genotype.valueOf(new Float64Chromosome(0, 360));
		final Function<Float64, Float64> fs = a -> a;

		final int size = 1000000;
		final Population<Float64Gene, Float64> population = new Population<>(size);
		for (int i = 0; i < size; ++i) {
			final Phenotype<Float64Gene, Float64> pt = Phenotype.valueOf(
				gtf.newInstance(), ff, fs, 0
			);
			population.add(pt);
		}

		long start = System.currentTimeMillis();
		for (int i = 0; i < size; ++i) {
			population.get(i).getFitness();
		}
		long stop = System.currentTimeMillis();
		System.out.println(Measure.valueOf(stop - start, SI.MILLI(SI.SECOND)));

		start = System.currentTimeMillis();
		for (int i = 0; i < size; ++i) {
			population.get(i).getFitness();
		}
		stop = System.currentTimeMillis();
		System.out.println(Measure.valueOf(stop - start, SI.MILLI(SI.SECOND)));


	}


}





