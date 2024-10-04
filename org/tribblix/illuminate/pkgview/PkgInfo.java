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
    private Map <String, String> pkginfomap;

    /**
     * Create an SVR4 pkg pkginfo container.
     *
     * @param pkghdl a PackageHandler for this OS image
     * @param name  The name of the package.
     */
    public PkgInfo(PackageHandler pkghdl, String name) {
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
    public Map <String, String> infoMap() {
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
    public String getInfoItem(String s) {
	if (pkginfomap == null) {
	    parseInfo();
	}
	return pkginfomap.get(s);
    }

    /*
     * Parse the pkginfo file.
     */
    private void parseInfo() {
	pkginfomap = JumbleUtils.stringToPropMap(pkghdl.getPkgInfo(name), "\n");
    }
}
