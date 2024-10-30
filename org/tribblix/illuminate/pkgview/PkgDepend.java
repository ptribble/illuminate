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
 * Abstracts an SVR4 package's depend file.
 *
 * @author Peter Tribble
 * @version 1.0
 */
public class PkgDepend {

    private String name;
    private PackageHandler pkghdl;
    private Set<String> dependson;
    private Set<String> rdepends;
    private Set<String> incompatibles;

    /**
     * Create an SVR4 pkg dependency container.
     *
     * @param pkghdl a PackageHandler for this OS image
     * @param name  The name of the package.
     */
    public PkgDepend(PackageHandler pkghdl, String name) {
	this.pkghdl = pkghdl;
	this.name = name;
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
