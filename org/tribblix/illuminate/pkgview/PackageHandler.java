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

import java.io.File;
import java.util.Set;
import java.util.TreeSet;
import uk.co.petertribble.jumble.JumbleFile;

/**
 * PackageHandler - central handler for all package requests for information.
 *
 * This class should be referred to in order to retrieve all
 * information about packaging. A client should ask this class
 * for a copy of the given information, rather than create a
 * new copy itself.
 *
 * Classes are also intended to use this class for reading files
 * from the filesystem, and shouldn't do so directly themselves.
 *
 * @author Peter Tribble
 * @version 1.0
 */
public class PackageHandler {

    private final String altroot;

    /*
     * These define the various packaging locations for this system.
     */
    private static final String PKG_ROOT = "/var/sadm/pkg";
    private static final String OVL_ROOT = "/var/sadm/overlays";
    private static final String ZAP_ROOT = "/etc/zap";

    /*
     * These are File objects corresponding to the above locations,
     * relocated to the root of this image, to avoid having to
     * create them frequently.
     */
    private final File pkgdirf;
    private final File ovldirf;
    private final File zapdirf;

    /*
     * These are our copies of the relevant data, created on demand.
     */
    private PkgList pkglist;
    private OverlayList ovlist;
    private ContentsParser cp;
    private ZapConfig zc;

    /**
     * Create a new PackageHandler, at the system root.
     */
    public PackageHandler() {
	this("/");
    }

    /**
     * Create a new PackageHandler, at the given root.
     *
     * @param altroot  An alternate root directory for this OS image
     */
    public PackageHandler(String altroot) {
	this.altroot = altroot;
	pkgdirf = new File(altroot + PKG_ROOT);
	ovldirf = new File(altroot + OVL_ROOT);
	zapdirf = new File(altroot + ZAP_ROOT);
    }

    /**
     * Get the root of this OS image. The intention is that most consumers
     * should not need to know this.
     *
     * @return the root of this OS image.
     */
    public String getRoot() {
	return altroot;
    }

    /**
     * Return the list of packages for this OS image.
     *
     * @return a PkgList containing the list of packages in this OS image
     */
    public synchronized PkgList getPkgList() {
	if (pkglist == null) {
	    pkglist = new PkgList(this);
	}
	return pkglist;
    }

    /**
     * Return the list of overlays for this OS image.
     *
     * @return an OverlayList containing the list of overlays in this OS image
     */
    public synchronized OverlayList getOverlayList() {
	if (ovlist == null) {
	    ovlist = new OverlayList(this);
	}
	return ovlist;
    }

    /**
     * Return the ContentsParser for this OS image.
     *
     * @return a ContentsParser for this OS image
     */
    public synchronized ContentsParser getContentsParser() {
	if (cp == null) {
	    cp = new ContentsParser(this);
	}
	return cp;
    }

    /**
     * Return the ZapConfig for this OS image.
     *
     * @return a ZapConfig for this OS image
     */
    public synchronized ZapConfig getZapConfig() {
	if (zc == null) {
	    zc = new ZapConfig(this);
	}
	return zc;
    }

    /*
     * Functions below are helpers.
     */

    protected Set<String> listPackageNames() {
	Set<String> pnamelist = new TreeSet<>();

	if (pkgdirf.exists()) {
	    for (File f : pkgdirf.listFiles()) {
		if (f.isDirectory()
		        && !f.isHidden()
		        && !"locale".equals(f.getName())
		        && new File(f, "pkginfo").exists()) {
		    pnamelist.add(f.getName());
		}
	    }
	}
	return pnamelist;
    }

    protected Set<String> listOverlayNames() {
	Set<String> onamelist = new TreeSet<>();

	if (ovldirf.exists()) {
	    for (File f : ovldirf.listFiles()) {
		if (f.getName().endsWith(".ovl")) {
		    String fname = f.getName();
		    String rootname = fname.substring(0, fname.length() - 4);
		    File f2 = new File(ovldirf, rootname + ".pkgs");
		    if (f2.exists()) {
			onamelist.add(rootname);
		    }
		}
	    }
	}
	return onamelist;
    }

    protected String[] listRepositories() {
	if (zapdirf.exists()) {
	    return JumbleFile.getLines(new File(zapdirf, "repo.list"));
	}
	return new String[0];
    }

    protected boolean isPkgInstalled(String name) {
	return new File(pkgdirf, name).exists();
    }

    protected String[] getPkgDepend(String name) {
	return JumbleFile.getLines(new File(pkgdirf, name + "/install/depend"));
    }

    protected String getPkgInfo(String name) {
	return JumbleFile.getStringContents(
		new File(pkgdirf, name + "/pkginfo"));
    }

    protected boolean isOvlInstalled(String name) {
	File f2 = new File(ovldirf, "installed");
	return new File(f2, name).exists();
    }

    protected String[] getOvlPkgs(String name) {
	return JumbleFile.getLines(new File(ovldirf, name + ".pkgs"));
    }

    protected String[] getOvlOvl(String name) {
	return JumbleFile.getLines(new File(ovldirf, name + ".ovl"));
    }

    protected String[] getRepository(String repo) {
	File f = new File(altroot + ZAP_ROOT
			+ "/repositories", repo + ".repo");
	if (f.exists()) {
	    return JumbleFile.getLines(f);
	}
	return new String[0];
    }

    protected String[] getCatalog(String repo) {
	return JumbleFile.getLines(new File(altroot
		+ ZAP_ROOT + "/repositories/" + repo + ".catalog"));
    }
}
