/*
 * Unidata Platform Community Edition
 * Copyright (c) 2013-2020, UNIDATA LLC, All rights reserved.
 * This file is part of the Unidata Platform Community Edition software.
 *
 * Unidata Platform Community Edition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Unidata Platform Community Edition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.unidata.mdm.backend.service.data.export;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Class ExportUtils.
 * @author ilya.bykov
 */
public class ExportUtils {
	/**
	 * Denormalize.
	 *
	 * @param <T>
	 *            the generic type
	 * @param containers
	 *            the containers
	 * @return the list
	 */
	public static <T> List<List<T>> denormalize(final List<List<T>> containers) {
		return combineInternal(0, containers);
	}

	/**
	 * Combine internal.
	 *
	 * @param <T>
	 *            the generic type
	 * @param currentIndex
	 *            the current index
	 * @param containers
	 *            the containers
	 * @return the list
	 */
	public static <T> List<List<T>> combineInternal(final int currentIndex, final List<List<T>> containers) {
		if (currentIndex == containers.size()) {
			// skip items for last container
			final List<List<T>> combinations = new ArrayList<List<T>>();
			combinations.add(Collections.emptyList());
			return combinations;
		}

		final List<List<T>> combinations = new ArrayList<List<T>>();
		final List<T> containerItemList = containers.get(currentIndex);
		// Get combination from next index
		final List<List<T>> suffixList = combineInternal(currentIndex + 1, containers);

		final int size = containerItemList.size();
		for (int i = 0; i < size; i++) {
			final T containerItem = containerItemList.get(i);
			if (suffixList != null) {
				for (final List<T> suffix : suffixList) {
					final List<T> nextCombination = new ArrayList<>();
					nextCombination.add(containerItem);
					nextCombination.addAll(suffix);
					combinations.add(nextCombination);
				}
			}
		}

		return combinations;
	}
}

