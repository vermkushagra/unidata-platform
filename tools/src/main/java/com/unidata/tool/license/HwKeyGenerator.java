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

package com.unidata.tool.license;

import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Optional;

import com.javax0.license3j.licensor.HardwareBinder;

public final class HwKeyGenerator {

    private static final short VERSION = 1;

    private static final String HELP_FLAG = "-h";
    private static final String ARCHITECTURE_DISABLE_FLAG = "-a";
    private static final String HOST_NAME_DISABLE_FLAG = "-hn";
    private static final String NETWORK_DISABLE_FLAG = "-n";
    private static final String ALLOWED_INTERFACES_FLAG = "-ia";
    private static final String DISALLOWED_INTERFACES_FLAG = "-id";

    public static void main(final String[] args) throws UnsupportedEncodingException, SocketException, UnknownHostException {
        if (flagSet(HELP_FLAG, args)) {
            System.out.println(
                    "Unidata License Hardware Key Generator v" + VERSION +"\n"
                            + "Program arguments:\n"
                            + HELP_FLAG + " - help\n"
                            + ARCHITECTURE_DISABLE_FLAG + " - disable architecture in generation\n"
                            + HOST_NAME_DISABLE_FLAG + " - disable host name in generation\n"
                            + NETWORK_DISABLE_FLAG + " - disable network in generation\n"
                            + ALLOWED_INTERFACES_FLAG + "=<value> - regexp for allowed network interfaces, works only when network enabled in generation\n"
                            + DISALLOWED_INTERFACES_FLAG + "=<value> - regexp for disallowed network interfaces, works only when network enabled in generation\n"
            );
            return;
        }
        final HardwareBinder hardwareBinder = new HardwareBinder();
        if (flagSet(ARCHITECTURE_DISABLE_FLAG, args)) {
            hardwareBinder.ignoreArchitecture();
        }
        if (flagSet(HOST_NAME_DISABLE_FLAG, args)) {
            hardwareBinder.ignoreHostName();
        }
        if (flagSet(NETWORK_DISABLE_FLAG, args)) {
            hardwareBinder.ignoreNetwork();
        }
        else {
            flagValue(ALLOWED_INTERFACES_FLAG, args).ifPresent(hardwareBinder::interfaceAllowed);
            flagValue(DISALLOWED_INTERFACES_FLAG, args).ifPresent(hardwareBinder::interfaceDenied);
        }
        System.out.println(hardwareBinder.getMachineIdString());
    }

    private static boolean flagSet(final String flag, final String[] args) {
        return Arrays.stream(args).anyMatch(flag::equals);
    }

    private static Optional<String> flagValue(final String flag, final String[] args) {
        return Arrays.stream(args)
                .filter(arg -> arg.startsWith(flag) && arg.contains("="))
                .findFirst()
                .map(arg -> arg.split("=", 2)[1]);
    }
}
