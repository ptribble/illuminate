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

import uk.co.petertribble.jumble.JumbleFile;
import java.io.File;
import java.util.Set;
import java.util.TreeSet;

/**
 * Describe a Tribblix overlay.
 *
 * @author Peter Tribble
 * @version 1.0
 */
public class Overlay implements Comparable<Overlay> {

    public static final String OVL_ROOT = "/var/sadm/overlays";

    private File ovrootf;

    private String altroot;
    private String name;
    private String description;
    private String oversion;
    private Set <Overlay> overlays;
    private Set <SVR4Package> packages;
    private Set <String> services;

    /**
     * Create an Overlay object. To be useful, you must call populate().
     *
     * @param altroot  An alternate root directory for this OS image
     * @param name  The name of this Overlay.
     */
    public Overlay(String altroot, String name) {
	this.altroot = altroot;
	ovrootf = new File(altroot + OVL_ROOT);
	this.name = name;
	packages = new TreeSet <SVR4Package> ();
	overlays = new TreeSet <Overlay> ();
	services = new TreeSet <String> ();
    }

    /**
     * Populate the data structures.
     */
    public void populate(PkgList plist, OverlayList ovlist) {
	parseOVL(ovlist);
	parsePKGS(plist);
    }

    private void parseOVL(OverlayList ovlist) {
	File f = new File(ovrootf, name + ".ovl");
	for (String line : JumbleFile.getLines(f)) {
	    String[] ds = line.split("=", 2);
	    if (ds[0].equals("VERSION")) {
		oversion = ds[1];
	    } else if (ds[0].equals("NAME")) {
		description = ds[1];
	    } else if (ds[0].equals("REQUIRES")) {
		// use existing overlay rather than a new one if possible
		Overlay ovl = ovlist.getOverlay(ds[1]);
		if (ovl == null) {
		    overlays.add(new Overlay(altroot, ds[1]));
		} else {
		    overlays.add(ovl);
		}
	    } else if (ds[0].equals("SERVICES")) {
		services.add(ds[1]);
	    }
	}
    }

    private void parsePKGS(PkgList plist) {
	File f = new File(ovrootf, name + ".pkgs");
	for (String line : JumbleFile.getLines(f)) {
	    SVR4Package pkg = plist.getPackage(line);
	    packages.add(pkg == null ? new SVR4Package(altroot, line) : pkg);
	}
    }

    public String getDescription() {
	return description;
    }

    public String toString() {
	return name;
    }

    public String getVersion() {
	return oversion;
    }

    public String getName() {
	return name;
    }

    /**
     * Returns all the overlays required by this Overlay.
     *
     * @return A Set of the overlays required by this Overlay.
     */
    public Set <Overlay> getOverlays() {
	return overlays;
    }

    /**
     * Returns all the services controlled by this Overlay.
     *
     * @return A Set of the services controlled by this Overlay.
     */
    public Set <String> getServices() {
	return services;
    }

    /**
     * Returns whether this overlay is installed.
     *
     * @return true if this overlay is installed, otherwise false.
     */
    public boolean isInstalled() {
	File f = new File(ovrootf, "installed");
	File f2 = new File(f, name);
	return f2.exists();
    }

    /**
     * Returns whether this overlay is complete, whether all its required
     * overlays and packages are installed.
     *
     * @return true if this overlay is complete, otherwise false.
     */
    public boolean isComplete() {
	for (Overlay ovl : overlays) {
	    if (!ovl.isInstalled()) {
		return false;
	    }
	}
	for (SVR4Package pkg : packages) {
	    if (!pkg.isInstalled()) {
		return false;
	    }
	}
	return true;
    }

    /**
     * Returns a Set of required overlays that are not installed
     */
    public Set <Overlay> missingOverlays() {
	Set <Overlay> omiss = new TreeSet <Overlay> ();
	for (Overlay ovl : overlays) {
	    if (!ovl.isInstalled()) {
		omiss.add(ovl);
	    }
	}
	return omiss;
    }

    /**
     * Returns a Set of member packages that are not installed
     */
    public Set <SVR4Package> missingPackages() {
	Set <SVR4Package> pmiss = new TreeSet <SVR4Package> ();
	for (SVR4Package pkg : packages) {
	    if (!pkg.isInstalled()) {
		pmiss.add(pkg);
	    }
	}
	return pmiss;
    }

    /**
     * Returns all the individual packages that are explicitly contained
     * in this Overlay. Does not recurse into required overlays.
     *
     * @return A Set of the packages explicitly contained
     * in this Overlay.
     */
    public Set <SVR4Package> getPackages() {
	return packages;
    }

    /**
     * Gets whether this overlay explicitly requires the specified
     * overlay.
     */
    public boolean containsOverlay(Overlay ovl) {
	return (overlays.contains(ovl)) ? true :
	    containsOverlay(ovl.getName());
    }

    /**
     * Gets whether this overlay explicitly requires the specified
     * overlay.
     */
    public boolean containsOverlay(String oname) {
	// check if any names match
	for (Overlay ovl : overlays) {
	    if (ovl.getName().equals(oname)) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Gets whether this overlay explicitly contains the specified
     * package.
     */
    public boolean containsPackage(SVR4Package p) {
	return (packages.contains(p)) ? true : containsPackage(p.getName());
    }

    /**
     * Gets whether this overlay explicitly contains the specified
     * package.
     */
    public boolean containsPackage(String pname) {
	// check if any names match
	for (SVR4Package pkg : packages) {
	    if (pkg.getName().equals(pname)) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Gets whether this overlay contains the specified package,
     * either explicitly or implicitly via requires.
     */
    public boolean includesPackage(SVR4Package p) {
	if (containsPackage(p)) {
	    return true;
	}
	// check required overlays
	for (Overlay ovl : overlays) {
	    if (ovl.includesPackage(p)) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Gets whether this overlay contains the specified package,
     * either explicitly or implicitly via requires.
     */
    public boolean includesPackage(String pname) {
	if (containsPackage(pname)) {
	    return true;
	}
	// check required overlays
	for (Overlay ovl : overlays) {
	    if (ovl.includesPackage(pname)) {
		return true;
	    }
	}
	return false;
    }

    /**
     * For Comparable.
     */
    public int compareTo(Overlay ovl) {
	return name.compareTo(ovl.getName());
    }
}
