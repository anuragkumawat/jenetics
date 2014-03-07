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
package org.jenetics.util;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import javolution.context.ConcurrentContext;
import javolution.context.ObjectFactory;
import javolution.util.FastList;

/**
 * Since the parallelization of the library is build on the {@link ConcurrentContext}
 * of the <a href="http://javolution.org/">Javolution</a> project, this class
 * allows you to share a common {@link ForkJoinPool} for the GA and the rest of
 * your application.
 * <p/>
 * The following example shows how to use the {@code ForkJoinContext} directly:
 * [code]
 * public class Main {
 *     public static void main(final String[] args) {
 *         final int nthreads = 10;
 *
 *         // Create a java ForkJoinPool and initialize the ForkJoinContext.
 *         final ForkJoinPool pool = new ForkJoinPool(nthreads);
 *         ForkJoinContext.setForkJoinPool(pool);
 *
 *         // Set the concurrence context to use by the javolution context.
 *         ConcurrentContext.setContext(ForkJoinContext.class);
 *
 *         // Execute some task concurrently.
 *         ConcurrentContext.enter()
 *         try {
 *             ConcurrentContext.execute(...);
 *             ConcurrentContext.execute(...);
 *         } finally {
 *             ConcurrentContext.exit();
 *         }
 *     }
 * }
 * [/code]
 *
 * A more convenient way for using the {@code ForkJoinContext} allows the
 * {@link Concurrency} class.
 *
 * @see Concurrency
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date$</em>
 */
public final class ForkJoinContext extends ConcurrentContext {

	private static final long serialVersionUID = 1L;

	private final static AtomicReference<ForkJoinPool> _POOL = new AtomicReference<>();

	private final FastList<Future<?>> _futures = new FastList<>(10);

	ForkJoinContext() {
	}

	/**
	 * Set the fork-join-pool used by this context. This method doesn't replace
	 * an already set {@link ForkJoinPool}. Before the <i>context</i> can be
	 * used a {@link ForkJoinPool} must be set.
	 *
	 * @param pool the fork-join-pool to use.
	 * @return {@code true} if the given pool has been set, {@code false}
	 *          otherwise.
	 * @throws NullPointerException if the pool is {@code null}.
	 */
	public static boolean setForkJoinPool(final ForkJoinPool pool) {
		return _POOL.compareAndSet(null, requireNonNull(pool, "ForkJoinPool"));
	}

	/**
	 * Return the current fork-join-pool used by this context.
	 *
	 * @return the current fork-join-pool used by this context. Can be
	 *          {@code null} if not set jet.
	 */
	public static ForkJoinPool getForkJoinPool() {
		return _POOL.get();
	}

	@Override
	protected void enterAction() {
		if (_POOL.get() == null) {
			throw new IllegalStateException("No ForkJoinPool set.");
		}
		_futures.clear();
	}

	@Override
	protected void executeAction(final Runnable logic) {
		_futures.add(_POOL.get().submit(logic));
	}



	@Override
	protected void exitAction() {
		try {
			for (FastList.Node<Future<?>> n = _futures.head(),
				end = _futures.tail(); (n = n.getNext()) != end;)
			{
				n.getValue().get();
			}
		} catch (InterruptedException | ExecutionException e) {
			throw (CancellationException)new CancellationException().initCause(e);
		}
	}

	static {
		ObjectFactory.setInstance(
			new ObjectFactory<ForkJoinContext>() {
				@Override protected ForkJoinContext create() {
					return new ForkJoinContext();
				}
			},
			ForkJoinContext.class
		);
	}

}
