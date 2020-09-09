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

package com.unidata.mdm.backend.service.classifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

public class CodeParser {

    private static final Set<Character> ALLOWED_CHARS = Sets.newHashSet('x', 'X', '0', '-', '+', '.', '/', '_', ' ');

    private static final Pattern GROUP_PATTERN = Pattern.compile("([xX]+0?)");

    public static Collection<Pair<Integer, InvalidSymbolReason>> validateCodePatternContent(final String codePattern) {
        if (StringUtils.isBlank(codePattern)) {
            throw new  IllegalArgumentException("Code pattern must be not empty");
        }

        final char[] chars = codePattern.toCharArray();
        final Set<Pair<Integer, InvalidSymbolReason>> invalidSymbolPositions = new TreeSet<>(
                Comparator.comparing(Pair::getLeft)
        );

        for (int i = 0; i < chars.length; i++) {
            final char symbol = chars[i];
            if (!ALLOWED_CHARS.contains(symbol)) {
                invalidSymbolPositions.add(Pair.of(i + 1, InvalidSymbolReason.UNKNOWN_SYMBOL));
            }
            else if (symbol == '0' &&
                    (i == 0 || i == chars.length - 1 || !isPlaceholder(chars[i - 1]) || isPlaceholder(chars[i + 1]))) {
                invalidSymbolPositions.add(Pair.of(i + 1, InvalidSymbolReason.WRONG_ZERO_FILLER_POSITION));
            }
            else if (i != 0
                            && (!isPlaceholder(symbol) && symbol != '0')
                            && (!isPlaceholder(chars[i - 1]) && chars[i - 1] != '0')
                            && ALLOWED_CHARS.contains(chars[i - 1])) {
                invalidSymbolPositions.add(Pair.of(i + 1, InvalidSymbolReason.MUST_BE_PLACEHOLDER));
            }
        }

        return invalidSymbolPositions;
    }


    public static Pattern regexpCodePatterBuilder(final String codePattern) {
        final Collection<Pair<Integer, InvalidSymbolReason>> validationResult = validateCodePatternContent(codePattern);
        if (validationResult.size() > 0) {
            throw new  IllegalArgumentException("Invalid code pattern " + codePattern + ", invalid data " + validationResult);
        }

        final char[] chars = codePattern.toCharArray();

        final StringBuilder regExp = new StringBuilder("^");

        int digitGroupSize = 0;

        boolean fistGroupAdded = false;

        boolean zeroPatternWasAdded = false;

        final List<String> endSymbols = new ArrayList<>();

        String nextZerosPattern = "";

        for (int i = 0; i < chars.length; i++) {
            if (isPlaceholder(chars[i])) {
                ++digitGroupSize;
            }
            else if (chars[i] == '0') {
                nextZerosPattern = "(?>(?>" + nextGroupZerosPattern(i, chars) + ")|";
                zeroPatternWasAdded = true;
            }
            else {
                if (i != 0) {
                    fistGroupAdded = true;
                    regExp.append("(\\d{").append(digitGroupSize).append("})").append(nextZerosPattern);
                    nextZerosPattern = "";
                    digitGroupSize = 0;
                }
                if (fistGroupAdded) {
                    regExp.append("(?>");
                    endSymbols.add(0, zeroPatternWasAdded ? "))" : ")?");
                    zeroPatternWasAdded = false;
                }
                regExp.append(groupSeparatorPattern(chars[i]));
            }
        }

        if (digitGroupSize > 0) {
            regExp.append("(\\d{").append(digitGroupSize).append("})");
        }

        regExp.append(endSymbols.stream().collect(Collectors.joining()));

        return Pattern.compile(regExp.append('$').toString());
    }

    private static String groupSeparatorPattern(char aChar) {
        if (aChar != '+' && aChar != ' ') {
            return "\\" + aChar;
        }
        if (aChar == ' ') {
            return "\\s";
        }
        return "";
    }

    private static String nextGroupZerosPattern(final int currentPosition, final char[] chars) {
        int currentSymbolPosition = currentPosition + 1;
        final StringBuilder groupZerosPattern = new StringBuilder(
                groupSeparatorPattern(chars[currentSymbolPosition++])
        );
        while (currentSymbolPosition < chars.length && isPlaceholder(chars[currentSymbolPosition++])) {
            groupZerosPattern.append(0);
        }
        --currentSymbolPosition;
        if (currentSymbolPosition < chars.length && chars[currentSymbolPosition] == '0') {
            groupZerosPattern.append(nextGroupZerosPattern(currentSymbolPosition, chars));
        }
        return groupZerosPattern.toString();
    }

    public static boolean isValidCodeForPattern(final String code, final String pattern) {
        return regexpCodePatterBuilder(pattern).matcher(code).matches();
    }

    public static String[] extractGroups(final String code, final String pattern) {
        final Matcher matcher = regexpCodePatterBuilder(pattern).matcher(code);
        if (!matcher.find()) {
            return new String[0];
        }
        final String[] groups = new String[matcher.groupCount()];
        for (int i = 1; i <= matcher.groupCount(); i++) {
            groups[i - 1] = matcher.group(i);
        }
        return Arrays.stream(groups).filter(Objects::nonNull).collect(Collectors.toList()).toArray(new String[0]);
    }

    private static boolean isPlaceholder(char ch) {
        return ch == 'X' || ch == 'x';
    }

    public static String toNodeId(final String code) {
        return StringUtils.removeAll(code, "[^\\d]");
    }

    public static List<String> extractParentIds(final String code, final String codePattern) {
        final String[] codes = CodeParser.extractGroups(code, codePattern);
        return IntStream.range(1, codes.length)
                .mapToObj(i -> generateParentId(codePattern, Arrays.copyOfRange(codes, 0, codes.length - i)))
                .collect(Collectors.toList());
    }

    public static String extractParentId(final String code, final String codePattern) {
        final String[] codes = CodeParser.extractGroups(code, codePattern);
        final String[] parent = Arrays.copyOfRange(codes, 0, codes.length - 1);

        return generateParentId(codePattern, parent);
    }

    public static int groupsCount(final String pattern) {
        final Matcher matcher = GROUP_PATTERN.matcher(pattern);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    private static String generateParentId(String codePattern, String[] codes) {
        final String parentId = Arrays.stream(codes).collect(Collectors.joining());

        return parentId + fillZerosForGroup(codes.length - 1, codePattern);
    }

    private static String fillZerosForGroup(final int groupIndex, final String codePattern) {
        final StringBuilder stringBuilder = new StringBuilder();
        int currentGroupIndex = groupIndex;
        final List<Pair<Integer, Boolean>> groupsInfo = groupsInfo(codePattern);
        do  {
            final Pair<Integer, Boolean> groupInfo = groupsInfo.get(currentGroupIndex);
            if (groupInfo.getRight()) {
                stringBuilder.append(StringUtils.repeat('0', groupsInfo.get(currentGroupIndex + 1).getLeft()));
            }
            else {
                break;
            }
        }
        while (currentGroupIndex++ < groupsInfo.size());
        return stringBuilder.toString();
    }

    private static List<Pair<Integer, Boolean>> groupsInfo(final String pattern) {
        final Matcher matcher = GROUP_PATTERN.matcher(pattern);
        List<Pair<Integer, Boolean>> groupsLengths = new ArrayList<>();
        while (matcher.find()) {
            final String group = matcher.group(1);
            final boolean isZerosFillerGroup = group.endsWith("0");
            groupsLengths.add(
                    Pair.of(group.length() - (isZerosFillerGroup ? 1 : 0), isZerosFillerGroup)
            );
        }
        return groupsLengths;
    }
}
