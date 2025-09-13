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
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

/**
 * PkgCheck - check that what should be installed actually is.
 */
public class PkgCheck {

    private PackageHandler pkghdl;

    private boolean list;
    private boolean verbose;
    private boolean dopaths;
    private boolean partpaths;
    private boolean allpkgs;
    private boolean checkovl;

    private ContentsParser cp;

    /**
     * Check integrity.
     *
     * @param altroot an alternate root of the package tree
     * @param args a list of package, overlay, or file names
     */
    public PkgCheck(final String altroot, final String[] args) {
	pkghdl = new PackageHandler(altroot);
	PkgList plist = pkghdl.getPkgList();
	Set<String> names = parseArgs(args);
	if (checkovl) {
	    doOverlays(names);
	} else if (dopaths || partpaths) {
	    cp = pkghdl.getContentsParser();
	    if (dopaths) {
		doPathNames(names);
	    } else {
		doPartPathNames(names);
	    }
	} else {
	    cp = pkghdl.getContentsParser();
	    for (String pkg : allpkgs ? plist.getPackageNames() : names) {
		if (plist.getPackage(pkg) == null) {
		    System.out.println("Invalid package " + pkg);
		} else {
		    System.out.println("Valid package " + pkg);
		    doProcess(pkg);
		}
	    }
	}
    }

    private Set<String> parseArgs(final String[] args) {
	Set<String> names = new HashSet<>();
	for (String arg : args) {
	    if ("-l".equals(arg)) {
		list = true;
	    } else if ("-o".equals(arg)) {
		checkovl = true;
	    } else if ("-v".equals(arg)) {
		verbose = true;
	    } else if ("-a".equals(arg)) {
		allpkgs = true;
	    } else if ("-p".equals(arg)) {
		dopaths = true;
		partpaths = false;
	    } else if ("-P".equals(arg)) {
		dopaths = false;
		partpaths = true;
	    } else if (arg.startsWith("-")) {
		usage();
	    } else {
		names.add(arg);
	    }
	}
	if (checkovl) {
	    if (dopaths || partpaths || allpkgs || list) {
		usage();
	    }
	}
	if (dopaths) {
	    if (partpaths || allpkgs || list) {
		usage();
	    }
	}
	if (partpaths) {
	    if (allpkgs || list) {
		usage();
	    }
	}
	return names;
    }

    private void doPathNames(final Set<String> names) {
	for (String name : names) {
	    ContentsFileDetail cfd = cp.getFileDetail(name);
	    if (cfd == null) {
		System.err.println("ERROR: invalid name " + name);
	    } else {
		showFile(cfd);
	    }
	}
    }

    private void doPartPathNames(final Set<String> names) {
	for (String path : cp.getPaths()) {
	    for (String name : names) {
		if (path.indexOf(name) > 0) {
		    showFile(cp.getFileDetail(path));
		}
	    }
	}
    }

    private void doOverlays(final Set<String> names) {
	OverlayList ovlist = pkghdl.getOverlayList();
	if (names.isEmpty()) {
	    for (Overlay ovl : ovlist.getOverlays()) {
		checkOverlay(ovl);
	    }
	} else {
	    for (String name : names) {
		Overlay ovl = ovlist.getOverlay(name);
		if (ovl == null) {
		    System.out.println("Invalid overlay " + name);
		} else {
		    checkOverlay(ovl);
		}
	    }
	}
    }

    private void checkOverlay(final Overlay ovl) {
	if (ovl.isInstalled() && !ovl.isComplete()) {
	    System.out.println("Overlay " + ovl
			+ " is installed but incomplete");
	    if (verbose) {
		for (Overlay ovl2 : ovl.missingOverlays()) {
		    System.out.println("  missing required overlay " + ovl2);
		}
		for (SVR4Package pkg : ovl.missingPackages()) {
		    System.out.println("  missing required package " + pkg);
		}
	    }
	}
	if (!ovl.isInstalled() && ovl.isComplete()) {
	    System.out.println("Overlay " + ovl
			+ " is complete but uninstalled");
	}
    }

    /*
     * Shows a nicely formatted list of packages that own the given file.
     */
    private void showOwningPkgs(final ContentsFileDetail cfd) {
	System.out.print("Path " + cfd.getName() + " belongs to the following");
	System.out.println(cfd.isShared() ? " packages:" : " package:");
	int i = 0;
	for (String s : cfd.getPackageNames()) {
	    i += 2;
	    i += s.length();
	    if (i > 79) {
		System.out.println();
		i = s.length() + 2;
	    }
	    System.out.print("  " + s);
	}
	System.out.println();
    }

    private void listFile(final ContentsFileDetail cfd) {
	System.out.print("  " + cfd.getName());
	if (verbose) {
	    System.out.print(" owner=" + cfd.getOwner());
	    System.out.print(" group=" + cfd.getGroup());
	    System.out.print(" mode=" + cfd.getMode());
	    if (cfd.isRegular()) {
		System.out.print(" size=" + cfd.getSize());
	    }
	}
	System.out.println();
    }

    private void checkFile(final ContentsFileDetail cfd) {
	File f = new File(pkghdl.getRoot(), cfd.getName());
	if (f.exists()) {
	    if (cfd.isRegular()) {
		if (f.isFile()) {
		    long fmodtime = f.lastModified() / 1000;
		    long pmodtime = cfd.lastModified();
		    if (f.length() != cfd.getSize()) {
			if (cfd.isEditable()) {
			    if (verbose) {
				System.out.println("WARNING: File "
					    + cfd.getName()
					    + " has incorrect size");
			    }
			} else {
			    System.out.println("ERROR: File "
					    + cfd.getName()
					    + " has incorrect size");
			}
		    }
		    // allow a little rounding error
		    if (Math.abs(fmodtime - pmodtime) >= 2) {
			if (cfd.isEditable()) {
			    if (verbose) {
				System.out.println("WARNING: File "
					+ cfd.getName()
					+ " has incorrect modification time");
			    }
			} else {
			    System.out.println("ERROR: File "
					+ cfd.getName()
					+ " has incorrect modification time");
			}
		    }
		} else {
		    System.out.println("ERROR: Path "
				       + cfd.getName()
				       + " is not a file");
		}
	    }
	    if (cfd.isDirectory() && !f.isDirectory()) {
		System.out.println("ERROR: Path "
				   + cfd.getName()
				   + " is not a directory");
	    }
	} else {
	    System.err.println("ERROR: Missing or unreadable path "
			       + cfd.getName());
	}
    }

    private void showFile(final ContentsFileDetail cfd) {
	if (dopaths || partpaths) {
	    showOwningPkgs(cfd);
	}
	if (list) {
	    listFile(cfd);
	} else {
	    checkFile(cfd);
	}
    }

    private void doProcess(final String pkg) {
	ContentsPackage cpp = cp.getPackage(pkg);
	if (cpp != null) {
	    for (ContentsFileDetail cfd : cpp.getDetails()) {
		showFile(cfd);
	    }
	}
    }

    private static void usage() {
	System.err.println("Usage: check [-R alt_root] [-v] "
		+ "[-a | -l | -o | -p path ... | -P partial-path ...] "
		+ "[name ...]");
	System.exit(1);
    }

    /**
     * Run the application.
     *
     * @param args Command line arguments
     */
    public static void main(final String[] args) {
	if (args.length == 0) {
	    usage();
	}
	if (args.length > 2 && "-R".equals(args[0])) {
	    new PkgCheck(args[1], Arrays.copyOfRange(args, 2, args.length));
	} else {
	    new PkgCheck("/", args);
	}
    }
}
