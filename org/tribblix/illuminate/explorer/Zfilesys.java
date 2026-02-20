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
 * Copyright 2026 Peter Tribble
 *
 */

package org.tribblix.illuminate.explorer;

import java.util.Set;
import java.util.HashSet;
import org.tribblix.illuminate.InfoCommand;

/**
 * Zfilesys - represent a ZFS filesystem.
 * @author Peter Tribble
 * @version 1.1
 */
public final class Zfilesys extends Zdataset {

    private final String shortname;
    private final Set<Zfilesys> children = new HashSet<>();
    private Set<Zsnapshot> snapshots;

    /**
     * Create a new Zfilesys object, to store details of a ZFS filesystem.
     *
     * @param name the name of the ZFS filesystem
     */
    public Zfilesys(final String name) {
	setName(name);
	int j = name.lastIndexOf('/');
	if (j >= 0) {
	    shortname = name.substring(j + 1);
	} else {
	    shortname = name;
	}
    }

    /**
     * Return the last component of the name of this filesystem.
     *
     * @return the last component of the name of the filesystem described by
     * this Zfilesys
     */
    public String getShortName() {
	return shortname;
    }

    /**
     * Add a child filesystem.
     *
     * @param zfs the child filesystem to add
     */
    public void addChild(final Zfilesys zfs) {
	children.add(zfs);
    }

    /**
     * Get the Set of all child filesystems. Note that this class does not
     * create or manage the dataset relationships, assuming that Zpool does
     * all the work.
     *
     * @return the Set of all child filesystems
     */
    public Set<Zfilesys> getChildren() {
	return children;
    }

    /**
     * Get the Set of all snapshots.
     *
     * @return the Set of all snapshots of this filesystem
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
