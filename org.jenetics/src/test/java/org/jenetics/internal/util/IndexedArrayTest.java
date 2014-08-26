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
package org.jenetics.internal.util;

import java.util.Arrays;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class IndexedArrayTest {

	@Test
	public void get() {
		final double[] values = new Random().doubles(23).toArray();
		final IndexedArray array = new IndexedArray(values.clone());

		for (int i = 0; i < values.length; ++i) {
			Assert.assertEquals(array.get(i), values[i]);
		}
	}

	@Test
	public void sort() {
		final double[] values = new Random().doubles(15).toArray();
		final IndexedArray array = new IndexedArray(values.clone());
		Assert.assertEquals(array.toString(), Arrays.toString(values));

		Arrays.sort(values);
		Assert.assertEquals(array.sort().toString(), Arrays.toString(values));
	}

}
