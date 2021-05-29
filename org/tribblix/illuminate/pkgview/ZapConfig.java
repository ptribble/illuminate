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
import java.util.*;
import uk.co.petertribble.jumble.JumbleFile;

/**
 * ZapConfig - describe zap configuration.
 * @author Peter Tribble
 * @version 1.0
 */
public class ZapConfig {

    public static final String ZAP_ROOT = "/etc/zap";

    private boolean zapexists;

    private Map <Integer, ZapRepository> repoMap;
    private Map <String, CatalogParser> catalogMap;

    /**
     * Create a zap configuration.
     *
     * @param altroot  An alternate root directory for this OS image
     */
    public ZapConfig(String altroot) {
	File zaprootf = new File(altroot + ZAP_ROOT);
	zapexists = zaprootf.exists();

	// create the repo list
	if (zapexists) {
	    repoMap = new TreeMap <Integer, ZapRepository> ();
	    catalogMap = new HashMap <String, CatalogParser> (8);
	    File rf = new File(zaprootf, "repo.list");
	    for (String line : JumbleFile.getLines(rf)) {
		String[] ds = line.split(" ", 2);
		repoMap.put(Integer.parseInt(ds[0]),
					new ZapRepository(altroot, ds[1]));
		catalogMap.put(ds[1], new CatalogParser(altroot, ds[1]));
	    }
	}
    }

    public Map getRepos() {
	return repoMap;
    }

    /**
     * Returns a CatalogPackage for the available package, as listed
     * in the catalogs.
     *
     * @param name  The name of the desired package
     * @return  The CatalogPackage for the desired package, or null if the
     * package cannot be found
     */
    public CatalogPackage getPackage(String name) {
	for (ZapRepository zr : repoMap.values()) {
	    CatalogPackage cp = catalogMap.get(zr.getName()).getPackage(name);
	    if (cp != null) {
		return cp;
	    }
	}
	return null;
    }

    /**
     * Returns the currently available version of a package.
     *
     * @param name  The name of the desired package
     * @return  The currently available version of the desired package
     *
     */
    public String currentVersion(String name) {
	CatalogPackage cp = getPackage(name);
	return cp == null ? null : cp.getVersion();
    }

    /**
     * Return whether zap exists in the current system.
     *
     * @return true if the current system uses zap
     */
    public boolean exists() {
	return zapexists;
    }
}
