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
package org.jenetics.gradle;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.regex.Pattern.quote;


/**
 * Represent a library version.
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmx.at">Franz Wilhelmstötter</a>
 * @since 1.4
 * @version 1.4 &mdash; <em>$Date: 2014-02-15 $</em>
 */
public final class Version implements Comparable<Version> {

	private final int _major;
	private final int _minor;
	private final int _micro;

	public Version(final int major, final int minor, final int micro) {
		// Check the version numbers.
		if ( major < 0 || minor < 0 || micro < 0 ) {
			throw new IllegalArgumentException(format(
				"Invalid range of the version numbers (%d, %d, %d)",
				major, minor, micro
			));
		}

		_major = major;
		_minor = minor;
		_micro = micro;
	}

	public Version(final int major, final int minor) {
		this(major, minor, 0);
	}

	public Version(final int major) {
		this(major, 0, 0);
	}

	public int getMajor() {
		return _major;
	}

	public int getMinor() {
		return _minor;
	}

	public int getMicro() {
		return _micro;
	}

	@Override
	public int compareTo(final Version version) {
		int comp = 0;

		if (_major > version._major) {
			comp = 1;
		} else if (_major < version._major) {
			comp = -1;
		}
		if (comp == 0) {
			if (_minor > version._minor) {
				comp = 1;
			} else if (_minor < version._minor) {
				comp = -1;
			}
		}
		if (comp == 0) {
			if (_micro > version._micro) {
				comp = 1;
			} else if (_micro < version._micro) {
				comp = -1;
			}
		}

		return comp;
	}

	@Override
	public int hashCode() {
		int hash = getClass().hashCode();
		hash = 31*hash + _major;
		hash = 31*hash + _minor;
		hash = 31*hash + _micro;
		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof Version)) {
			return false;
		}

		final Version version = (Version)obj;
		return _major == version._major &&
				_minor == version._minor &&
				_micro == version._micro;
	}

	@Override
	public String toString() {
		return format("%d.%d.%d", _major, _minor, _micro);
	}

	public static Version parse(final String versionString) {
		requireNonNull(versionString, "Version string must not be null.");
		final String[] parts = versionString.split(quote("."));

		Version version = null;
		try {
			if (parts.length == 1) {
				version = new Version(Integer.parseInt(parts[0]));
			} else if (parts.length == 2) {
				version = new Version(
					Integer.parseInt(parts[0]),
					Integer.parseInt(parts[1])
				);
			} else if (parts.length == 3) {
				version = new Version(
					Integer.parseInt(parts[0]),
					Integer.parseInt(parts[1]),
					Integer.parseInt(parts[2])
				);
			} else {
				throw new IllegalArgumentException(format(
					"'%s' is not a valid version string.", versionString
				));
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(format(
				"'%s' is not a valid version string.", versionString
			));
		}

		return version;
	}

}
