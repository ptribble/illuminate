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
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

/**
 * PkgCheck - check that what should be installed actually is.
 */
public class PkgCheck {

    private String altroot;

    private boolean check = true;
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
    public PkgCheck(String altroot, String[] args) {
	this.altroot = altroot;
	PkgList plist = new PkgList(altroot);
	Set <String> names = parseArgs(args);
	if (checkovl) {
	    doOverlays(names, plist);
	} else if (dopaths || partpaths) {
	    cp = ContentsParser.getInstance(altroot);
	    if (dopaths) {
		doPathNames(names);
	    } else {
		doPartPathNames(names);
	    }
	} else {
	    cp = ContentsParser.getInstance(altroot);
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

    private Set <String> parseArgs(String[] args) {
	Set <String> names = new HashSet <String> ();
	for (String arg : args) {
	    if ("-l".equals(arg)) {
		list = true;
		check = false;
	    } else if ("-o".equals(arg)) {
		checkovl = true;
	    } else if ("-v".equals(arg)) {
		verbose = true;
	    } else if ("-a".equals(arg)) {
		allpkgs = true;
	    } else if ("-V".equals(arg)) {
		verbose = true;
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
	if (dopaths && checkovl) {
	    usage();
	}
	if (partpaths && checkovl) {
	    usage();
	}
	return names;
    }

    private void doPathNames(Set <String> names) {
	for (String name : names) {
	    ContentsFileDetail cfd = cp.getFileDetail(name);
	    if (cfd == null) {
		System.err.println("ERROR: invalid name " + name);
	    } else {
		showFile(cfd);
	    }
	}
    }

    private void doPartPathNames(Set <String> names) {
	for (String path : cp.getPaths()) {
	    for (String name : names) {
		if (path.indexOf(name) > 0) {
		    showFile(cp.getFileDetail(path));
		}
	    }
	}
    }

    private void doOverlays(Set <String> names, PkgList plist) {
	OverlayList ovlist = new OverlayList(altroot, plist);
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

    private void checkOverlay(Overlay ovl) {
	if (ovl.isInstalled() && !ovl.isComplete()) {
	    System.out.println("Overlay " + ovl +
			" is installed but incomplete");
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
	    System.out.println("Overlay " + ovl +
			" is complete but uninstalled");
	}
    }

    /*
     * Shows a nicely formatted list of packages that own the given file.
     */
    private void showOwningPkgs(ContentsFileDetail cfd) {
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

    private void showFile(ContentsFileDetail cfd) {
	if (dopaths || partpaths) {
	    showOwningPkgs(cfd);
	}
	if (list) {
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
	if (check) {
	    File f = new File(altroot, cfd.getName());
	    if (f.exists()) {
		if (cfd.isRegular()) {
		    if (f.isFile()) {
			long fmodtime = f.lastModified()/1000;
			long pmodtime = cfd.lastModified();
			if (f.length() != cfd.getSize()) {
			    if (cfd.isEditable()) {
				if (verbose) {
				    System.out.println("   WARNING: File " +
					cfd.getName() +
					" has incorrect size");
				}
			    } else {
				System.out.println("   ERROR: File " +
					cfd.getName() +
					" has incorrect size");
			    }
			}
			// allow a little rounding error
			if (Math.abs(fmodtime - pmodtime) >= 2) {
			    if (cfd.isEditable()) {
				if (verbose) {
				    System.out.println("   WARNING: File " +
					cfd.getName() +
					" has incorrect modification time");
				}
			    } else {
				System.out.println("   ERROR: File " +
					cfd.getName() +
					" has incorrect modification time");
			    }
			}
		    } else {
			System.out.println("   ERROR: Path " +
					cfd.getName() +
					" is not a file");
		    }
		}
		if (cfd.isDirectory()) {
		    if (!f.isDirectory()) {
			System.out.println("   ERROR: Path " +
					cfd.getName() +
					" is not a directory");
		    }
		}
	    } else {
		System.err.println("Missing or unreadable path "
			+ cfd.getName());
	    }
	}
    }

    private void doProcess(String pkg) {
	ContentsPackage cpp = cp.getPackage(pkg);
	if (cpp != null) {
	    for (ContentsFileDetail cfd : cpp.getDetails()) {
		showFile(cfd);
	    }
	}
    }

    private static void usage() {
	System.err.println("Usage: check [-R alt_root] [-v|-V] "
		+ "[-o | -p path ... | -P partial-path ...] [name ...]");
	System.exit(1);
    }

    /**
     * Run the application.
     *
     * @param args Command line arguments
     */
    public static void main(String args[]) {
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
