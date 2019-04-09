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

import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.text.DecimalFormat;

/**
 * PkgUtils - utility methods to format information
 *
 * @author Peter Tribble
 * @version 2.0
 */
public class PkgUtils {

    /**
     * Returns the pkginfo file as a html table.
     */
    static public String infoTable(SVR4Package pkg) {
	StringBuilder sb = new StringBuilder();
	Map <String, String> infomap = pkg.infoMap();
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
	Iterator <String> itr = infomap.keySet().iterator();
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
	for (String s : infomap.keySet()) {
	    String v = infomap.get(s);
	    if (!"".equals(v)) {
		if ("ZAP_URL".equals(s)) {
		    addRow(sb, s, "<a href=\"" + v + "\">" + v +"</a>");
		} else {
		    addRow(sb, s, v);
		}
	    }
	}

	return wrapTable(sb);
    }

    static public String infoTable(Overlay ovl) {
	StringBuilder sb = new StringBuilder();
	headRow(sb, PkgResources.getString("PKGUTILS.PROPERTY"),
			PkgResources.getString("PKGUTILS.VALUE"));
	addRow(sb, "Name", ovl.getOverlayName());
	addRow(sb, "Description", ovl.getDescription());
	addRow(sb, "Version", ovl.getVersion());
	return wrapTable(sb);
    }

    static public String dependencyTable(SVR4Package pkg) {
	return dependencyTable(pkg.getDependencySet(), pkg.getRDependencySet(),
			pkg.getIncompatibleSet());
    }

    static public String dependencyTable(Overlay ovl) {
	StringBuilder sb = new StringBuilder();
	headRow2(sb, "This Overlay depends on");
	for (Overlay o : ovl.getOverlays()) {
	    addRow(sb, o.getOverlayName(), o.getDescription());
	}
	return wrapTable(sb);
    }

    static public String overlayMembers(Overlay ovl) {
	StringBuilder sb = new StringBuilder();
	headRow2(sb, "This Overlay contains the following packages");
	for (SVR4Package pkg : ovl.getPackages()) {
	    addRow(sb, pkg.toString(), pkg.getDescription());
	}
	return wrapTable(sb);
    }

    /*
     * Common dependency tree code
     */
    static private String dependencyTable(Set <String> depset,
				Set <String> rdepset, Set <String> idepset) {
	StringBuilder sb = new StringBuilder();

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

    static private void innerdeptable(StringBuilder sb, Set <String> depset,
			String deptype) {
	for (String s : depset) {
	    addRow(sb, s, deptype);
	}
    }

    static public String detailTable(SVR4Package pkg, ContentsParser cp) {
	StringBuilder sb = new StringBuilder();
	if (cp != null) {
	    ContentsPackage cpp = cp.getPackage(pkg.getName());
	    if (cpp != null) {
		sb.append(doDetailTable(cpp));
		sb.append(doFileList(cpp));
	    }
	}
	return sb.toString();
    }

    static private String doFileList(ContentsPackage cpp) {
	StringBuilder sb = new StringBuilder();
	headRow(sb, PkgResources.getString("PKGUTILS.FILELIST"));
	StringBuilder sb2 = new StringBuilder();
	sb2.append("<pre>\n");
	for (ContentsFileDetail cfd : cpp.getDetails()) {
	    sb2.append(cfd.getName()).append("\n");
	}
	sb2.append("</pre>\n");
	return wrapTable(sb) + sb2;
    }

    static private String doDetailTable(ContentsPackage cpp) {
	StringBuilder sb = new StringBuilder();

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

    static public void headRow(StringBuilder sb, String s1, String s2) {
	sb.append("<tr bgcolor=\"#eeeeee\"><th>");
	sb.append(s1);
	sb.append("</th><th>");
	sb.append(s2);
	sb.append("</th></tr>\n");
    }

    static public void headRow2(StringBuilder sb, String s) {
	sb.append("<tr bgcolor=\"#eeeeee\"><th colspan=\"2\">");
	sb.append(s);
	sb.append("</th></tr>\n");
    }

    static public void headRow(StringBuilder sb, String s) {
	sb.append("<tr bgcolor=\"#eeeeee\"><th>");
	sb.append(s);
	sb.append("</th></tr>\n");
    }

    static public void addRow(StringBuilder sb, String s) {
	sb.append("<tr><td>");
	sb.append(s);
	sb.append("</td></tr>\n");
    }

    static public void addRow(StringBuilder sb, String s1, long l) {
	sb.append("<tr><td>");
	sb.append(s1);
	sb.append("</td><td>");
	sb.append(l);
	sb.append("</td></tr>\n");
    }

    static public void addRow(StringBuilder sb, String s1, String s2) {
	sb.append("<tr><td>");
	sb.append(s1);
	sb.append("</td><td>");
	sb.append(s2);
	sb.append("</td></tr>\n");
    }

    static public String wrapTable(StringBuilder sb) {
	sb.insert(0, "<table width=\"100%\">");
	sb.append("</table>\n");
	return sb.toString();
    }

    static private String niceSpaceUsed(long space) {
        DecimalFormat df = new DecimalFormat("##0.0#");
	StringBuilder sb = new StringBuilder();
	double dspace = (double) space;
	int iscale = 0;
	while ((dspace > 1024.0) && (iscale < 3)) {
	    iscale++;
	    dspace /= 1024.0;
	}
	sb.append(df.format(dspace));
	sb.append(" ");
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

    static public String overlayMembership(SVR4Package pkg,
		OverlayList ovlist) {
	StringBuilder sb = new StringBuilder();

	headRow2(sb, PkgResources.getString("PKGUTILS.OVL"));
	for (Overlay ovl : ovlist.containingOverlays(pkg)) {
	    addRow(sb, ovl.toString(), ovl.getDescription());
	}

	return wrapTable(sb);
    }
}
