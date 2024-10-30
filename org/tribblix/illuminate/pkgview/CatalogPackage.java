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
