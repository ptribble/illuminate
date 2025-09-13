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

package org.tribblix.illuminate.pkgview;

import java.util.Set;
import java.util.TreeSet;

/**
 * MissingPackages - produce a report of packages that are claimed as being
 * required by other packages but aren't installed.
 */
public final class MissingPackages {

    private MissingPackages() {
    }

    /**
     * Run the application.
     *
     * @param args Command line arguments
     */
    public static void main(final String[] args) {
	String altroot = "/";
	if (args.length == 2 && "-R".equals(args[0])) {
	    altroot = args[1];
	}
	PackageHandler pkghdl = new PackageHandler(altroot);
	PkgList plist = pkghdl.getPkgList();
	/*
	 * Go through installed packages and create a complete list
	 * of their dependencies.
	 */
	Set<String> deps = new TreeSet<>();
	for (SVR4Package pkg : plist) {
	    deps.addAll(pkg.getDependencySet());
	}
	/*
	 * Remove the installed packages from the list, this should give
	 * the set of unsatisfied dependencies.
	 */
	for (SVR4Package pkg : plist) {
	    deps.remove(pkg.getName());
	}
	/*
	 * Map the reverse dependencies.
	 */
	plist.createRevDependencies();

	for (String s : deps) {
	    System.out.print("missing package " + s);
	    System.out.println(" needed by " + plist.getDependantSet(s));
	}
    }
}
