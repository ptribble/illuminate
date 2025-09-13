/*
 * SPDX-License-Identifier: CDDL-1.0
 *
 * CDDL HEADER START
 *
 * This file and its contents are supplied under the terms of the
 * Common Development and Distribution License ("CDDL"), version 1.0.
 * You may only use this file in accordance with the terms of version
 * 1.0 of the CDDL.
 *
 * A full copy of the text of the CDDL should have accompanied this
 * source. A copy of the CDDL is also available via the Internet at
 * http://www.illumos.org/license/CDDL.
 *
 * CDDL HEADER END
 *
 * Copyright 2025 Peter Tribble
 *
 */

package org.tribblix.illuminate;

import java.util.Map;
import java.util.HashMap;

/**
 * SmfDependents - print the services that depend on this service, in a tree.
 * @author Peter Tribble
 * @version 1.0
 */
public final class SmfDependents {

    // save the dependency tree to avoid repeated calls
    private static final Map<String, String> DEPMAP = new HashMap<>();

    private SmfDependents() {
    }

    private static void printDependents(final String arg) {
	printDependents(arg, "");
    }

    private static void printDependents(final String arg, final String inset) {
	System.out.println(inset + arg);
	String sdep = DEPMAP.get(arg);
	if (sdep == null) {
	    SmfService ss = new SmfService(arg, "dummy");
	    sdep = ss.getDependents();
	    DEPMAP.put(arg, sdep);
	}
	for (String line : sdep.split("\n")) {
	    String[] ds = line.trim().split("\\s+", 3);
	    if (ds.length == 3) {
		printDependents(ds[2], inset + "  ");
	    }
	}
    }

    /**
     * Run the smfdependents command.
     *
     * @param args names of smf services you wish to display
     */
    public static void main(final String[] args) {
	for (String arg : args) {
	    printDependents(arg);
	}
    }
}
