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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * We parse the contents file and create two hashes.
 *
 * The first hash is by file, or equivalently by line.
 * The key is the filename, the value is a ContentsFileDetail
 * which stores the metadata and the list of packages that contain
 * this file.
 *
 * The second hash is by package. The key is the package name,
 * and the value is a List of ContentsFileDetail's.
 */
public final class ContentsParser {

    private Map<String, ContentsFileDetail> fileHash = new HashMap<>();
    private Map<String, ContentsPackage> pkgHash = new HashMap<>();

    private static final String CONTENTS_FILE = "/var/sadm/install/contents";

    /**
     * Parse a contents file.
     *
     * @param pkghdl a PackageHandler for this OS image
     */
    public ContentsParser(final PackageHandler pkghdl) {
	parse(pkghdl);
    }

    /*
     * Oddly, using this version is significantly slower, although it does
     * consume rather less memory. And timing of the actual reading of the
     * file indicates that this is a lot faster, so I'm not sure why the
     * overall time gets worse.
     *
     * On my machine, the time breakdown is like:
     *  - just reading every line, 0.9s
     *  - just parsing every line, 1.5s, so the parsing adds 0.6s
     *  - populating the fileHash adds about 1s
     *  - populating the pkgHash and its contents adds another 1s
     *
     * So the actual parse is pretty quick - it's populating the maps
     * that really adds to the cost.
     */
    private void parse(final PackageHandler pkghdl) {
	try (BufferedReader in
		= Files.newBufferedReader(
		    Paths.get(pkghdl.getRoot() + CONTENTS_FILE))) {
	    String s;
	    while ((s = in.readLine()) != null) {
		if (s.charAt(0) == '/') {
		    ContentsFileDetail cfd = new ContentsFileDetail(pkghdl, s);
		    fileHash.put(cfd.getName(), cfd);
		    for (String pkgname : cfd.getPackageNames()) {
			ContentsPackage cp = pkgHash.get(pkgname);
			if (cp == null) {
			    cp = new ContentsPackage();
			    pkgHash.put(pkgname, cp);
			}
			cp.addFile(cfd);
		    }
		}
	    }
	} catch (IOException ioe) { }
    }

    /**
     * Return the Set of all paths in the contents file.
     *
     * @return the Set of all paths in the contents file
     */
    public Set<String> getPaths() {
	return fileHash.keySet();
    }

    /**
     * Get the details of a particular path name.
     *
     * @param s the path name to return details of
     *
     * @return the corresponding ContentsFileDetail
     */
    public ContentsFileDetail getFileDetail(final String s) {
	return fileHash.get(s);
    }

    /**
     * Get the details of a particular package.
     *
     * @param pkgname the package name to return details of
     *
     * @return the corresponding ContentsPackage
     */
    public ContentsPackage getPackage(final String pkgname) {
	return pkgHash.get(pkgname);
    }

    /**
     * Get the details of a particular overlay.
     *
     * @param ovl the overlay name to return details of
     *
     * @return the corresponding ContentsPackage
     */
    public ContentsPackage getOverlay(final Overlay ovl) {
	return new ContentsPackage(ovl, this);
    }
}
