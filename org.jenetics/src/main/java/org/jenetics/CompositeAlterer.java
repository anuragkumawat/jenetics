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

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.jenetics.util.object.NonNull;
import static org.jenetics.util.object.eq;
import static org.jenetics.util.object.hashCodeOf;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.jenetics.util.Array;
import org.jenetics.util.ISeq;
import org.jenetics.util.Seq;

/**
 * Combines several alterers to one.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.2 &mdash; <em>$Date$</em>
 */
public final class CompositeAlterer<G extends Gene<?, G>>
	extends AbstractAlterer<G>
{

	private final ISeq<Alterer<G>> _alterers;

	/**
	 * Combine the given alterers.
	 *
	 * @param alterers the alterers to combine.
	 * @throws NullPointerException if one of the alterers is {@code null}.
	 */
	public CompositeAlterer(final Seq<Alterer<G>> alterers) {
		super(1.0);

		alterers.forEach(NonNull("Alterer"));
		_alterers = normalize(alterers).toISeq();
	}

	/**
	 * Combine the given alterers.
	 *
	 * @param alterers the alterers to combine.
	 * @throws NullPointerException if one of the alterers is {@code null}.
	 *
	 * @deprecated Use {@link #valueOf(Alterer...)} instead.
	 */
	@Deprecated
	@SafeVarargs
	public CompositeAlterer(final Alterer<G>... alterers) {
		this(Array.valueOf(alterers));
	}

	private static <G extends Gene<?, G>>
	Array<Alterer<G>> normalize(final Seq<Alterer<G>> alterers) {
		final Deque<Alterer<G>> stack = new LinkedList<>(alterers.asList());

		final List<Alterer<G>> normalized = new LinkedList<>();

		while (!stack.isEmpty()) {
			final Alterer<G> alterer = stack.pollFirst();

			if (alterer instanceof CompositeAlterer<?>) {
				final CompositeAlterer<G> calterer = (CompositeAlterer<G>)alterer;

				for (int i = calterer.getAlterers().length(); --i >= 0;) {
					stack.addFirst(calterer.getAlterers().get(i));
				}
			} else {
				normalized.add(alterer);
			}
		}

		return Array.valueOf(normalized);
	}

	@Override
	public <C extends Comparable<? super C>>
	int alter(final Population<G, C> population, final int generation) {
		final AtomicInteger alterations = new AtomicInteger(0);
		_alterers.forEach(a -> {
			alterations.addAndGet(a.alter(population, generation));
		});
		return alterations.get();
	}

	/**
	 * Create a new CompositeAlterer with the given alterer appended.
	 *
	 * @param alterer the alterer to append.
	 * @return a new CompositeAlterer.
	 * @throws NullPointerException if the given alterer is {@code null}.
	 */
	public CompositeAlterer<G> append(final Alterer<G> alterer) {
		return CompositeAlterer.valueOf(this, requireNonNull(alterer, "Alterer"));
	}

	/**
	 * Return the alterers this alterer consists of. The returned array is sealed
	 * and cannot be changed.
	 *
	 * @return the alterers this alterer consists of.
	 */
	public ISeq<Alterer<G>> getAlterers() {
		return _alterers;
	}

	@Override
	public int hashCode() {
		return hashCodeOf(getClass()).and(_alterers).value();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != getClass()) {
			return false;
		}

		final CompositeAlterer<?> alterer = (CompositeAlterer<?>)obj;
		return eq(_alterers, alterer._alterers);
	}

	@Override
	public String toString() {
		return format("%s[%s]", getClass().getSimpleName(), _alterers);
	}

	/**
	 * Combine the given alterers.
	 *
	 * @param alterers the alterers to combine.
	 * @throws NullPointerException if one of the alterers is {@code null}.
	 */
	@SafeVarargs
	public static <G extends Gene<?, G>>
	CompositeAlterer<G> valueOf(final Alterer<G>... alterers) {
		return new CompositeAlterer<>(Array.valueOf(alterers));
	}

	/**
	 * Joins the given alterer and returns a new CompositeAlterer object. If one
	 * of the given alterers is a CompositeAlterer the sub alterers of it are
	 * unpacked and appended to the newly created CompositeAlterer.
	 *
	 * @param <T> the gene type of the alterers.
	 * @param a1 the first alterer.
	 * @param a2 the second alterer.
	 * @return a new CompositeAlterer object.
	 * @throws NullPointerException if one of the given alterer is {@code null}.
	 */
	public static <T extends Gene<?, T>> CompositeAlterer<T> join(
		final Alterer<T> a1,
		final Alterer<T> a2
	) {
		return CompositeAlterer.valueOf(a1, a2);
	}
}







