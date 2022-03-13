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

package org.tribblix.illuminate.explorer;

import java.util.Set;
import java.util.HashSet;
import org.tribblix.illuminate.InfoCommand;

/**
 * ZFSconfig - represent the configuration of ZFS.
 * @author Peter Tribble
 * @version 1.0
 */
public final class ZFSconfig {
    private static final ZFSconfig INSTANCE = new ZFSconfig();

    private Set <Zpool> zpools;

    private ZFSconfig() {
	zpools = new HashSet <Zpool> ();
	parseZpool();
    }

    /**
     * Get the singleton ZFSconfig instance.
     *
     * @return the singleton ZFSconfig instance
     */
    public static ZFSconfig getInstance() {
	return INSTANCE;
    }

    /*
     * Parse zpool output. Populates the list of pools and gives some
     * information about each one.
     */
    private void parseZpool() {
	InfoCommand ic = new InfoCommand("ZL", "/usr/sbin/zpool", "list -H");
	if (ic.exists()) {
	    for (String line : ic.getOutputLines()) {
		String[] ds = line.split("\\s+");
		if (ds.length > 4) {
		    zpools.add(new Zpool(ds[0]));
		}
	    }
	}
    }

    /**
     * Return the set of available pools.
     *
     * @return a Set of all ZFS pools
     */
    public Set <Zpool> pools() {
	return zpools;
    }
}
