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

import java.util.Random;

import org.jscience.mathematics.number.Float64;

import org.jenetics.util.Factory;
import org.jenetics.util.RandomRegistry;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class SelectorFactories {

	private SelectorFactories() {
	}

	public static Factory<BoltzmannSelector<Float64Gene, Float64>>
	BoltzmannSelector = new Factory<BoltzmannSelector<Float64Gene, Float64>>() {
		@Override
		public BoltzmannSelector<Float64Gene, Float64> newInstance() {
			final Random random = RandomRegistry.getRandom();
			return new BoltzmannSelector<>(random.nextDouble());
		}
	};

	public static Factory<ExponentialRankSelector<Float64Gene, Float64>>
	ExponentialRankSelector = new Factory<ExponentialRankSelector<Float64Gene, Float64>>() {
		@Override
		public ExponentialRankSelector<Float64Gene, Float64> newInstance() {
			final Random random = RandomRegistry.getRandom();
			return new ExponentialRankSelector<>(random.nextDouble());
		}
	};

	public static Factory<LinearRankSelector<Float64Gene, Float64>>
	LinearRankSelector = new Factory<LinearRankSelector<Float64Gene, Float64>>() {
		@Override
		public LinearRankSelector<Float64Gene, Float64> newInstance() {
			final Random random = RandomRegistry.getRandom();
			return new LinearRankSelector<>(random.nextDouble());
		}
	};

	public static Factory<RouletteWheelSelector<Float64Gene, Float64>>
	RouletteWheelSelector = new Factory<RouletteWheelSelector<Float64Gene, Float64>>() {
		@Override
		public RouletteWheelSelector<Float64Gene, Float64> newInstance() {
			return new RouletteWheelSelector<>();
		}
	};

}





