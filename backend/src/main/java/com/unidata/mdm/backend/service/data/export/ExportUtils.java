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

