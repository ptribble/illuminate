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
import java.io.*;
import uk.co.petertribble.jumble.JumbleFile;

/**
 * We parse the catalog file and create a map of catalog entries.
 */
public class CatalogParser {

    private Map <String, CatalogPackage> pkgMap;

    /**
     * Parse a package catalog.
     *
     * @param altroot  An alternate root directory for this OS image
     * @param reponame  The repository represented by this catalog
     */
    public CatalogParser(String altroot, String reponame) {
	/*
	 * the release repo usually has 3 packages
	 * the illumos repo has just under 500
	 * the main repo currently has 1400
	 * size so that we wouldn't normally ever need to rehash
	 */
	int isize = 8;
	if ("illumos".equals(reponame)) {
	    isize = 640;
	} else if ("tribblix".equals(reponame)) {
	    isize = 2000;
	} else if ("oi".equals(reponame)) {
	    isize = 256;
	}
	pkgMap = new HashMap <String, CatalogPackage> (isize);
	parse(altroot, reponame);
    }

    /*
     * The catalog has lines of the form
     * name|version|depends|size|checksum
     */
    private void parse(String altroot, String reponame) {
	for (String s : JumbleFile.getLines(new File(altroot +
		ZapConfig.ZAP_ROOT + "/repositories/" + reponame
		+ ".catalog"))) {
	    String[] ds = s.split("\\|", 5);
	    pkgMap.put(ds[0],
		new CatalogPackage(ds[0], ds[1], ds[2], ds[3], reponame));
	}
    }

    public CatalogPackage getPackage(String name) {
	return pkgMap.get(name);
    }
}
