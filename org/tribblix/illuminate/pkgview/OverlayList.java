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
public final class OverlayList {

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
    public OverlayList(final PackageHandler pkghdl) {
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
    public Overlay getOverlay(final String name) {
	return ovMap.get(name);
    }

    /**
     * Return the overlay(s) that contain (require) the given overlay.
     *
     * @param ovl the overlay of interest
     *
     * @return the Set of overlays requiring the given overlay
     */
    public Set<Overlay> containingOverlays(final Overlay ovl) {
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
    public Set<Overlay> containingOverlays(final SVR4Package pkg) {
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
    public Set<Overlay> containingOverlays(final List<SVR4Package> pkglist) {
	Set<Overlay> h = new TreeSet<>();
	for (SVR4Package pkg : pkglist) {
	    h.addAll(containingOverlays(pkg));
	}
	return h;
    }
}
