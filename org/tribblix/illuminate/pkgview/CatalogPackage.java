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
 * Describe an SVR4 package in a catalog.
 *
 * @author Peter Tribble
 * @version 1.0
 */
public class CatalogPackage {

    private String name;
    private String pkgvers;
    private String dsize;
    private String repo;
    private String depends;

    /**
     * Create an SVR4 package catalog entry.
     *
     * @param name  The name of the package.
     * @param pkgvers  The version of the package.
     * @param depends  The name of the package.
     * @param dsize  The download size of the package, specifically the size
     * of the zap file it's distributed in.
     * @param repo  The name of the repository supplying this package.
     */
    public CatalogPackage(String name, String pkgvers, String depends,
			String dsize, String repo) {
	this.name = name;
	this.pkgvers = pkgvers;
	this.dsize = dsize;
	this.repo = repo;
	this.depends = depends;
    }

    /**
     * Return the name of this package.
     *
     * @return the name of this package
     */
    public String getName() {
	return name;
    }

    /**
     * The versioned name of this package.
     *
     * @return the name of this package
     */
    @Override
    public String toString() {
	return name + "@" + pkgvers;
    }

    /**
     * The available version of this package.
     *
     * @return the version of this package
     */
    public String getVersion() {
	return pkgvers;
    }

    /**
     * The download size of this package. This is the zipped size.
     *
     * @return the download size of this package
     */
    public String getDownloadSize() {
	return dsize;
    }

    /**
     * Return the Set of package names that this package depends on.
     *
     * @return the Set of package names this package depends on
     */
    public Set<String> getDependencySet() {
	Set<String> dependson = new TreeSet<>();
	for (String s : depends.split("\\s+")) {
	    dependson.add(s);
	}
	return dependson;
    }

    /**
     * Which repository this CatalogPackage comes from.
     *
     * @return the repository supplying this package
     */
    public String getRepo() {
	return repo;
    }
}
