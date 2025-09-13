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

/**
 * Print out the installed sizes of all the packages.
 */
public final class PackageSizes {

    private PackageSizes() {
    }

    /**
     * Prints out the installed sizes of all packages.
     *
     * @param args Command line arguments
     */
    public static void main(final String[] args) {
	String altroot = "/";
	if (args.length == 2 && "-R".equals(args[0])) {
	    altroot = args[1];
	}
	PackageHandler pkghdl = new PackageHandler(altroot);
	ContentsParser cp = pkghdl.getContentsParser();
	for (String pkg :  pkghdl.getPkgList().getPackageNames()) {
	    ContentsPackage cc = cp.getPackage(pkg);
	    if (cc == null) {
		System.out.println("0 | 0 | " + pkg);
	    } else {
		System.out.println(cc.spaceUsed() + " | " + cc.numEntries()
				+ " | " + pkg);
	    }
	}
    }
}
