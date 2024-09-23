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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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

    private Map <String, ContentsFileDetail> fileHash = new HashMap<>();
    private Map <String, ContentsPackage> pkgHash = new HashMap<>();

    private static final String CONTENTS_FILE = "/var/sadm/install/contents";

    /**
     * Parse a contents file.
     *
     * @param pkghdl a PackageHandler for this OS image
     */
    public ContentsParser(PackageHandler pkghdl) {
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
    private void parse(PackageHandler pkghdl) {
	try (BufferedReader in
		= new BufferedReader(
		    new FileReader(pkghdl.getRoot() + CONTENTS_FILE))) {
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
	} catch (IOException ioe) {}
    }

    /**
     * Return the Set of all paths in the contents file.
     *
     * @return the Set of all paths in the contents file
     */
    public Set <String> getPaths() {
	return fileHash.keySet();
    }

    /**
     * Get the details of a particular path name.
     *
     * @param s the path name to return details of
     *
     * @return the corresponding ContentsFileDetail
     */
    public ContentsFileDetail getFileDetail(String s) {
	return fileHash.get(s);
    }

    /**
     * Get the details of a particular package.
     *
     * @param pkgname the package name to return details of
     *
     * @return the corresponding ContentsPackage
     */
    public ContentsPackage getPackage(String pkgname) {
	return pkgHash.get(pkgname);
    }

    /**
     * Get the details of a particular overlay.
     *
     * @param ovl the overlay name to return details of
     *
     * @return the corresponding ContentsPackage
     */
    public ContentsPackage getOverlay(Overlay ovl) {
	return new ContentsPackage(ovl, this);
    }
}
