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
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * PkgList - list installed SVR4 packages.
 *
 * @author Peter Tribble
 * @version 2.0
 */
public class PkgList extends TreeSet<SVR4Package> {

    private static final long serialVersionUID = 1L;

    private Map<String, SVR4Package> pkgMap = new HashMap<>();
    private Map<String, Set<SVR4Package>> revDependencies;

    /**
     * Create a package list.
     *
     * Packages are in directories, so ignore files
     * and anything hidden (starting with a dot) and
     * also the locale directory
     *
     * @param pkghdl a PackageHandler for this OS image
     */
    public PkgList(PackageHandler pkghdl) {
        for (String s : pkghdl.listPackageNames()) {
	    SVR4Package sp = new SVR4Package(pkghdl, s);
	    add(sp);
	    pkgMap.put(s, sp);
	}
    }

    /**
     * Return a Set of installed package names.
     *
     * @return a Set of installed package names
     */
    public Set<String> getPackageNames() {
	return new TreeSet<>(pkgMap.keySet());
    }

    /**
     * Return the SVR4Package of the given name.
     *
     * @param name the name of the requested package
     *
     * @return the SVR4Package of the name, or null if no such package
     * is installed.
     */
    public SVR4Package getPackage(String name) {
	return pkgMap.get(name);
    }

    /**
     * Get the dependants of the given package.
     *
     * @param pkg the package to query
     *
     * @return a Set of packages that depend on the given package
     */
    public Set<SVR4Package> getDependantSet(String pkg) {
	return (revDependencies == null) ? null : revDependencies.get(pkg);
    }

    /**
     * Create a reverse dependency tree. Pull the dependencies out
     * and populate another Map.
     */
    public void createRevDependencies() {
	revDependencies = new HashMap<>();
	for (SVR4Package pkg : this) {
	    for (String pkgdep : pkg.getDependencySet()) {
		Set<SVR4Package> revSet = revDependencies.get(pkgdep);
		if (revSet == null) {
		    revSet = new HashSet<>();
		    revDependencies.put(pkgdep, revSet);
		}
		revSet.add(pkg);
	    }
	}
	/*
	 * Now we've built the tree, tell the packages what their
	 * dependants are.
	 */
	for (SVR4Package pkg : this) {
	    pkg.setDependantSet(revDependencies.get(pkg.getName()));
	}
    }
}
