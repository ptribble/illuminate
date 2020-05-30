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

import java.util.*;
import java.io.File;
import uk.co.petertribble.jumble.JumbleFile;
import uk.co.petertribble.jumble.JumbleUtils;

/**
 * Describe an SVR4 package.
 *
 * @author Peter Tribble
 * @version 2.0
 */
public class SVR4Package implements Comparable<SVR4Package> {

    public static final String PKG_ROOT = "/var/sadm/pkg";

    private File pkgrootf;

    private String name;
    private Map <String, String> infomap;
    private Set <String> dependson;
    private Set <String> rdepends;
    private Set <String> incompatibles;
    private Set <SVR4Package> depSet;

    /**
     * Create an SVR4 package container.
     *
     * @param altroot  An alternate root directory for this OS image
     * @param name  The name of the package.
     */
    public SVR4Package(String altroot, String name) {
	pkgrootf = new File(altroot + PKG_ROOT);
	this.name = name;
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

    /*
     * Actually parse the depend file
     */
    private void parseDepend() {
	dependson = new TreeSet <String> ();
	rdepends = new TreeSet <String> ();
	incompatibles = new TreeSet <String> ();
	for (String s : getDepend()) {
	    String[] ds = s.split("\\s+", 3);
	    // Must have at least 2 words
	    if ((ds.length > 1) && (ds[0].equals("P") || ds[0].equals("I") ||
						ds[0].equals("R"))) {
		if (ds[0].equals("P")) {
		    dependson.add(ds[1]);
		} else if (ds[0].equals("R")) {
		    rdepends.add(ds[1]);
		} else if (ds[0].equals("I")) {
		    incompatibles.add(ds[1]);
		}
	    }
	}
    }

    /**
     * Return the Set of package names that this package depends on.
     *
     * @return the Set of package names this package depends on
     */
    public Set <String> getDependencySet() {
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
    public Set <String> getRDependencySet() {
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
    public Set <String> getIncompatibleSet() {
	if (incompatibles == null) {
	    parseDepend();
	}
	return incompatibles;
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
	if (infomap == null) {
	    parseInfo();
	}
	// defensive copy, as PkgUtils mangles it
	return new HashMap <String, String> (infomap);
    }

    /*
     * Get the specified property from the pkginfo file.
     */
    private String getInfoItem(String s) {
	if (infomap == null) {
	    parseInfo();
	}
	return infomap.get(s);
    }

    /*
     * Parse the pkginfo file.
     */
    private void parseInfo() {
	infomap = JumbleUtils.stringToPropMap(getInfo(), "\n");
    }

    /**
     * Returns whether this package is installed, by seeing whether the
     * directory corresponding to its name exists.
     *
     * @return true if this package is installed
     */
    public boolean isInstalled() {
	return new File(pkgrootf, name).exists();
    }

    /*
     * Returns the pkginfo file associated with this package as a String.
     */
    private String getInfo() {
	return JumbleFile.getStringContents(
		new File(pkgrootf, name+"/pkginfo"));
    }

    /*
     * Returns the depend file associated with this package as a String array.
     */
    private String[] getDepend() {
	return JumbleFile.getLines(new File(pkgrootf, name+"/install/depend"));
    }

    /**
     * For Comparable.
     */
    public int compareTo(SVR4Package p) {
	return name.compareTo(p.getName());
    }
}
