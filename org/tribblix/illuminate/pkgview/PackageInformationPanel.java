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

    private transient PackageHandler pkghdl;

    private PackageTextPane tp_info;
    private PackageTextPane tp_dep;
    private PackageTextPane tp_rdep;
    private PackageTextPane tp_ovl;
    private JScrollPane jp_ovl;
    private JingleTextPane tp_filehead;
    private JingleTextPane tp_filelist;
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

	tp_info = new PackageTextPane();
	tp_dep = new PackageTextPane();
	tp_rdep = new PackageTextPane();
	tp_ovl = new PackageTextPane();
	tp_filehead = new JingleTextPane();
	tp_filelist = new JingleTextPane("text/plain");
	add(PkgResources.getString("PKG.INFO"), new JScrollPane(tp_info));
	if (showdependencies) {
	    add(PkgResources.getString("PKG.DEPENDENCIES"),
		new JScrollPane(tp_dep));
	}
	if (ovlist.exists()) {
	    jp_ovl = new JScrollPane(tp_ovl);
	    add(PkgResources.getString("PKG.OVERLAYS"), jp_ovl);
	}
    }

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
	    setFilesText(PkgUtils.detailTable(cpp), PkgUtils.doTextFileList(cpp));
	    setCursor(c);
	}
    }

    public void showOverlay(Overlay ovl) {
	setOvlTab(PkgResources.getString("PKG.PACKAGES"));
	setInfoText(PkgUtils.infoTable(ovl),
		    PkgUtils.dependencyTable(ovl),
		    PkgUtils.ovlDeps(ovlist.containingOverlays(ovl)));
	setOverlayText(PkgUtils.overlayMembers(ovl));
	disableFilesTab();
    }

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
	tp_info.setText(si);
	tp_dep.setText(sd);
	tp_rdep.setText(srd);
    }

    private void setOverlayText(String s) {
	tp_ovl.setText(s);
    }

    private void setFilesText(String shead, String slist) {
	if (!showfiles) {
	    showFilesTab();
	}
	enableFiles();
	tp_filehead.setText(shead);
	tp_filelist.setText(slist);
    }

    /**
     * Show the reverse dependency tab.
     *
     * @param pkg the package to show
     */
    public void showRevDependencies(SVR4Package pkg) {
	add(PkgResources.getString("PKG.DEPENDANTS"),
	    new JScrollPane(tp_rdep));
	if (pkg != null) {
	    showPkg(pkg);
	}
    }

    /*
     * Show the files tab.
     */
    private void showFilesTab() {
	JPanel jfp = new JPanel(new BorderLayout());
	jfp.add(tp_filehead, BorderLayout.PAGE_START);
	jfp.add(tp_filelist, BorderLayout.CENTER);
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
	int i = indexOfComponent(jp_ovl);
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
