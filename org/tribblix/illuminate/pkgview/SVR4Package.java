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
    private Set<SVR4Package> depSet;

    /**
     * Create an SVR4 package container.
     *
     * @param pkghdl a PackageHandler for this OS image
     * @param name the name of the package
     */
    public SVR4Package(final PackageHandler pkghdl, final String name) {
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
    public Set<String> getDependencySet() {
	return pkgdepend.getDependencySet();
    }

    /**
     * Return the Set of package names that this package declares to be
     * dependent on it.
     *
     * @return the Set of package names this package declares to be
     * dependent on it
     */
    public Set<String> getRDependencySet() {
	return pkgdepend.getRDependencySet();
    }

    /**
     * Return the Set of package names that this package is incompatible with.
     *
     * @return the Set of package names this package declares to be
     * incompatible with it
     */
    public Set<String> getIncompatibleSet() {
	return pkgdepend.getIncompatibleSet();
    }

    /**
     * Define the set of packages that are dependant on this package.
     *
     * @see #getDependantSet
     *
     * @param depSet the Set of dependant packages
     */
    public void setDependantSet(final Set<SVR4Package> depSet) {
	this.depSet = depSet;
    }

    /**
     * Return the set of packages that are dependant on this package.
     *
     * @see #setDependantSet
     *
     * @return the Set of dependant packages
     */
    public Set<SVR4Package> getDependantSet() {
	return depSet;
    }

    /**
     * Parse the pkginfo file.
     *
     * @return the content of the pkginfo file as a Map
     */
    public Map<String, String> infoMap() {
	return pkginfo.infoMap();
    }

    /*
     * Get the specified property from the pkginfo file.
     */
    private String getInfoItem(final String s) {
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
    public int compareTo(final SVR4Package p) {
	return name.compareTo(p.getName());
    }

    /**
     * For Comparable.
     *
     * @param o the object to check for equality with this SVR4Package
     *
     * @return whether the given object is equal to this SVR4Package
     */
    @Override
    public boolean equals(final Object o) {
	if (o instanceof SVR4Package) {
	    SVR4Package p = (SVR4Package) o;
	    return name.equals(p.getName());
        }
        return false;
    }

    /**
     * For Comparable. As the unique property of an SVR4Package is its
     * name, use the hashCode of the underlying name.
     *
     * @return a unique hashcode for this SVR4Package
     */
    @Override
    public int hashCode() {
	return name.hashCode();
    }
}
