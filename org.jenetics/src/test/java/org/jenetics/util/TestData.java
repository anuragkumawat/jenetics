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

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Helper class for reading test data from file.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @version <em>$Date$</em>
 */
public class TestData implements Iterable<String[]> {

	private final String _resource;

	public TestData(final String resource) {
		_resource = resource;
	}

	@Override
	public Iterator<String[]> iterator() {
		return new Iterator<String[]>() {
			private final Reader _reader = new Reader(_resource);

			private String[] _data = _reader.read();

			@Override
			public boolean hasNext() {
				return _data != null;
			}

			@Override
			public String[] next() {
				final String[] current = _data;
				_data = _reader.read();
				if (_data == null) {
					_reader.close();
				}
				return current;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * Return a stream with the data lines.
	 *
	 * @return a stream with the data lines
	 */
	public Stream<String[]> stream() {
		return StreamSupport.stream(spliterator(), false);
	}

	public static TestData of(final String resource, final String... parameters) {
		final String param = Arrays.stream(parameters)
			.collect(Collectors.joining(",", "[", "]"));

		final String path =  resource + param + ".dat";
		return new TestData(path);
	}

	public static int[] toInt(final String[] line) {
		return Arrays.stream(line).mapToInt(Integer::parseInt).toArray();
	}

	public static int[] toInt(final double[] array) {
		final int[] result = new int[array.length];
		for (int i = 0; i < result.length; ++i) {
			result[i] = (int)array[i];
		}
		return result;
	}

	public static long[] toLong(final String[] line) {
		return Arrays.stream(line).mapToLong(Long::parseLong).toArray();
	}

	public static double[] toDouble(final String[] line) {
		return Arrays.stream(line).mapToDouble(Double::parseDouble).toArray();
	}

	private static final class Reader implements Closeable {
		private final BufferedReader _reader;

		Reader(final String resource) {
			_reader = new BufferedReader(new InputStreamReader(
				Reader.class.getResourceAsStream(resource)
			));
		}

		String[] read() {
			try {
				String line = null;
				while ((line = _reader.readLine()) != null &&
					(line.trim().startsWith("#") ||
						line.trim().isEmpty()))
				{
				}

				return line != null ? line.split(",") : null;
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}

		@Override
		public void close() {
			try {
				_reader.close();
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}
}
