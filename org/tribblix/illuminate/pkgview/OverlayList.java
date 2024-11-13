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

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

/**
 * OverlayList - shows available overlays in a JList.
 * @author Peter Tribble
 * @version 1.0
 */
public class OverlayList {

    private boolean ovlexists;

    private Set<Overlay> ovlist = new TreeSet<>();
    private Map<String, Overlay> ovMap = new HashMap<>();

    /**
     * Create an overlay list.
     *
     * There should be a .ovl file and a .pkgs file for each overlay
     *
     * @param pkghdl a PackageHandler for this OS image
     */
    public OverlayList(PackageHandler pkghdl) {
	PkgList plist = pkghdl.getPkgList();

	// first create a list of empty overlays
        for (String s : pkghdl.listOverlayNames()) {
	    Overlay ovl = new Overlay(pkghdl, s);
	    ovlist.add(ovl);
	    ovMap.put(s, ovl);
	}

	ovlexists = !ovlist.isEmpty();

	// then populate them
	for (Overlay ovl : ovlist) {
	    ovl.populate(plist, this);
	}
    }

    /**
     * Return whether overlays exist in the current system.
     *
     * @return true if the current system uses overlays
     */
    public boolean exists() {
	return ovlexists;
    }

    /**
     * Return a Set of the system's overlays.
     *
     * @return a Set of the system's overlays
     */
    public Set<Overlay> getOverlays() {
	return ovlist;
    }

    /**
     * Get the overlay of the given name.
     *
     * @param name the name of the Overlay to be retrieved
     *
     * @return the Overlay of the given name, or null if there is no such
     * overlay
     */
    public Overlay getOverlay(String name) {
	return ovMap.get(name);
    }

    /**
     * Return the overlay(s) that contain (require) the given overlay.
     *
     * @param ovl the overlay of interest
     *
     * @return the Set of overlays requiring the given overlay
     */
    public Set<Overlay> containingOverlays(Overlay ovl) {
	Set<Overlay> h = new TreeSet<>();
	for (Overlay ov1 : ovlist) {
	    if (ov1.containsOverlay(ovl)) {
		h.add(ov1);
	    }
	}
	return h;
    }

    /**
     * Return the overlay(s) that contain the given package.
     *
     * @param pkg the package of interest
     *
     * @return the Set of overlays containing the given package
     */
    public Set<Overlay> containingOverlays(SVR4Package pkg) {
	Set<Overlay> h = new TreeSet<>();
	for (Overlay ovl : ovlist) {
	    if (ovl.containsPackage(pkg.getName())) {
		h.add(ovl);
	    }
	}
	return h;
    }

    /**
     * Return the overlay(s) that contain the given packages.
     *
     * @param pkglist the list of packages of interest
     *
     * @return the Set of overlays containing the given packages
     */
    public Set<Overlay> containingOverlays(List<SVR4Package> pkglist) {
	Set<Overlay> h = new TreeSet<>();
	for (SVR4Package pkg : pkglist) {
	    h.addAll(containingOverlays(pkg));
	}
	return h;
    }
}
