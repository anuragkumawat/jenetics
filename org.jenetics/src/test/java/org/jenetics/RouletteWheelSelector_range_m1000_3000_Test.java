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

import org.jenetics.stat.Distribution;
import org.jenetics.stat.Histogram;
import org.jenetics.stat.LinearDistribution;
import org.jenetics.util.Range;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class RouletteWheelSelector_range_m1000_3000_Test extends RouletteWheelSelectorTest {

	private Range<Double> _domain = new Range<>(-1000.0, 3000.0);

	@Override
	protected final Range<Double> getDomain() {
		return _domain;
	}

	@Override
	protected Distribution<Double> getDistribution() {
		return new LinearDistribution<>(getDomain(), 0);
	}

	@Override
	protected double χ2(
		final Histogram<Double> histogram,
		final Distribution<Double> distribution
	) {
		return histogram.χ2(
			distribution.getCDF(),
			getDomain().getMin(), getDomain().getMax()
		);
	}

}
