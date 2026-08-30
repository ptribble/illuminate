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
 * Copyright 2026 Peter Tribble
 *
 */

package org.tribblix.illuminate.pkgview;

/**
 * Updates - produce a report of packages that need to be updated.
 */
public final class Updates {

    private Updates() {
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
	ZapConfig zc = pkghdl.getZapConfig();
	/*
	 * Go through installed packages and list those that need updating.
	 * The format here is identical to 'zap verify-packages'.
	 */
	for (SVR4Package pkg : pkghdl.getPkgList()) {
	    String cver = zc.currentVersion(pkg.getName());
	    if (!pkg.getVersion().equals(cver)) {
		System.out.println("WARN: package " + pkg.getName()
				   + " needs updating to " + cver);
	    }
	}
    }
}
