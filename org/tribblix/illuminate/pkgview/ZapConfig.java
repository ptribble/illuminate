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
import java.util.TreeMap;

/**
 * ZapConfig - describe zap configuration.
 * @author Peter Tribble
 * @version 1.0
 */
public class ZapConfig {

    private boolean zapexists;

    private Map<Integer, ZapRepository> repoMap;
    private Map<String, CatalogParser> catalogMap;

    /**
     * Create a zap configuration.
     *
     * @param pkghdl a PackageHandler for this OS image
     */
    public ZapConfig(PackageHandler pkghdl) {
	repoMap = new TreeMap<>();
	catalogMap = new HashMap<>(8);
	for (String line : pkghdl.listRepositories()) {
	    String[] ds = line.split(" ", 2);
	    repoMap.put(Integer.parseInt(ds[0]),
			new ZapRepository(pkghdl, ds[1]));
	    catalogMap.put(ds[1], new CatalogParser(pkghdl, ds[1]));
	}
	zapexists = !repoMap.isEmpty();
    }

    /**
     * Returns a Map of zap repositories, keyed by rank.
     *
     * @return a Map of zap repositories
     */
    public Map<Integer, ZapRepository> getRepos() {
	return repoMap;
    }

    /**
     * Returns a CatalogPackage for the available package, as listed
     * in the catalogs.
     *
     * @param name the name of the desired package
     * @return the CatalogPackage for the desired package, or null if the
     * package cannot be found
     */
    public CatalogPackage getPackage(String name) {
	for (ZapRepository zr : repoMap.values()) {
	    CatalogPackage cp = catalogMap.get(zr.getName()).getPackage(name);
	    if (cp != null) {
		return cp;
	    }
	}
	return null;
    }

    /**
     * Returns the currently available version of a package.
     *
     * @param name the name of the desired package
     * @return the currently available version of the desired package
     *
     */
    public String currentVersion(String name) {
	CatalogPackage cp = getPackage(name);
	return cp == null ? null : cp.getVersion();
    }

    /**
     * Return whether zap exists in the current system.
     *
     * @return true if the current system uses zap
     */
    public boolean exists() {
	return zapexists;
    }
}
