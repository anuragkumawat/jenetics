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

import static java.lang.String.format;

import org.jenetics.util.Copyable;


/**
 * Abstraction for an ordered and bounded sequence of elements.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 1.4 &mdash; <em>$Date$</em>
 */
public abstract class ArrayProxy<T> implements Copyable<ArrayProxy<T>> {

	protected final int _start;
	protected final int _end;
	protected final int _length;

	protected ArrayProxy(final int start, final int end) {
		_start = start;
		_end = end;
		_length = _end - _start;
	}

	/**
	 * Return the <i>array</i> element at the specified position in the
	 * {@code ArrayProxy}.
	 *
	 * @param index index of the element to return
	 * @return the <i>array</i> element at the specified position
	 * @throws IndexOutOfBoundsException if the index it out of range
	 *         (index < 0 || index >= _length).
	 */
	public T get(final int index) {
		checkIndex(index);
		return uncheckedOffsetGet(index + _start);
	}

	/**
	 * Set the <i>array</i> element at the specified position in the
	 * {@code ArrayProxy}
	 *
	 * @param index the index of the element to set
	 * @param value the <i>array</i> element
	 * @throws IndexOutOfBoundsException if the index it out of range
	 *         (index < 0 || index >= _length).
	 */
	public void set(final int index, final T value) {
		checkIndex(index);
		uncheckedOffsetSet(index + _start, value);
	}

	/**
	 * Return the <i>array</i> element at the specified position in the
	 * {@code ArrayProxy}. The array boundaries are not checked.
	 *
	 * @param index index of the element to return
	 * @return the <i>array</i> element at the specified position
	 */
	public T uncheckedGet(final int index) {
		return uncheckedOffsetGet(index + _start);
	}

	/**
	 * Set the <i>array</i> element at the specified position in the
	 * {@code ArrayProxy}. The array boundaries are not checked.
	 *
	 * @param index index of the <i>array</i> element
	 */
	public void uncheckedSet(final int index, final T value) {
		uncheckedOffsetSet(index + _start, value);
	}

	/**
	 * Return the <i>array</i> element at the specified, absolute position in the
	 * {@code ArrayProxy}. The array boundaries are not checked.
	 *
	 * @param absoluteIndex absolute index of the element to return
	 * @return the <i>array</i> element at the specified absolute position
	 */
	public abstract T uncheckedOffsetGet(final int absoluteIndex);

	/**
	 * Set the <i>array</i> element at the specified absolute position in the
	 * {@code ArrayProxy}. The array boundaries are not checked.
	 *
	 * @param index absolute index of the <i>array</i> element
	 */
	public abstract void uncheckedOffsetSet(final int absoluteIndex, final T value);

	/**
	 * Return a new sub {@code ArrayProxy} object with the given start and end
	 * indexes. The underlying array storage is not copied. With the returned
	 * sub-array proxy it is possible to <i>write through</i> the original
	 * array.
	 *
	 * @param start the start index of the new sub {@code ArrayProxy} object,
	 *         inclusively.
	 * @param end the end index of the new sub {@code ArrayProxy} object,
	 *         exclusively.
	 * @return a new array proxy (view) with the given start and end index.
	 * @throws IndexOutOfBoundsException if the given indexes are out of bounds.
	 */
	public abstract ArrayProxy<T> sub(final int start, final int end);

	/**
	 * Return a new sub {@code ArrayProxy} object with the given start index.
	 * The underlying array storage is not copied. With the returned sub-array
	 * proxy it is possible to <i>write through</i> the original array.
	 *
	 * @param start the start index of the new sub {@code ArrayProxy} object,
	 *         inclusively.
	 * @return a new array proxy (view) with the given start index.
	 * @throws IndexOutOfBoundsException if the given indexes are out of bounds.
	 */
	public ArrayProxy<T> sub(final int start) {
		return sub(start, _length);
	}

	/**
	 * Swap a given range with a range of the same size with another array.
	 * Implementations of this class should replace this with a optimized
	 * version, depending on the underlying data structure.
	 *
	 * <pre>
	 *            start                end
	 *              |                   |
	 * this:  +---+---+---+---+---+---+---+---+---+---+---+---+
	 *              +---------------+
	 *                          +---------------+
	 * other: +---+---+---+---+---+---+---+---+---+---+---+---+
	 *                          |
	 *                      otherStart
	 * </pre>
	 *
	 * @param start the start index of {@code this} range, inclusively.
	 * @param end the end index of {@code this} range, exclusively.
	 * @param other the other array to swap the elements with.
	 * @param otherStart the start index of the {@code other} array.
	 * @throws IndexOutOfBoundsException if {@code start > end}.
	 * @throws IndexOutOfBoundsException if {@code start < 0 ||
	 *         end >= this.length() || otherStart < 0 ||
	 *         otherStart + (end - start) >= other.length()}
	 */
	public void swap(
		final int start,
		final int end,
		final ArrayProxy<T> other,
		final int otherStart
	) {
		checkIndex(start, end);
		other.checkIndex(otherStart, otherStart + (end - start));
		cloneIfSealed();
		other.cloneIfSealed();

		for (int i = (end - start); --i >= 0;) {
			final T temp = uncheckedGet(i + start);
			uncheckedSet(i + start, other.uncheckedGet(otherStart + i));
			other.uncheckedSet(otherStart + i, temp);
		}
	}

	/**
	 * Clone the underlying data structure of this {@code ArrayProxy} if it is
	 * sealed.
	 * <p/>
	 * The <i>default</i> implementation will look like this:
	 * [code]
	 *     public void cloneIfSealed() {
	 *         if (_sealed) {
	 *             _array = _array.clone();
	 *             _selaed = false;
	 *         }
	 *     }
	 * [/code]
	 */
	public abstract void cloneIfSealed();


	/**
	 * Set the seal flag for this {@code ArrayProxy} instance and return a new
	 * {@code ArrayProxy} object with an not set <i>seal</i> flag but with the
	 * same underlying data structure.
	 * <p/>
	 * The <i>default</i> implementation will look like this:
	 * [code]
	 * public MyArrayProxy<T> seal() {
	 *     _sealed = true;
	 *     return new MyArrayProxy(_array, _start, _end);
	 * }
	 * [code]
	 *
	 * @return a new {@code ArrayProxy} instance; for command chaining.
	 */
	public abstract ArrayProxy<T> seal();

	protected final void checkIndex(final int index) {
		if (index < 0 || index >= _length) {
			throw new ArrayIndexOutOfBoundsException(format(
				"Index %s is out of bounds [0, %s)", index, _length
			));
		}
	}

	protected final void checkIndex(final int from, final int to) {
		if (from > to) {
			throw new ArrayIndexOutOfBoundsException(format(
				"fromIndex(%d) > toIndex(%d)", from, to
			));
		}
		if (from < 0 || to > _length) {
			throw new ArrayIndexOutOfBoundsException(format(
				"Invalid index range: [%d, %s)", from, to
			));
		}
	}


}



