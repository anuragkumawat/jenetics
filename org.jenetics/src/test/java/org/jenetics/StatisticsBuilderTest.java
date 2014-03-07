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

import java.lang.reflect.Method;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import org.jenetics.util.RandomRegistry;
import org.jenetics.util.Scoped;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class StatisticsBuilderTest {

	public Object newBuilder() {
		return new Statistics.Builder<DoubleGene, Double>();
	}

	@DataProvider(name = "properties")
	public Object[][] builderProperties() {
		try (Scoped<Random> s = RandomRegistry.scope(new Random(123456))) {
			return new Object[][] {
				{"generation", Integer.TYPE, s.get().nextInt(1000)},
				{"invalid", Integer.TYPE, s.get().nextInt(1000)},
				{"killed", Integer.TYPE, s.get().nextInt(10000)},
				{"samples", Integer.TYPE, s.get().nextInt(1000)},
				{"ageMean", Double.TYPE, s.get().nextDouble()},
				{"ageVariance", Double.TYPE, s.get().nextDouble()},
				{"bestPhenotype", Phenotype.class, TestUtils.newDoublePhenotype()},
				{"worstPhenotype", Phenotype.class, TestUtils.newDoublePhenotype()},
				{"optimize", Optimize.class, Optimize.MINIMUM},
				{"optimize", Optimize.class, Optimize.MAXIMUM}
			};
		}
	}

	@Test(dataProvider = "properties")
	public void build(final String name, final Class<?> valueType, final Object value)
		throws Exception
	{
		final Object builder = newBuilder();
		final Method setter = builder.getClass().getMethod(name, valueType);
		final Method build = builder.getClass().getMethod("build");

		setter.invoke(builder, value);
		final Object statistics = build.invoke(builder);
		final Method getter = statistics.getClass().getMethod(toGetter(name));
		final Object result = getter.invoke(statistics);

		Assert.assertEquals(result, value);
	}

	private static String toGetter(final String name) {
		return "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
	}

}
