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

import java.io.File;
import java.util.*;

/**
 * OverlayList - shows available overlays in a JList.
 * @author Peter Tribble
 * @version 1.0
 */
public class OverlayList {

    private boolean ovlexists;

    private Set <Overlay> ovlist;
    private Map <String, Overlay> ovMap;

    /**
     * Create an overlay list.
     *
     * There should be a .ovl file and a .pkgs file for each overlay
     *
     * @param altroot  An alternate root directory for this OS image
     */
    public OverlayList(String altroot, PkgList plist) {
	ovMap = new HashMap <String, Overlay> ();
	ovlist = new TreeSet <Overlay> ();

	File ovrootf = new File(altroot + Overlay.OVL_ROOT);
	ovlexists = ovrootf.exists();

	// first create a list of empty overlays
	for (File f : ovrootf.listFiles()) {
	    if (f.getName().endsWith(".ovl")) {
		String fname = f.getName();
		String rootname = fname.substring(0, fname.length()-4);
		File f2 = new File(ovrootf, rootname + ".pkgs");
		if (f2.exists()) {
		    Overlay ovl = new Overlay(altroot, rootname);
		    ovlist.add(ovl);
		    ovMap.put(rootname, ovl);
		}
	    }
	}

	// then populate them
	for (Overlay ovl : ovlist) {
	    ovl.populate(plist, this);
	}
    }

    /**
     * Return whether overlays exist in the current system.
     */
    public boolean exists() {
	return ovlexists;
    }

    public Set <Overlay> getOverlays() {
	return ovlist;
    }

    public Overlay getOverlay(String name) {
	return ovMap.get(name);
    }

    /**
     * Return the overlay(s) that contain (require) the given overlay.
     */
    public Set <Overlay> containingOverlays(Overlay ovl) {
	Set <Overlay> h = new TreeSet <Overlay> ();
	for (Overlay ov1 : ovlist) {
	    if (ov1.containsOverlay(ovl)) {
		h.add(ov1);
	    }
	}
	return h;
    }

    /**
     * Return the overlay(s) that contain the given package.
     */
    public Set <Overlay> containingOverlays(SVR4Package pkg) {
	Set <Overlay> h = new TreeSet <Overlay> ();
	for (Overlay ovl : ovlist) {
	    if (ovl.containsPackage(pkg)) {
		h.add(ovl);
	    }
	}
	return h;
    }

    /**
     * Return the overlay(s) that contain the given package.
     */
    public Set <Overlay> containingOverlays(
				List <SVR4Package> pkglist) {
	Set <Overlay> h = new TreeSet <Overlay> ();
	for (SVR4Package pkg : pkglist) {
	    h.addAll(containingOverlays(pkg));
	}
	return h;
    }
}
