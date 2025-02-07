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

import java.awt.BorderLayout;
import java.awt.Cursor;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import uk.co.petertribble.jingle.JingleTextPane;

/**
 * Show information about installed files or packages in a set of
 * tabbed panels.
 *
 * @author Peter Tribble
 * @version 2.0
 */
public class PackageInformationPanel extends JTabbedPane {

    private static final long serialVersionUID = 1L;

    private transient PackageHandler pkghdl;

    private PackageTextPane infoPane;
    private PackageTextPane dependPane;
    private PackageTextPane revdepPane;
    private PackageTextPane overlayPane;
    private JScrollPane overlayScrollPane;
    private JingleTextPane filehPane;
    private JingleTextPane flistPane;
    private boolean showfiles;
    private transient OverlayList ovlist;
    private transient ContentsParser cp;
    private transient ZapConfig zc;

    /**
     * Create a default PackageInformationPanel showing the default tabs.
     *
     * @param pkghdl a PackageHandler for this OS image
     */
    public PackageInformationPanel(PackageHandler pkghdl) {
	this(pkghdl, true);
    }

    /**
     * Create a PackageInformationPanel showing the specified tabs.
     *
     * @param pkghdl a PackageHandler for this OS image
     * @param showdependencies a boolean determining if dependencies are shown
     */
    public PackageInformationPanel(PackageHandler pkghdl,
				boolean showdependencies) {
	this.pkghdl = pkghdl;
	ovlist = pkghdl.getOverlayList();
	zc = pkghdl.getZapConfig();

	infoPane = new PackageTextPane();
	dependPane = new PackageTextPane();
	revdepPane = new PackageTextPane();
	overlayPane = new PackageTextPane();
	filehPane = new JingleTextPane();
	flistPane = new JingleTextPane("text/plain");
	add(PkgResources.getString("PKG.INFO"), new JScrollPane(infoPane));
	if (showdependencies) {
	    add(PkgResources.getString("PKG.DEPENDENCIES"),
		new JScrollPane(dependPane));
	}
	if (ovlist.exists()) {
	    overlayScrollPane = new JScrollPane(overlayPane);
	    add(PkgResources.getString("PKG.OVERLAYS"), overlayScrollPane);
	}
    }

    /**
     * Show information on the requested package.
     *
     * @param pkg the package to be displayed
     */
    public void showPkg(SVR4Package pkg) {
	setOvlTab(PkgResources.getString("PKG.OVERLAYS"));
	if (pkg.isInstalled()) {
	    setInfoText(PkgUtils.infoTable(pkg, zc),
		    PkgUtils.dependencyTable(pkg),
		    PkgUtils.revDeps(pkg.getDependantSet()));
	} else {
	    setInfoText("Not installed", "", "");
	}
	setOverlayText(PkgUtils.overlayMembership(pkg, ovlist));
	if (cp != null) {
	    Cursor c = getCursor();
	    ContentsPackage cpp = cp.getPackage(pkg.getName());
	    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    setFilesText(PkgUtils.detailTable(cpp),
			 PkgUtils.doTextFileList(cpp));
	    setCursor(c);
	}
    }

    /**
     * Show information on the requested overlay.
     *
     * @param ovl the overlay to be displayed
     */
    public void showOverlay(Overlay ovl) {
	setOvlTab(PkgResources.getString("PKG.PACKAGES"));
	setInfoText(PkgUtils.infoTable(ovl),
		    PkgUtils.dependencyTable(ovl),
		    PkgUtils.ovlDeps(ovlist.containingOverlays(ovl)));
	setOverlayText(PkgUtils.overlayMembers(ovl));
	disableFilesTab();
    }

    /**
     * Show information on the requested filename.
     *
     * @param fname the name of the file to be displayed
     */
    public void showFile(String fname) {
	setOvlTab(PkgResources.getString("PKG.OVERLAYS"));
	if (cp == null) {
	    infoOnly("Package information not available.");
	} else {
	    // need to remove any altroot from the filename before
	    // matching it against the contents file
	    ContentsFileDetail cfd = cp.getFileDetail(fname
					.replaceFirst(pkghdl.getRoot(), "/")
					.replaceFirst("//", "/"));
	    if (cfd == null) {
		infoOnly("Not a member of any package.");
	    } else {
		setInfoText(PkgUtils.fileDetailTable(cfd), "", "");
		setOverlayText(PkgUtils.overlayMembership(ovlist, cfd));
	    }
	}
    }

    /*
     * The following methods update the text in the tabs. There are 5 possible
     * tabs - info, dep, rdep, ovl, files
     */

    private void infoOnly(String s) {
	setInfoText(s, "", "");
	setOverlayText("");
    }

    private void setInfoText(String si, String sd, String srd) {
	infoPane.setText(si);
	dependPane.setText(sd);
	revdepPane.setText(srd);
    }

    private void setOverlayText(String s) {
	overlayPane.setText(s);
    }

    private void setFilesText(String shead, String slist) {
	if (!showfiles) {
	    showFilesTab();
	}
	enableFiles();
	filehPane.setText(shead);
	flistPane.setText(slist);
    }

    /**
     * Show the reverse dependency tab.
     *
     * @param pkg the package to show
     */
    public void showRevDependencies(SVR4Package pkg) {
	add(PkgResources.getString("PKG.DEPENDANTS"),
	    new JScrollPane(revdepPane));
	if (pkg != null) {
	    showPkg(pkg);
	}
    }

    /*
     * Show the files tab.
     */
    private void showFilesTab() {
	JPanel jfp = new JPanel(new BorderLayout());
	jfp.add(filehPane, BorderLayout.PAGE_START);
	jfp.add(flistPane, BorderLayout.CENTER);
	add(PkgResources.getString("PKG.CONTENTS"), new JScrollPane(jfp));
	showfiles = true;
    }

    /*
     * Disable the files tab
     */
    private void disableFilesTab() {
	int i = indexOfTab(PkgResources.getString("PKG.CONTENTS"));
	if (i >= 0) {
	    if (isEnabledAt(i)) {
		setSelectedIndex(0);
	    }
	    setEnabledAt(i, false);
	}
    }

    /*
     * Enable the files tab
     */
    private void enableFiles() {
	int i = indexOfTab(PkgResources.getString("PKG.CONTENTS"));
	if (i >= 0) {
	    setEnabledAt(i, true);
	}
    }

    /*
     * Change the title of the overlays tab: if displaying a package, that tab
     * displays overlays, if displaying an overlay, then the tab shows packages
     */
    private void setOvlTab(String s) {
	int i = indexOfComponent(overlayScrollPane);
	if (i >= 0) {
	    setTitleAt(i, s);
	}
    }

    /**
     * Cause the detailed view of package contents to be shown.
     */
    public void showDetailedView() {
	cp = pkghdl.getContentsParser();
    }
}
