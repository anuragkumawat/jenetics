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

import static java.lang.Math.min;

import java.util.Random;

import org.jenetics.internal.math.random;

/**
 * An abstract base class which eases the implementation of {@code Random}
 * objects which natively creates random {@code long} values. All other
 * {@code Random} functions are optimized using this {@code long} values.
 *
 * [code]
 * public class MyRandom64 extends Random64 {
 *     \@Override
 *     public long nextLong() {
 *         // Only this method must be implemented.
 *         ...
 *     }
 * }
 * [/code]
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.3
 * @version 1.3 &mdash; <em>$Date$</em>
 */
public abstract class Random64 extends PRNG {

	private static final long serialVersionUID = 1L;

	protected Random64(long seed) {
		super(seed);
	}

	protected Random64() {
		this(math.random.seed());
	}

	/**
	 * Force to explicitly override the Random.nextLong() method. All other
	 * methods of this class are implemented by calling this method.
	 */
	@Override
	public abstract long nextLong();


	@Override
	public boolean nextBoolean() {
		return (nextLong() & 0x8000000000000000L) != 0L;
	}

	@Override
	public int nextInt() {
		return (int)(nextLong() >>> 32);
	}

	@Override
	protected int next(final int bits) {
		return (int)(nextLong() >>> (64 - bits));
	}

	/**
	 * Optimized version of the {@link Random#nextBytes(byte[])} method for
	 * 64-bit random engines.
	 */
	@Override
	public void nextBytes(final byte[] bytes) {
		for (int i = 0, len = bytes.length; i < len;) {
			int n = min(len - i, Long.SIZE/Byte.SIZE);

			for (long x = nextLong(); --n >= 0; x >>= Byte.SIZE) {
				bytes[i++] = (byte)x;
			}
		}
	}

	@Override
	public float nextFloat() {
		return random.toFloat2(nextLong());
	}

	/**
	 * Optimized version of the {@link Random#nextDouble()} method for 64-bit
	 * random engines.
	 */
	@Override
	public double nextDouble() {
		return random.toDouble2(nextLong());
	}

}



