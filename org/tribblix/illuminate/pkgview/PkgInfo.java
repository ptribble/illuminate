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

import java.util.Map;
import java.util.HashMap;
import uk.co.petertribble.jumble.JumbleUtils;

/**
 * Abstracts an SVR4 package's pkginfo file.
 *
 * @author Peter Tribble
 * @version 1.0
 */
public class PkgInfo {

    private String name;
    private PackageHandler pkghdl;
    private Map<String, String> pkginfomap;

    /**
     * Create an SVR4 pkg pkginfo container.
     *
     * @param pkghdl a PackageHandler for this OS image
     * @param name the name of the package
     */
    public PkgInfo(final PackageHandler pkghdl, final String name) {
	this.pkghdl = pkghdl;
	this.name = name;
    }

    /**
     * Get the whole of the pkginfo file as a key-value Map.
     * Returns a copy so that a consumer is free to modify the
     * Map as it sees fit.
     *
     * @return the content of the pkginfo file as a Map
     */
    public Map<String, String> infoMap() {
	if (pkginfomap == null) {
	    parseInfo();
	}
	// make defensive copy, as PkgUtils modifies it
	return new HashMap<>(pkginfomap);
    }

    /**
     * Get the specified property from the pkginfo file.
     *
     * @param s the name of the entry of interest
     *
     * @return the value of the desired entry
     */
    public String getInfoItem(final String s) {
	if (pkginfomap == null) {
	    parseInfo();
	}
	return pkginfomap.get(s);
    }

    /*
     * Parse the pkginfo file.
     */
    private void parseInfo() {
	pkginfomap = JumbleUtils.stringToPropMap(pkghdl.getPkgInfo(name));
    }
}
