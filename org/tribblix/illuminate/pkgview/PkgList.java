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
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;

/**
 * PkgList - list installed SVR4 packages.
 *
 * @author Peter Tribble
 * @version 2.0
 */
public class PkgList extends TreeSet <SVR4Package> {

    private static final long serialVersionUID = 1L;

    private Map <String, SVR4Package> pkgMap = new HashMap<>();
    private Map <String, Set <SVR4Package>> revDependencies;

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
    public Set <String> getPackageNames() {
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

    public Set <SVR4Package> getDependantSet(String pkg) {
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
		Set <SVR4Package> revSet = revDependencies.get(pkgdep);
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
