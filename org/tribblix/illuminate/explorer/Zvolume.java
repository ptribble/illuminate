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
 * Zvolume - represent a ZFS volume.
 * @author Peter Tribble
 * @version 1.0
 */
public class Zvolume extends Zdataset {

    private String shortname;
    private Set<Zsnapshot> snapshots;

    /**
     * Create a new Zvolume object, to store details of a ZFS volume.
     *
     * @param name the name of the ZFS volume
     */
    public Zvolume(final String name) {
	this.name = name;
	int j = name.lastIndexOf('/');
	if (j >= 0) {
	    shortname = name.substring(j + 1);
	} else {
	    shortname = name;
	}
    }

    /**
     * Return the last component of the name of this volume.
     *
     * @return the last component of the name of the volume described by this
     * Zvolume
     */
    public String getShortName() {
	return shortname;
    }

    /**
     * Get the Set of all snapshots.
     *
     * @return the Set of all snapshots of this volume
     */
    public Set<Zsnapshot> getSnapshots() {
	if (snapshots == null) {
	    snapshots = new HashSet<>();
	    InfoCommand ic = new InfoCommand("ZF", "/usr/sbin/zfs",
					"list -H -t snapshot -d 1 -r " + name);
	    if (ic.exists()) {
		for (String line : ic.getOutputLines()) {
		    String[] ds = line.split("\\s+");
		    snapshots.add(new Zsnapshot(ds[0]));
		}
	    }
	}
	return snapshots;
    }
}
