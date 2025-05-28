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

import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;

/**
 * Generates a view of the software installed on a system. The display
 * is broken into three tabs, which show a list of installed packages,
 * a tree view based on overlays, and (optionally) a view
 * based on the files in the filesystem.
 *
 * @author Peter Tribble
 * @version 2.0
 */
public final class InstalledSoftwarePanel extends JTabbedPane {

    private static final long serialVersionUID = 1L;

    transient PackageHandler pkghdl;

    PkgList plist;
    PackagePanel ipp;
    OverlayPanel ovp;
    InstalledFilesPanel ifp;

    /**
     * Create a panel to display installed software.
     *
     * @param altroot An alternate root directory for this OS image
     */
    public InstalledSoftwarePanel(String altroot) {
	pkghdl = new PackageHandler(altroot);
	plist = pkghdl.getPkgList();
	OverlayList ovlist = pkghdl.getOverlayList();

	ipp = new PackagePanel(pkghdl);
	add(PkgResources.getString("PKG.LIST"), ipp);

	ovp = new OverlayPanel(pkghdl);
	if (ovlist.exists()) {
	    add(PkgResources.getString("PKG.OVP"), ovp);
	}

	ifp = new InstalledFilesPanel(pkghdl);
	add(PkgResources.getString("PKG.FS"), ifp);

	new RevDependencyWorker().execute();
	new ContentsWorker().execute();
    }

    /*
     * Parse the contents file in the background. ContentsParser is
     * a singleton, so once we've done it here we can tell the other
     * views to use it.
     */
    class ContentsWorker extends SwingWorker<String, Object> {
	@Override
	public String doInBackground() {
	    pkghdl.getContentsParser();
	    return "done";
	}

	@Override
	protected void done() {
	    ipp.showDetailedView();
	    ifp.showDetailedView();
	    ovp.showDetailedView();
	}
    }

    /*
     * Generate reverse dependencies in the background, then tell the other
     * views to show them.
     */
    class RevDependencyWorker extends SwingWorker<String, Object> {
	@Override
	public String doInBackground() {
	    plist.createRevDependencies();
	    return "done";
	}

	@Override
	protected void done() {
	    ipp.showRevDependencies();
	    ovp.showRevDependencies();
	}
    }
}
