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
import java.util.Set;
import java.util.TreeSet;

/**
 * Abstracts an SVR4 package's depend file.
 *
 * @author Peter Tribble
 * @version 1.0
 */
public final class PkgDepend {

    private final String name;
    private final PackageHandler pkghdl;
    private Set<String> dependson;
    private Set<String> rdepends;
    private Set<String> incompatibles;

    /**
     * Create an SVR4 pkg dependency container.
     *
     * @param phdl a PackageHandler for this OS image
     * @param pname the name of the package.
     */
    public PkgDepend(final PackageHandler phdl, final String pname) {
	pkghdl = phdl;
	name = pname;
    }

    /**
     * Return the Set of package names that this package depends on.
     *
     * @return the Set of package names this package depends on
     */
    public Set<String> getDependencySet() {
	if (dependson == null) {
	    parseDepend();
	}
	return dependson;
    }

    /**
     * Return the Set of package names that this package declares to be
     * dependent on it.
     *
     * @return the Set of package names this package declares to be
     * dependent on it
     */
    public Set<String> getRDependencySet() {
	if (rdepends == null) {
	    parseDepend();
	}
	return rdepends;
    }

    /**
     * Return the Set of package names that this package is incompatible with.
     *
     * @return the Set of package names this package declares to be
     * incompatible with it
     */
    public Set<String> getIncompatibleSet() {
	if (incompatibles == null) {
	    parseDepend();
	}
	return incompatibles;
    }

    /*
     * Actually parse the depend file
     */
    private void parseDepend() {
	dependson = new TreeSet<>();
	rdepends = new TreeSet<>();
	incompatibles = new TreeSet<>();
	for (String s : pkghdl.getPkgDepend(name)) {
	    String[] ds = s.split("\\s+", 3);
	    // Must have at least 2 words
	    if (ds.length > 1) {
		if ("P".equals(ds[0])) {
		    dependson.add(ds[1]);
		} else if ("R".equals(ds[0])) {
		    rdepends.add(ds[1]);
		} else if ("I".equals(ds[0])) {
		    incompatibles.add(ds[1]);
		}
	    }
	}
    }
}
