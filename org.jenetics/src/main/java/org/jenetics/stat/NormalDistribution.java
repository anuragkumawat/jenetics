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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.jenetics.internal.math.statistics.Φ;
import static org.jenetics.internal.math.statistics.φ;
import static org.jenetics.internal.util.object.eq;
import static org.jenetics.internal.util.object.nonNegative;

import java.io.Serializable;
import java.util.Locale;
import java.util.function.Function;

import org.jenetics.internal.util.HashBuilder;

import org.jenetics.util.Range;

/**
 * Normal (Gaussian) distribution. With
 *
 * <p>
 * <img
 *     src="doc-files/normal-pdf.gif"
 *     alt="f(x)=\frac{1}{\sqrt{2\pi \sigma^{2}}}\cdot
 *          e^{-\frac{(x-\mu)^2}{2\sigma^{2}}})"
 * >
 * </p>
 * as <i>pdf</i> and
 * <p>
 * <img
 *     src="doc-files/normal-cdf.gif"
 *     alt="f(x)=\frac{1}{2}\cdot \left [ 1 + \textup{erf} \left(
 *          \frac{x - \mu }{\sqrt{2\sigma^{2}}} \right) \right ]"
 * >
 * </p>
 * as <i>cdf</i>.
 *
 * @see <a href="http://en.wikipedia.org/wiki/Normal_distribution">Normal distribution</a>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 2.0 &mdash; <em>$Date$</em>
 */
public class NormalDistribution<
	N extends Number & Comparable<? super N>
>
	implements Distribution<N>
{

	/**
	 * <p>
	 * <img
	 *     src="doc-files/normal-pdf.gif"
	 *     alt="f(x)=\frac{1}{\sqrt{2\pi \sigma^{2}}}\cdot
	 *          e^{-\frac{(x-\mu)^2}{2\sigma^{2}}})"
	 * >
	 * </p>
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.0
	 * @version 1.0 &mdash; <em>$Date$</em>
	 */
	static final class PDF<N extends Number & Comparable<? super N>>
		implements
			Function<N, Double>,
			Serializable
	{
		private static final long serialVersionUID = 2L;

		private final Range<N> _domain;
		private final double _mean;
		private final double _var;
		private final double _stddev;

		public PDF(final Range<N> domain, final double mean, final double var) {
			_domain = domain;
			_mean = mean;
			_var = var;
			_stddev = Math.sqrt(var);
		}

		@Override
		public Double apply(final N value) {
			final double x = value.doubleValue();

			double result = 0.0;
			if (_domain.contains(value)) {
				result = φ(x, _mean, _stddev);
			}

			return result;
		}

		@Override
		public String toString() {
			return format(
				Locale.ENGLISH,
				"p(x) = N[µ=%f, σ²=%f](x)", _mean, _var
			);
		}

	}

	/**
	 * <p>
	 * <img
	 *     src="doc-files/normal-cdf.gif"
	 *     alt="f(x)=\frac{1}{2}\cdot \left [ 1 + \textup{erf} \left(
	 *          \frac{x - \mu }{\sqrt{2\sigma^{2}}} \right) \right ]"
	 * >
	 * </p>
	 *
	 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
	 * @since 1.0
	 * @version 1.0 &mdash; <em>$Date$</em>
	 */
	static final class CDF<N extends Number & Comparable<? super N>>
		implements
			Function<N, Double>,
			Serializable
	{
		private static final long serialVersionUID = 2L;

		private final double _min;
		private final double _max;
		private final double _mean;
		private final double _var;
		private final double _stddev;

		public CDF(final Range<N> domain, final double mean, final double var) {
			_min = domain.getMin().doubleValue();
			_max = domain.getMax().doubleValue();
			_mean = mean;
			_var = var;
			_stddev = Math.sqrt(var);
		}

		@Override
		public Double apply(final N value) {
			final double x = value.doubleValue();

			double result = 0.0;
			if (x < _min) {
				result = 0.0;
			} else if (x > _max) {
				result = 1.0;
			} else {
				result = Φ(x, _mean, _stddev);
			}

			return result;
		}

		@Override
		public String toString() {
			return format(
				Locale.ENGLISH,
				"P(x) = 1/2(1 + erf((x - %f)/(sqrt(2·%f))))",
				_mean, _var
			);
		}

	}

	private final Range<N> _domain;
	private final Function<N, Double> _cdf;
	private final Function<N, Double> _pdf;
	private final double _mean;
	private final double _var;

	/**
	 * Create a new normal distribution object.
	 *
	 * @param domain the domain of the distribution.
	 * @param mean the mean value of the normal distribution.
	 * @param var the variance of the normal distribution.
	 * @throws NullPointerException if the {@code domain} is {@code null}.
	 * @throws IllegalArgumentException if the variance is negative.
	 */
	public NormalDistribution(
		final Range<N> domain,
		final double mean,
		final double var
	) {
		_domain = requireNonNull(domain, "Domain");
		_mean = mean;
		_var = nonNegative(var, "Variance");

		_pdf = new PDF<>(_domain, _mean, _var);
		_cdf = new CDF<>(_domain, _mean, _var);
	}

	@Override
	public Range<N> getDomain() {
		return _domain;
	}

	/**
	 * Return a new CDF object.
	 *
	 * <p>
	 * <img
	 *     src="doc-files/normal-cdf.gif"
	 *     alt="f(x)=\frac{1}{2}\cdot \left [ 1 + \textup{erf} \left(
	 *          \frac{x - \mu }{\sqrt{2\sigma^{2}}} \right) \right ]"
	 * >
	 * </p>
	 */
	@Override
	public Function<N, Double> getCDF() {
		return _cdf;
	}

	/**
	 * Return a new PDF object.
	 *
	 * <p>
	 * <img
	 *     src="doc-files/normal-pdf.gif"
	 *     alt="f(x)=\frac{1}{\sqrt{2\pi \sigma^{2}}}\cdot e^{-\frac{(x-\mu)^2}{2\sigma^{2}}})"
	 * >
	 * </p>
	 */
	@Override
	public Function<N, Double> getPDF() {
		return _pdf;
	}

	@Override
	public int hashCode() {
		return HashBuilder.of(getClass()).and(_domain).and(_mean).and(_var).value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		}

		final NormalDistribution<?> dist = (NormalDistribution<?>)obj;
		return eq(_domain, dist._domain) &&
				eq(_mean, dist._mean) &&
				eq(_var, dist._var);
	}

	@Override
	public String toString() {
		return format("N[µ=%f, σ²=%f]", _mean, _var);
	}

}
