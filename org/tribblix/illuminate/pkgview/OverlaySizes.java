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
 * Print out the installed sizes of all the overlays.
 */
public final class OverlaySizes {

    private OverlaySizes() {
    }

    /**
     * Prints out the installed sizes of all overlays.
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
	for (Overlay ovl : pkghdl.getOverlayList().getOverlays()) {
	    ContentsPackage cc = cp.getOverlay(ovl);
	    System.out.println(cc.spaceUsed() + " | " + cc.numEntries()
			+ " | " + ovl.getName()
			+ " | " + ovl.getDescription());
	}
    }
}
