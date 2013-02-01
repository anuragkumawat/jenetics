/*
 * Java Genetic Algorithm Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Author:
 *     Franz Wilhelmstötter (franz.wilhelmstoetter@gmx.at)
 *
 */
package org.jenetics.util;

import static org.jenetics.util.object.nonNull;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.0
 * @version 1.0 &mdash; <em>$Date$</em>
 */
abstract class ArraySeq<T> implements Seq<T>, Serializable {
	private static final long serialVersionUID = 1L;

	transient ArrayRef _array;
	transient int _start;
	transient int _end;
	transient int _length;

	/**
	 * <i>Universal</i> array constructor.
	 *
	 * @param array the array which holds the elements. The array will not be
	 *         copied.
	 * @param start the start index of the given array (exclusively).
	 * @param end the end index of the given array (exclusively)
	 * @throws NullPointerException if the given {@code array} is {@code null}.
	 * @throws IndexOutOfBoundsException for an illegal start/end point index
	 *          value ({@code start < 0 || end > array.lenght || start > end}).
	 */
	ArraySeq(final ArrayRef array, final int start, final int end) {
		nonNull(array, "Array");
		if (start < 0 || end > array.length || start > end) {
			throw new ArrayIndexOutOfBoundsException(String.format(
				"Invalid index range: [%d, %s)", start, end
			));
		}
		_array = array;
		_start = start;
		_end = end;
		_length = _end - _start;
	}

	ArraySeq(final int length) {
		this(new ArrayRef(length), 0, length);
	}

	@Override
	@SuppressWarnings("unchecked")
	public T get(final int index) {
		checkIndex(index);
		return (T)_array.data[index + _start];
	}

	@Override
	public int indexOf(final Object element) {
		int index = -1;

		if (element == null) {
			index = indexWhere(new Function<T, Boolean>() {
				@Override public Boolean apply(final T object) {
					return object == null ? Boolean.TRUE : Boolean.FALSE;
				}
			});
		} else {
			index = indexWhere(new Function<T, Boolean>() {
				@Override public Boolean apply(final T object) {
					return element.equals(object) ? Boolean.TRUE : Boolean.FALSE;
				}
			});
		}

		return index;
	}

	@Override
	public int lastIndexOf(final Object element) {
		int index = -1;

		if (element == null) {
			index = lastIndexWhere(new Function<T, Boolean>() {
				@Override public Boolean apply(final T object) {
					return object == null ? Boolean.TRUE : Boolean.FALSE;
				}
			});
		} else {
			index = lastIndexWhere(new Function<T, Boolean>() {
				@Override public Boolean apply(final T object) {
					return element.equals(object) ? Boolean.TRUE : Boolean.FALSE;
				}
			});
		}

		return index;
	}

	@Override
	public int indexWhere(final Function<? super T, Boolean> predicate) {
		nonNull(predicate, "Predicate");

		int index = -1;

		for (int i = _start; i < _end && index == -1; ++i) {
			@SuppressWarnings("unchecked")
			final T element = (T)_array.data[i];

			if (predicate.apply(element) == Boolean.TRUE) {
				index = i - _start;
			}
		}

		return index;
	}

	@Override
	public <R> void foreach(final Function<? super T, ? extends R> function) {
		nonNull(function, "Function");

		for (int i = _start; i < _end; ++i) {
			@SuppressWarnings("unchecked")
			final T element = (T)_array.data[i];
			function.apply(element);
		}
	}

	@Override
	public boolean forall(final Function<? super T, Boolean> predicate) {
		nonNull(predicate, "Predicate");

		boolean valid = true;
		for (int i = _start; i < _end && valid; ++i) {
			@SuppressWarnings("unchecked")
			final T element = (T)_array.data[i];
			valid = predicate.apply(element).booleanValue();
		}
		return valid;
	}

	/*
	<B> B foldLeft(final B z, final Function2<? super B, ? super T, ? extends B> op) {
		B result = z;
		for (int i = 0, n = length(); i < n; ++i) {
			@SuppressWarnings("unchecked")
			final T value = (T)_array.data[i + _start];
			result = op.apply(result, value);
		}
		return z;
	}

	<B> B foldRight(final B z, final Function2<? super T, ? super B, ? extends B> op) {
		B result = z;
		for (int i = length(); --i >= 0;) {
			@SuppressWarnings("unchecked")
			final T value = (T)_array.data[i + _start];
			result = op.apply(value, result);
		}
		return z;
	}

	interface Function2<T1, T2, R> {
		R apply(T1 t1, T2 t2);
	}
	*/

	@Override
	public int lastIndexWhere(final Function<? super T, Boolean> predicate) {
		nonNull(predicate, "Predicate");

		int index = -1;

		for (int i = _end - 1; i >= _start && index == -1; --i) {
			@SuppressWarnings("unchecked")
			final T element = (T)_array.data[i];
			if (predicate.apply(element) == Boolean.TRUE) {
				index = i - _start;
			}
		}

		return index;
	}

	@Override
	public boolean contains(final Object element) {
		return indexOf(element) != -1;
	}

	@Override
	public int length() {
		return _length;
	}

	@Override
	public Iterator<T> iterator() {
		return new ArraySeqIterator<>(this);
	}

	@Override
	public <B> Iterator<B> iterator(
		final Function<? super T, ? extends B> converter
	) {
		nonNull(converter, "Converter");

		return new Iterator<B>() {
			private final Iterator<T> _iterator = iterator();
			@Override public boolean hasNext() {
				return _iterator.hasNext();
			}
			@Override public B next() {
				return converter.apply(_iterator.next());
			}
			@Override public void remove() {
				_iterator.remove();
			}
		};
	}

	@Override
	public Object[] toArray() {
		Object[] array = null;
		if (length() == _array.data.length) {
			array = _array.data.clone();
		} else {
			array = new Object[length()];
			System.arraycopy(_array.data, _start, array, 0, length());
		}

		return array;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T[] toArray(final T[] array) {
		T[] result = null;
		if (array.length < length()) {
			result = (T[])Arrays.copyOfRange(_array.data, _start, _end, array.getClass());
		} else {
			System.arraycopy(_array.data, _start, array, 0, length());
			if (array.length > length()) {
				array[length()] = null;
			}
			result = array;
		}

		return result;
	}

	@Override
	public List<T> asList() {
		return new ArraySeqList<>(this);
	}

	final void checkIndex(final int index) {
		if (index < 0 || index >= _length) {
			throw new ArrayIndexOutOfBoundsException(String.format(
				"Index %s is out of bounds [0, %s)", index, (_end - _start)
			));
		}
	}

	final void checkIndex(final int from, final int to) {
		if (from > to) {
			throw new IllegalArgumentException(
				"fromIndex(" + from + ") > toIndex(" + to+ ")"
			);
		}
		if (from < 0 || to > _length) {
			throw new ArrayIndexOutOfBoundsException(String.format(
				"Invalid index range: [%d, %s)", from, to
			));
		}
	}

	@Override
	public int hashCode() {
		return arrays.hashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return arrays.equals(this, obj);
	}

	@Override
	public String toString(
		final String prefix,
		final String separator,
		final String suffix
	) {
		  final StringBuilder out = new StringBuilder();

		  out.append(prefix);
		  if (length() > 0) {
			out.append(_array.data[_start]);
		  }
		  for (int i = _start + 1; i < _end; ++i) {
			out.append(separator);
			out.append(_array.data[i]);
		  }
		  out.append(suffix);

		  return out.toString();
	}

	@Override
	public String toString(final String separator) {
		return toString("", separator, "");
	}

	@Override
	public String toString() {
		  return toString("[", ",", "]");
	}

	private void writeObject(final ObjectOutputStream out)
		throws IOException
	{
		out.defaultWriteObject();

		out.writeInt(length());
		for (int i = _start; i < _end; ++i) {
			out.writeObject(_array.data[i]);
		}
	}

	private void readObject(final ObjectInputStream in)
		throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();

		_length = in.readInt();
		_array = new ArrayRef(_length);
		_start = 0;
		_end = _length;
		for (int i = 0; i < _length; ++i) {
			_array.data[i] = in.readObject();
		}
	}

}
