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

    private Set<Zpool> zpools = new HashSet<>();

    private ZFSconfig() {
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
    public Set<Zpool> pools() {
	return zpools;
    }
}
