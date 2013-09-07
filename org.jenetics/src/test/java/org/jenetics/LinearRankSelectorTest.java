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

import org.jscience.mathematics.number.Float64;

import org.jenetics.stat.Distribution;
import org.jenetics.stat.LinearDistribution;
import org.jenetics.util.Factory;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class LinearRankSelectorTest
	extends ProbabilitySelectorTester<LinearRankSelector<Float64Gene, Float64>>
{

	@Override
	protected boolean isSorted() {
		return true;
	}

	@Override
	protected Factory<LinearRankSelector<Float64Gene, Float64>> getFactory() {
		return SelectorFactories.LinearRankSelector;
	}

	@Override
	protected Distribution<Float64> getDistribution() {
		return new LinearDistribution<>(getDomain(), 0);
	}

	@Override
	protected LinearRankSelector<Float64Gene, Float64> getSelector() {
		return new LinearRankSelector<>(0.0);
	}


}




