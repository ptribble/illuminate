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
import java.util.Set;

/**
 * Describe an SVR4 package.
 *
 * @author Peter Tribble
 * @version 2.0
 */
public class SVR4Package implements Comparable<SVR4Package> {

    private PackageHandler pkghdl;
    private PkgInfo pkginfo;
    private PkgDepend pkgdepend;

    private String name;
    private Set <SVR4Package> depSet;

    /**
     * Create an SVR4 package container.
     *
     * @param pkghdl a PackageHandler for this OS image
     * @param name  The name of the package.
     */
    public SVR4Package(PackageHandler pkghdl, String name) {
	this.pkghdl = pkghdl;
	this.name = name;
	pkginfo = new PkgInfo(pkghdl, name);
	pkgdepend = new PkgDepend(pkghdl, name);
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
     * The name of this package.
     *
     * @return the name of this package
     */
    @Override
    public String toString() {
	return name;
    }

    /**
     * The long name, or description of this package. If the NAME field is
     * present, then that; else the DESC field or, if that is not
     * present, just returns the name of the package.
     *
     * @return the name or description of this package
     */
    public String getDescription() {
	String s = getInfoItem("NAME");
	if (s == null) {
	    s = getInfoItem("DESC");
	}
	return (s == null) ? name : s;
    }

    /**
     * The version of this package. If not available, return "-".
     *
     * @return the version of this package
     */
    public String getVersion() {
	String s = getInfoItem("VERSION");
	return (s == null) ? "-" : s;
    }

    /**
     * Return the Set of package names that this package depends on.
     *
     * @return the Set of package names this package depends on
     */
    public Set <String> getDependencySet() {
	return pkgdepend.getDependencySet();
    }

    /**
     * Return the Set of package names that this package declares to be
     * dependent on it.
     *
     * @return the Set of package names this package declares to be
     * dependent on it
     */
    public Set <String> getRDependencySet() {
	return pkgdepend.getRDependencySet();
    }

    /**
     * Return the Set of package names that this package is incompatible with.
     *
     * @return the Set of package names this package declares to be
     * incompatible with it
     */
    public Set <String> getIncompatibleSet() {
	return pkgdepend.getIncompatibleSet();
    }

    /**
     * Define the set of packages that are dependant on this package.
     *
     * @see #getDependantSet
     *
     * @param depSet the Set of dependant packages
     */
    public void setDependantSet(Set <SVR4Package> depSet) {
	this.depSet = depSet;
    }

    /**
     * Return the set of packages that are dependant on this package.
     *
     * @see #setDependantSet
     *
     * @return the Set of dependant packages
     */
    public Set <SVR4Package> getDependantSet() {
	return depSet;
    }

    /**
     * Parse the pkginfo file.
     *
     * @return the content of the pkginfo file as a Map
     */
    public Map <String, String> infoMap() {
	return pkginfo.infoMap();
    }

    /*
     * Get the specified property from the pkginfo file.
     */
    private String getInfoItem(String s) {
	return pkginfo.getInfoItem(s);
    }

    /**
     * Returns whether this package is installed, by seeing whether the
     * directory corresponding to its name exists.
     *
     * @return true if this package is installed
     */
    public boolean isInstalled() {
	return pkghdl.isPkgInstalled(name);
    }

    /**
     * For Comparable.
     */
    @Override
    public int compareTo(SVR4Package p) {
	return name.compareTo(p.getName());
    }
}
