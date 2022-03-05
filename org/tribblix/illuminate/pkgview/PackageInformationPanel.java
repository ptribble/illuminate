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
import java.awt.Cursor;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import java.io.File;

/**
 * Show information about installed files or packages in a set of
 * tabbed panels.
 *
 * @author Peter Tribble
 * @version 2.0
 */
public class PackageInformationPanel extends JTabbedPane {

    private String altroot;

    private PackageTextPane tp_info;
    private PackageTextPane tp_dep;
    private PackageTextPane tp_rdep;
    private PackageTextPane tp_ovl;
    private JScrollPane jp_ovl;
    private PackageTextPane tp_files;
    private boolean showfiles;
    private OverlayList ovlist;
    private ContentsParser cp;
    private ZapConfig zc;

    /**
     * Create a default PackageInformationPanel showing the default tabs.
     *
     * @param altroot the root of the file system
     * @param ovlist an OverlayList object
     * @param zc the ZapConfig for this image
     */
    public PackageInformationPanel(String altroot, OverlayList ovlist,
			    ZapConfig zc) {
	this(altroot, ovlist, zc, true);
    }

    /**
     * Create a PackageInformationPanel showing the specified tabs.
     *
     * @param altroot the root of the file system
     * @param ovlist an OverlayList object
     * @param zc the ZapConfig for this image
     * @param showdependencies a boolean determining if dependencies are shown
     */
    public PackageInformationPanel(String altroot, OverlayList ovlist,
			    ZapConfig zc, boolean showdependencies) {
	this.altroot = altroot;
	this.ovlist = ovlist;
	this.zc = zc;

	tp_info = new PackageTextPane();
	tp_dep = new PackageTextPane();
	tp_rdep = new PackageTextPane();
	tp_ovl = new PackageTextPane();
	tp_files = new PackageTextPane();
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
		    revDeps(pkg.getDependantSet()));
	} else {
	    setInfoText("Not installed", "", "");
	}
	setOverlayText(PkgUtils.overlayMembership(pkg, ovlist));
	if (cp != null) {
	    Cursor c = getCursor();
	    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    setFilesText(PkgUtils.detailTable(pkg, cp));
	    setCursor(c);
	}
    }

    public void showOverlay(Overlay ovl) {
	setOvlTab(PkgResources.getString("PKG.PACKAGES"));
	setInfoText(PkgUtils.infoTable(ovl),
		    PkgUtils.dependencyTable(ovl),
		    ovlDeps(ovlist.containingOverlays(ovl)));
	setOverlayText(PkgUtils.overlayMembers(ovl));
	disableFiles();
    }

    private String ovlDeps(Set <Overlay> ovls) {
	StringBuilder sb = new StringBuilder(105);
	PkgUtils.headRow2(sb, "Depending on this overlay:");
	if (ovls != null) {
	    for (Overlay ovl : ovls) {
		PkgUtils.addRow(sb, ovl.getName(), ovl.getDescription());
	    }
	}
	return PkgUtils.wrapTable(sb);
    }

    private String revDeps(Set <SVR4Package> pkgs) {
	StringBuilder sb = new StringBuilder(93);
	PkgUtils.headRow(sb, "Depending on this package:");
	if (pkgs != null) {
	    for (SVR4Package pkg : pkgs) {
		PkgUtils.addRow(sb, pkg.getName());
	    }
	}
	return PkgUtils.wrapTable(sb);
    }

    public void showFile(File f) {
	setOvlTab(PkgResources.getString("PKG.OVERLAYS"));
	if (cp == null) {
	    infoOnly("Package information not available.");
	} else {
	    // need to remove any altroot from the filename before
	    // matching it against the contents file
	    ContentsFileDetail cfd = cp.getFileDetail(f.toString()
			.replaceFirst(altroot, "/").replaceFirst("//", "/"));
	    if (cfd == null) {
		infoOnly("Not a member of any package.");
	    } else {
		setInfoText(fileDetailTable(cfd), "", "");
		setOverlayText(overlayMembership(cfd));
	    }
	}
    }

    private String fileDetailTable(ContentsFileDetail cfd) {
	StringBuilder sb = new StringBuilder(400);
	PkgUtils.headRow2(sb, "Path name: " + cfd.getName());
	PkgUtils.addRow(sb, "File Type:", cfd.getDescriptiveType());
	if (cfd.isLink()) {
	    PkgUtils.addRow(sb, "Link target:", cfd.getTarget());
	} else {
	    PkgUtils.addRow(sb, "Owner:", cfd.getOwner());
	    PkgUtils.addRow(sb, "Group owner:", cfd.getGroup());
	    PkgUtils.addRow(sb, "Permissions:", cfd.getMode());
	    if (cfd.isRegular()) {
		PkgUtils.addRow(sb, "Size:", cfd.getSize());
	    }
	}
	sb.append("</table>\n<table width=\"100%\">");
	PkgUtils.headRow(sb, "This " + cfd.getBasicType()
			+ " is a member of the following packages");
	for (String pname : cfd.getPackageNames()) {
	    PkgUtils.addRow(sb, pname);
	}
	return PkgUtils.wrapTable(sb);
    }

    /*
     * Display which overlays a given file is in.
     */
    private String overlayMembership(ContentsFileDetail cfd) {
	StringBuilder sb = new StringBuilder();
	PkgUtils.headRow2(sb, "This " + cfd.getBasicType()
			+ " is part of the following overlays");
	for (Overlay ovl : ovlist.containingOverlays(cfd.getPackages())) {
	    PkgUtils.addRow(sb, ovl.toString(), ovl.getDescription());
	}
	return PkgUtils.wrapTable(sb);
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

    private void setFilesText(String s) {
	if (!showfiles) {
	    showFiles();
	}
	enableFiles();
	tp_files.setText(s);
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
    private void showFiles() {
	add(PkgResources.getString("PKG.CONTENTS"), new JScrollPane(tp_files));
	showfiles = true;
    }

    /*
     * Disable the files tab
     */
    private void disableFiles() {
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
	cp = ContentsParser.getInstance(altroot);
    }
}
