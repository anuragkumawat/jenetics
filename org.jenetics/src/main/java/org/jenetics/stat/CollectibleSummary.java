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
package org.jenetics.stat;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version @__version__@ &mdash; <em>$Date$</em>
 * @since @__version__@
 */
final class CollectibleSummary<N extends Number & Comparable<? super N>>
	implements Summary<N>
{

	private long _samples = 0L;
	private N _min;
	private N _max;

	// Sum variables which are used for the Kahan summation algorithm.
	private double _sum = 0.0;
	private double _c = 0.0;
	private double _y = 0.0;
	private double _t = 0.0;

	private double _mean = Double.NaN;

	void accumulate(final N number) {
		++_samples;
		updateMin(number);
		updateMax(number);
		updateSum(number.doubleValue());
	}

	private void updateMin(final N number) {
		if (_min == null || _min.compareTo(number) > 0) {
			_min = number;
		}
	}

	private void updateMax(final N number) {
		if (_max == null || _max.compareTo(number) < 0) {
			_max = number;
		}
	}

	private void updateSum(final double number) {
		_y = number - _c;
		_t = _sum + _y;
		_c = _t - _sum - _y;
		_sum = _t;
	}

	CollectibleSummary<N> combine(final CollectibleSummary<N> other) {
		final CollectibleSummary<N> result = new CollectibleSummary<>();

		result._samples += other._samples;
		result._min = _min.compareTo(other._min) < 0 ? _min : other._min;
		result._max = _max.compareTo(other._max) > 0 ? _max : other._max;
		result.updateSum(_sum);
		result.updateSum(other._sum);

		return result;
	}

	@Override
	public long getSampleSize() {
		return _samples;
	}

	@Override
	public N getMin() {
		return _min;
	}

	@Override
	public N getMax() {
		return _max;
	}

	@Override
	public double getSum() {
		return _sum;
	}

	@Override
	public double getMean() {
		return 0;
	}

	@Override
	public double getVariance() {
		return 0;
	}

	@Override
	public double getSkewness() {
		return 0;
	}

	@Override
	public double getKurtosis() {
		return 0;
	}

}
