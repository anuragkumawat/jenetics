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
package org.jenetics.util;

import static org.jenetics.util.arrays.sort;

import java.util.Comparator;
import java.util.Random;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class arraysTest {

	@Test
	public void iselect() {
		final Array<Integer> array = new Array<>(100);
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, i);
		}

		for (int i = 0; i < array.length(); ++i) {
			//System.out.println(ArrayUtils.iselect(array, i));
			//Assert.assertEquals(ArrayUtils.iselect(array, i), i);
		}
		//System.out.println(ArrayUtils.iselect(array, 2));
	}

	@Test
	public void sorted() {
		final Array<Integer> array = new Array<>(100);
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, i);
		}
		Assert.assertTrue(array.isSorted());

		array.set(10, 5);
		Assert.assertFalse(array.isSorted());

		array.setAll(-234);
		Assert.assertTrue(array.isSorted());

		for (int i = 0; i < array.length(); ++i) {
			array.set(i, array.length() - i);
		}
		Assert.assertFalse(array.isSorted());
	}

	@Test
	public void sorted2() {
		final Array<Integer> array = new Array<>(100);
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, i);
		}
		Assert.assertFalse(array.isSorted(new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				return -o1.compareTo(o2);
			}
		}));
	}

	@Test
	public void sort1() {
		final Random random = new Random();
		final Array<Integer> array = new Array<>(100);
		array.fill(() -> random.nextInt(10000));
		Assert.assertFalse(array.isSorted());

		final Array<Integer> clonedArray = array.copy();
		org.jenetics.util.arrays.sort(array.subSeq(30, 40));
		Assert.assertTrue(array.subSeq(30, 40).isSorted());
		Assert.assertEquals(array.subSeq(0, 30), clonedArray.subSeq(0, 30));
		Assert.assertEquals(array.subSeq(40), clonedArray.subSeq(40));
	}

//	@Test
//	public void performance() {
//		final int SIZE = 1000;
//		final Population<IntegerGene, Integer64> pop = new Population<IntegerGene, Integer64>(SIZE);
//		for (int i = 0; i < SIZE; ++i) {
//			pop.add(Phenotype.valueOf(
//					Genotype.valueOf(new IntegerChromosome(IntegerGene.valueOf(i, 0, SIZE))),
//					new FitnessFunction<IntegerGene, Integer64>() {
//						private static final long serialVersionUID = 1L;
//						@Override
//						public Integer64 evaluate(Genotype<IntegerGene> genotype) {
//							return null;
//						}
//					}, i));
//		}
//
//		final Timer timer = new Timer();
//		timer.start();
//		for (int j = 0; j < 10000; ++j) {
//			for (int i = 0; i < pop.size(); ++i) {
//				final Phenotype<?, ?> pt = pop.get(i);
//			}
//		}
//		timer.stop();
//		System.out.println(timer.toString());
//
//		timer.reset();
//		timer.start();
//		for (int i = 0; i < 10000; ++i) {
//			ArrayUtils.subset(1000, 400, RandomRegistry.getRandom());
//		}
//		timer.stop();
//		System.out.println(timer);
//
//	}

	public static void main(String[] args) {
		Array<Integer> array = new Array<>(10000000);
		for (int i = 0; i < array.length(); ++i) {
			array.set(i, (int)(Math.random()*1000));
		}

		Timer timer = new Timer();
		timer.start();
		sort(array);
		timer.stop();
		Reporter.log(timer.toString());
	}


}
