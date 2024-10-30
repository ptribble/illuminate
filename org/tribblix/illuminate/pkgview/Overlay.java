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
 * Describe a Tribblix overlay.
 *
 * @author Peter Tribble
 * @version 1.0
 */
public class Overlay implements Comparable<Overlay> {

    private PackageHandler pkghdl;

    private String name;
    private String description;
    private String oversion;
    private Set<Overlay> overlays = new TreeSet<>();
    private Set<SVR4Package> packages = new TreeSet<>();
    private Set<String> services = new TreeSet<>();

    /**
     * Create an Overlay object. To be useful, you must call populate().
     *
     * @param pkghdl a PackageHandler for this OS image
     * @param name  The name of this Overlay.
     */
    public Overlay(PackageHandler pkghdl, String name) {
	this.pkghdl = pkghdl;
	this.name = name;
    }

    /**
     * Populate the data structures.
     *
     * @param plist a PkgList object
     * @param ovlist an OverlayList object
     */
    public void populate(PkgList plist, OverlayList ovlist) {
	parseOVL(ovlist);
	parsePKGS(plist);
    }

    private void parseOVL(OverlayList ovlist) {
	for (String line : pkghdl.getOvlOvl(name)) {
	    String[] ds = line.split("=", 2);
	    if ("VERSION".equals(ds[0])) {
		oversion = ds[1];
	    } else if ("NAME".equals(ds[0])) {
		description = ds[1];
	    } else if ("REQUIRES".equals(ds[0])) {
		// use existing overlay rather than a new one if possible
		Overlay ovl = ovlist.getOverlay(ds[1]);
		if (ovl == null) {
		    overlays.add(new Overlay(pkghdl, ds[1]));
		} else {
		    overlays.add(ovl);
		}
	    } else if ("SERVICES".equals(ds[0])) {
		services.add(ds[1]);
	    }
	}
    }

    private void parsePKGS(PkgList plist) {
	for (String line : pkghdl.getOvlPkgs(name)) {
	    SVR4Package pkg = plist.getPackage(line);
	    packages.add(pkg == null ? new SVR4Package(pkghdl, line) : pkg);
	}
    }

    /**
     * Returns a description of this Overlay.
     *
     * @return a String description of this overlay
     */
    public String getDescription() {
	return description;
    }

    @Override
    public String toString() {
	return name;
    }

    /**
     * Returns the version of this Overlay.
     *
     * @return the version of this overlay as a String
     */
    public String getVersion() {
	return oversion;
    }

    /**
     * Returns the name of this Overlay.
     *
     * @return a name of this overlay as a String
     */
    public String getName() {
	return name;
    }

    /**
     * Returns all the overlays required by this Overlay.
     *
     * @return A Set of the overlays required by this Overlay.
     */
    public Set<Overlay> getOverlays() {
	return overlays;
    }

    /**
     * Returns all the services controlled by this Overlay.
     *
     * @return A Set of the services controlled by this Overlay.
     */
    public Set<String> getServices() {
	return services;
    }

    /**
     * Returns whether this overlay is installed.
     *
     * @return true if this overlay is installed, otherwise false.
     */
    public boolean isInstalled() {
	return pkghdl.isOvlInstalled(name);
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
     * Returns a Set of required overlays that are not installed.
     *
     * @return a Set of required overlays that are not installed
     */
    public Set<Overlay> missingOverlays() {
	Set<Overlay> omiss = new TreeSet<>();
	for (Overlay ovl : overlays) {
	    if (!ovl.isInstalled()) {
		omiss.add(ovl);
	    }
	}
	return omiss;
    }

    /**
     * Returns a Set of member packages that are not installed.
     *
     * @return a Set of the packages contained in this Overlay that are
     * not currently installed
     */
    public Set<SVR4Package> missingPackages() {
	Set<SVR4Package> pmiss = new TreeSet<>();
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
    public Set<SVR4Package> getPackages() {
	return packages;
    }

    /**
     * Gets whether this overlay explicitly requires the specified
     * overlay.
     *
     * @param ovl the overlay of interest
     *
     * @return true if the given overlay is required by this overlay
     */
    public boolean containsOverlay(Overlay ovl) {
	return overlays.contains(ovl) || containsOverlay(ovl.getName());
    }

    /**
     * Gets whether this overlay explicitly requires the specified
     * overlay.
     *
     * @param oname the name of the overlay of interest
     *
     * @return true if the given overlay is required by this overlay
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
     *
     * @param pname the name of the package of interest
     *
     * @return true if the given package is contained in this overlay
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
     * For Comparable.
     */
    @Override
    public int compareTo(Overlay ovl) {
	return name.compareTo(ovl.getName());
    }
}
