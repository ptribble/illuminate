/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at usr/src/OPENSOLARIS.LICENSE
 * or http://www.opensolaris.org/os/licensing.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at usr/src/OPENSOLARIS.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
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
    private static final Map <String, String> DEPMAP = new HashMap<>();

    private SmfDependents() {
    }

    public static void printDependents(String arg) {
	printDependents(arg, "");
    }

    public static void printDependents(String arg, String inset) {
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

    public static void main(String[] args) {
	for (String arg : args) {
	    printDependents(arg);
	}
    }
}
