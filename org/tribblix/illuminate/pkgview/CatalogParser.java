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

import java.util.HashMap;
import java.util.Map;

/**
 * We parse the catalog file and create a map of catalog entries.
 */
public final class CatalogParser {

    private final Map<String, CatalogPackage> pkgMap;

    /**
     * Parse a package catalog.
     *
     * @param pkghdl a PackageHandler for this OS image
     * @param reponame the repository represented by this catalog
     */
    public CatalogParser(final PackageHandler pkghdl, final String reponame) {
	/*
	 * the release repo usually has 3 packages
	 * the illumos repo has just under 500
	 * the main repo currently has 1400
	 * size so that we wouldn't normally ever need to rehash
	 */
	int isize = 8;
	if ("illumos".equals(reponame)) {
	    isize = 640;
	} else if ("tribblix".equals(reponame)) {
	    isize = 2000;
	} else if ("oi".equals(reponame)) {
	    isize = 256;
	}
	pkgMap = new HashMap<>(isize);
	parse(pkghdl, reponame);
    }

    /*
     * The catalog has lines of the form
     * name|version|depends|size|checksum
     */
    private void parse(final PackageHandler pkghdl, final String reponame) {
	for (String s : pkghdl.getCatalog(reponame)) {
	    String[] ds = s.split("\\|", 5);
	    pkgMap.put(ds[0],
		new CatalogPackage(ds[0], ds[1], ds[2], ds[3], reponame));
	}
    }

    /**
     * Return the CatalogPackage for the requested package.
     *
     * @param name the name of the package of interest
     *
     * @return the CatalogPackage for the given package, or null if there
     * is no entry for the requested package.
     */
    public CatalogPackage getPackage(final String name) {
	return pkgMap.get(name);
    }
}
