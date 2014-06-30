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

import static org.jenetics.internal.util.reflect.typeOf;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date$</em>
 */
public class Equality<T> {
	private final T _self;
	private final Optional<T> _other;

	private Equality(final T self, final Optional<T> other) {
		_self = Objects.requireNonNull(self);
		_other = Objects.requireNonNull(other);
	}

	public boolean test(final Predicate<T> equality) {
		return _other.isPresent() &&
			_other.filter(o -> o == _self).isPresent() ||
			_other.filter(equality).isPresent();
	}

	public boolean test() {
		return _other.isPresent();
	}

	public static <T> Equality<T> of(final T self, final Object other) {
		return new Equality<>(self, cast(typeOf(self), other));
	}

	private static <A> Optional<A> cast(final Class<A> type, final Object object) {
		return Optional.ofNullable(object)
			.filter(o -> o.getClass() == type)
			.map(type::cast);
	}

}
