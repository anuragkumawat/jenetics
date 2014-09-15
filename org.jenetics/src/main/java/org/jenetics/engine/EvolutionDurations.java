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
package org.jenetics.engine;

import static java.util.Objects.requireNonNull;
import static org.jenetics.internal.util.Equality.eq;

import java.io.Serializable;
import java.time.Duration;

import org.jenetics.internal.util.Equality;
import org.jenetics.internal.util.Hash;

/**
 * This class contains timing information about one evolution step.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 3.0
 * @version 3.0 &mdash; <em>$Date$</em>
 */
public final class EvolutionDurations implements Serializable {
	private static final long serialVersionUID = 1L;

	private final Duration _offspringSelectionDuration;
	private final Duration _survivorsSelectionDuration;
	private final Duration _offspringAlterDuration;
	private final Duration _offspringFilterDuration;
	private final Duration _survivorFilterDuration;
	private final Duration _evaluationDuration;
	private final Duration _evolveDuration;

	private EvolutionDurations(
		final Duration offspringSelectionDuration,
		final Duration survivorsSelectionDuration,
		final Duration offspringAlterDuration,
		final Duration offspringFilterDuration,
		final Duration survivorFilterDuration,
		final Duration evaluationDuration,
		final Duration evolveDuration
	) {
		_offspringSelectionDuration = requireNonNull(offspringSelectionDuration);
		_survivorsSelectionDuration = requireNonNull(survivorsSelectionDuration);
		_offspringAlterDuration = requireNonNull(offspringAlterDuration);
		_offspringFilterDuration = requireNonNull(offspringFilterDuration);
		_survivorFilterDuration = requireNonNull(survivorFilterDuration);
		_evaluationDuration = requireNonNull(evaluationDuration);
		_evolveDuration = requireNonNull(evolveDuration);
	}

	/**
	 * Return the duration needed for selecting the offspring population.
	 *
	 * @return the duration needed for selecting the offspring population
	 */
	public Duration getOffspringSelectionDuration() {
		return _offspringSelectionDuration;
	}

	/**
	 * Return the duration needed for selecting the survivors population.
	 *
	 * @return the duration needed for selecting the survivors population
	 */
	public Duration getSurvivorsSelectionDuration() {
		return _survivorsSelectionDuration;
	}

	/**
	 * Return the duration needed for altering the offspring population.
	 *
	 * @return the duration needed for altering the offspring population
	 */
	public Duration getOffspringAlterDuration() {
		return _offspringAlterDuration;
	}

	/**
	 * Return the duration needed for removing and replacing invalid offspring
	 * individuals.
	 *
	 * @return the duration needed for removing and replacing invalid offspring
	 *         individuals
	 */
	public Duration getOffspringFilterDuration() {
		return _offspringFilterDuration;
	}

	/**
	 * Return the duration needed for removing and replacing old and invalid
	 * survivor individuals.
	 *
	 * @return the duration needed for removing and replacing old and invalid
	 *         survivor individuals
	 */
	public Duration getSurvivorFilterDuration() {
		return _survivorFilterDuration;
	}

	/**
	 * Return the duration needed for evaluating the fitness function of the new
	 * individuals.
	 *
	 * @return the duration needed for evaluating the fitness function of the new
	 *         individuals
	 */
	public Duration getEvaluationDuration() {
		return _evaluationDuration;
	}

	/**
	 * Return the duration needed for the whole evolve step.
	 *
	 * @return the duration needed for the whole evolve step
	 */
	public Duration getEvolveDuration() {
		return _evolveDuration;
	}

	@Override
	public int hashCode() {
		return Hash.of(getClass())
			.and(_offspringSelectionDuration)
			.and(_survivorsSelectionDuration)
			.and(_offspringAlterDuration)
			.and(_offspringFilterDuration)
			.and(_survivorFilterDuration)
			.and(_evaluationDuration)
			.and(_evolveDuration).value();
	}

	@Override
	public boolean equals(final Object obj) {
		return Equality.of(this, obj).test(d ->
			eq(_offspringSelectionDuration, d._offspringSelectionDuration) &&
			eq(_survivorsSelectionDuration, d._survivorsSelectionDuration) &&
			eq(_offspringAlterDuration, d._offspringAlterDuration) &&
			eq(_offspringFilterDuration, d._offspringFilterDuration) &&
			eq(_survivorFilterDuration, d._survivorFilterDuration) &&
			eq(_evaluationDuration, d._evaluationDuration) &&
			eq(_evolveDuration, d._evolveDuration)
		);
	}

	/**
	 * Return an new {@code EvolutionDurations} object with the given values.
	 *
	 * @param offspringSelectionDuration the duration needed for selecting the
	 *        offspring population
	 * @param survivorsSelectionDuration the duration needed for selecting the
	 *        survivors population
	 * @param offspringAlterDuration the duration needed for altering the
	 *        offspring population
	 * @param offspringFilterDuration the duration needed for removing and
	 *        replacing invalid offspring individuals
	 * @param survivorFilterDuration the duration needed for removing and
	 *        replacing old and invalid survivor individuals
	 * @param evaluationDuration the duration needed for evaluating the fitness
	 *        function of the new individuals
	 * @param evolveDuration the duration needed for the whole evolve step
	 * @return an new durations object
	 * @throws java.lang.NullPointerException if one of the arguments is
	 *         {@code null}
	 */
	public static EvolutionDurations of(
		final Duration offspringSelectionDuration,
		final Duration survivorsSelectionDuration,
		final Duration offspringAlterDuration,
		final Duration offspringFilterDuration,
		final Duration survivorFilterDuration,
		final Duration evaluationDuration,
		final Duration evolveDuration
	) {
		return new EvolutionDurations(
			offspringSelectionDuration,
			survivorsSelectionDuration,
			offspringAlterDuration,
			offspringFilterDuration,
			survivorFilterDuration,
			evaluationDuration,
			evolveDuration
		);
	}

}
