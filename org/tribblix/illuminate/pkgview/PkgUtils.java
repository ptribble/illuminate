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

import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.text.DecimalFormat;

/**
 * PkgUtils - utility methods to format information.
 *
 * @author Peter Tribble
 * @version 2.0
 */
public final class PkgUtils {

    private PkgUtils() {
    }

    /**
     * Returns the pkginfo file as a html table.
     *
     * @param pkg the package to display
     * @param zc the ZapConfig for this image
     *
     * @return a nicely formatted version of the pkginfo file
     */
    public static String infoTable(SVR4Package pkg, ZapConfig zc) {
	StringBuilder sb = new StringBuilder(256);
	Map<String, String> infomap = pkg.infoMap();
	// clean out the junk
	infomap.remove("#FASPACD");
	infomap.remove("PKG");
	infomap.remove("MAXINST");
	infomap.remove("PKGINST");
	infomap.remove("PKGSAV");
	infomap.remove("OAMBASE");
	infomap.remove("ARCH");
	infomap.remove("TZ");
	infomap.remove("PATH");
	infomap.remove("UPDATE");
	infomap.remove("ISTATES");
	infomap.remove("LANG");
	Iterator<String> itr = infomap.keySet().iterator();
	while (itr.hasNext()) {
	    String s = itr.next();
	    if (s.startsWith("LC_") || s.startsWith("PATCH_")
		   || s.startsWith("SUNW_") || s.startsWith("ACTIVE_")
		   || s.startsWith("SCRIPTS_") || s.startsWith("PKG_")
		   || s.startsWith("IPS_")) {
		itr.remove();
	    }
	}
	// and the following are uninteresting to the user
	infomap.remove("CLASSES");
	infomap.remove("HOTLINE");
	infomap.remove("PSTAMP");
	infomap.remove("CATEGORY");
	if ("/".equals(infomap.get("BASEDIR"))) {
	    infomap.remove("BASEDIR");
	}
	headRow(sb, PkgResources.getString("PKGUTILS.PROPERTY"),
			PkgResources.getString("PKGUTILS.VALUE"));
	for (Map.Entry<String, String> entry : infomap.entrySet()) {
	    String v = entry.getValue();
	    if (!"".equals(v)) {
		String s = entry.getKey();
		if ("ZAP_URL".equals(s)) {
		    addRow(sb, s, "<a href=\"" + v + "\">" + v + "</a>");
		} else {
		    addRow(sb, s, v);
		}
	    }
	}

	if (zc.exists()) {
	    String cver = zc.currentVersion(pkg.getName());
	    if (pkg.getVersion().equals(cver)) {
		addRow(sb, "Update status", "Up to date");
	    } else {
		if (cver == null) {
		    addRow(sb, "Update status", "(Package not in catalog)");
		} else {
		    addRow(sb, "Update status",
			"New version " + cver + " available");
		}
	    }
	}
	return wrapTable(sb);
    }

    /**
     * Describe an overlay.
     *
     * @param ovl the overlay to describe
     *
     * @return a formatted table describing the given overlay
     */
    public static String infoTable(Overlay ovl) {
	// the fixed text here varies from 37 to 51 characters
	// overlay names are between 3 and 20 characters
	StringBuilder sbh = new StringBuilder(70);
	sbh.append("Overlay ").append(ovl.getName());
	if (ovl.isInstalled()) {
	    if (ovl.isComplete()) {
		sbh.append(" is installed and complete.");
	    } else {
		sbh.append(" is marked as installed but incomplete.");
	    }
	} else {
	    if (ovl.isComplete()) {
		sbh.append(" is complete but not marked as installed.");
	    } else {
		sbh.append(" is uninstalled and incomplete.");
	    }
	}
	// the fixed text here is 35 characters
	// headrow is 45, each addrow is 27, wraptable adds 29
	// minimum length 200, max 256
	StringBuilder sb = new StringBuilder(256);
	headRow(sb, PkgResources.getString("PKGUTILS.PROPERTY"),
			PkgResources.getString("PKGUTILS.VALUE"));
	addRow(sb, "Name", ovl.getName());
	addRow(sb, "Description", ovl.getDescription());
	addRow(sb, "Version", ovl.getVersion());
	return sbh.toString() + wrapTable(sb);
    }

    /**
     * Produce a html table displaying the dependencies of the requested
     * package.
     *
     * @param pkg the package to display
     *
     * @return a formatted html table
     */
    public static String dependencyTable(SVR4Package pkg) {
	return dependencyTable(pkg.getDependencySet(), pkg.getRDependencySet(),
			pkg.getIncompatibleSet());
    }

    /**
     * Produce a html table displaying the overlays that the requested
     * overlay depends on.
     *
     * @param ovl the overlay to display
     *
     * @return a formatted html table
     */
    public static String dependencyTable(Overlay ovl) {
	StringBuilder sb = new StringBuilder(128);
	headRow2(sb, "This Overlay depends on");
	for (Overlay o : ovl.getOverlays()) {
	    addRow(sb, o.getName(), o.getDescription());
	}
	return wrapTable(sb);
    }

    /**
     * Produce a html table displaying the packages that are members
     * of an overlay.
     *
     * @param ovl the overlay to display
     *
     * @return a formatted html table
     */
    public static String overlayMembers(Overlay ovl) {
	StringBuilder sb = new StringBuilder(256);
	headRow2(sb, "This Overlay contains the following packages");
	for (SVR4Package pkg : ovl.getPackages()) {
	    addRow(sb, pkg.toString(), pkg.getDescription());
	}
	return wrapTable(sb);
    }

    /*
     * Common dependency tree code
     */
    private static String dependencyTable(Set<String> depset,
				Set<String> rdepset, Set<String> idepset) {
	StringBuilder sb = new StringBuilder(256);

	headRow(sb, PkgResources.getString("PKGUTILS.PACKAGE"),
		PkgResources.getString("PKGUTILS.DEPENDENCY"));
	innerdeptable(sb, depset,
		PkgResources.getString("PKGUTILS.PREREQ"));
	innerdeptable(sb, rdepset,
		PkgResources.getString("PKGUTILS.REQ"));
	innerdeptable(sb, idepset,
		PkgResources.getString("PKGUTILS.INCOMP"));
	return wrapTable(sb);
    }

    private static void innerdeptable(StringBuilder sb, Set<String> depset,
			String deptype) {
	for (String s : depset) {
	    addRow(sb, s, deptype);
	}
    }

    /**
     * Produce a html table displaying the details of a ContentsPackage.
     * This gives the number of entries of different types - files,
     * directories, soft and hard links, and space used.
     *
     * @param cpp a ContentsPackage to display
     *
     * @return a formatted html table
     */
    public static String detailTable(ContentsPackage cpp) {
	StringBuilder sb = new StringBuilder();
	if (cpp != null) {
	    sb.append(doDetailTable(cpp));
	    headRow(sb, PkgResources.getString("PKGUTILS.FILELIST"));
	}
	return wrapTable(sb);
    }

    /**
     * Produce a textual list of the files in a ContentsPackage.
     * Not html, as that's too expensive to display.
     *
     * @param cpp a ContentsPackage to display
     *
     * @return a textual list of filenames
     */
    public static String doTextFileList(ContentsPackage cpp) {
	StringBuilder sb = new StringBuilder(80);
	if (cpp != null) {
	    for (ContentsFileDetail cfd : cpp.getDetails()) {
		sb.append(cfd.getName()).append('\n');
	    }
	}
	return sb.toString();
    }

    private static String doDetailTable(ContentsPackage cpp) {
	StringBuilder sb = new StringBuilder(320);

	headRow2(sb, PkgResources.getString("PKGUTILS.DETAILS"));
	addRow(sb, PkgResources.getString("PKGUTILS.FILES"), cpp.numFiles());
	addRow(sb, PkgResources.getString("PKGUTILS.DIRS"),
				cpp.numDirectories());
	addRow(sb, PkgResources.getString("PKGUTILS.HARDLINKS"),
				cpp.numHardLinks());
	addRow(sb, PkgResources.getString("PKGUTILS.SYMLINKS"),
				cpp.numSymLinks());
	addRow(sb, PkgResources.getString("PKGUTILS.SPC"),
				niceSpaceUsed(cpp.spaceUsed()));

	return wrapTable(sb);
    }

    /**
     * Produce a html table displaying a Set of packages.
     * The title row contains "Depending on this overlay:"
     *
     * @param ovls a Set of overlays
     *
     * @return a formatted html table
     */
    public static String ovlDeps(Set<Overlay> ovls) {
	StringBuilder sb = new StringBuilder(105);
	headRow2(sb, "Depending on this overlay:");
	if (ovls != null) {
	    for (Overlay ovl : ovls) {
		addRow(sb, ovl.getName(), ovl.getDescription());
	    }
	}
	return wrapTable(sb);
    }

    /**
     * Produce a html table displaying a Set of packages.
     * The title row contains "Depending on this package:"
     *
     * @param pkgs a Set of packages
     *
     * @return a formatted html table
     */
    public static String revDeps(Set<SVR4Package> pkgs) {
	StringBuilder sb = new StringBuilder(93);
	headRow(sb, "Depending on this package:");
	if (pkgs != null) {
	    for (SVR4Package pkg : pkgs) {
		addRow(sb, pkg.getName());
	    }
	}
	return wrapTable(sb);
    }

    /**
     * Produce a html table describing the details of a given file.
     * This includes type, ownership, permissions, size, and package
     * membership.
     *
     * @param cfd a ContentsFileDetail representing a file
     *
     * @return a formatted html table
     */
    public static String fileDetailTable(ContentsFileDetail cfd) {
	StringBuilder sb = new StringBuilder(400);
	headRow2(sb, "Path name: " + cfd.getName());
	addRow(sb, "File Type:", cfd.getDescriptiveType());
	if (cfd.isLink()) {
	    addRow(sb, "Link target:", cfd.getTarget());
	} else {
	    addRow(sb, "Owner:", cfd.getOwner());
	    addRow(sb, "Group owner:", cfd.getGroup());
	    addRow(sb, "Permissions:", cfd.getMode());
	    if (cfd.isRegular()) {
		addRow(sb, "Size:", cfd.getSize());
	    }
	}
	sb.append("</table>\n<table width=\"100%\">");
	headRow(sb, "This " + cfd.getBasicType()
			+ " is a member of the following packages");
	for (String pname : cfd.getPackageNames()) {
	    addRow(sb, pname);
	}
	return wrapTable(sb);
    }

    /**
     * Produce a html table describing the overlays the given file
     * is a member of.
     *
     * @param ovlist an OverlayList enumerating all overlays
     * @param cfd a ContentsFileDetail representing a file
     *
     * @return a formatted html table
     */
    public static String overlayMembership(OverlayList ovlist,
					   ContentsFileDetail cfd) {
	StringBuilder sb = new StringBuilder();
	headRow2(sb, "This " + cfd.getBasicType()
			+ " is part of the following overlays");
	for (Overlay ovl : ovlist.containingOverlays(cfd.getPackages())) {
	    addRow(sb, ovl.toString(), ovl.getDescription());
	}
	return wrapTable(sb);
    }

    /**
     * Produce a html table describing the overlays the given package
     * is a member of.
     *
     * @param pkg the package to display
     * @param ovlist an OverlayList enumerating all overlays
     *
     * @return a formatted html table
     */
    public static String overlayMembership(SVR4Package pkg,
		OverlayList ovlist) {
	StringBuilder sb = new StringBuilder(80);

	headRow2(sb, PkgResources.getString("PKGUTILS.OVL"));
	for (Overlay ovl : ovlist.containingOverlays(pkg)) {
	    addRow(sb, ovl.toString(), ovl.getDescription());
	}

	return wrapTable(sb);
    }

    private static void headRow(StringBuilder sb, String s1, String s2) {
	// adds 46 characters to the string
	sb.append("<tr bgcolor=\"#eeeeee\"><th>").append(s1)
	    .append("</th><th>").append(s2).append("</th></tr>\n");
    }

    private static void headRow2(StringBuilder sb, String s) {
	// adds 49 characters to the string
	sb.append("<tr bgcolor=\"#eeeeee\"><th colspan=\"2\">").append(s)
	    .append("</th></tr>\n");
    }

    private static void headRow(StringBuilder sb, String s) {
	// adds 37 characters to the string
	sb.append("<tr bgcolor=\"#eeeeee\"><th>").append(s)
	    .append("</th></tr>\n");
    }

    private static void addRow(StringBuilder sb, String s) {
	// adds 19 characters to the string
	sb.append("<tr><td>").append(s).append("</td></tr>\n");
    }

    private static void addRow(StringBuilder sb, String s1, long l) {
	// adds 28 characters to the string
	sb.append("<tr><td>").append(s1).append("</td><td>").append(l)
	    .append("</td></tr>\n");
    }

    private static void addRow(StringBuilder sb, String s1, String s2) {
	// adds 28 characters to the string
	sb.append("<tr><td>").append(s1).append("</td><td>").append(s2)
	    .append("</td></tr>\n");
    }

    private static String wrapTable(StringBuilder sb) {
	// adds 29 characters to the string
	sb.insert(0, "<table width=\"100%\">");
	sb.append("</table>\n");
	return sb.toString();
    }

    private static String niceSpaceUsed(long space) {
        DecimalFormat df = new DecimalFormat("##0.0#");
	StringBuilder sb = new StringBuilder();
	double dspace = space;
	int iscale = 0;
	while (dspace > 1024.0 && iscale < 3) {
	    iscale++;
	    dspace /= 1024.0;
	}
	sb.append(df.format(dspace)).append(' ');
	switch (iscale) {
	    case 0:
		sb.append(PkgResources.getString("PKGUTILS.BYTES"));
		break;
	    case 1:
		sb.append(PkgResources.getString("PKGUTILS.KBYTES"));
		break;
	    case 2:
		sb.append(PkgResources.getString("PKGUTILS.MBYTES"));
		break;
	    case 3:
		sb.append(PkgResources.getString("PKGUTILS.GBYTES"));
		break;
	}
	return sb.toString();
    }
}
