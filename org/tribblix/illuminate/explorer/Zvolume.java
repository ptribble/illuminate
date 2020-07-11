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
 * Zvolume - represent a ZFS volume.
 * @author Peter Tribble
 * @version 1.0
 */
public class Zvolume extends Zdataset {

    private String shortname;
    private Set <Zsnapshot> snapshots;

    /**
     * Create a new Zvolume object, to store details of a ZFS volume
     *
     * @param name the name of the ZFS volume
     */
    public Zvolume(String name) {
	this.name = name;
	int j = name.lastIndexOf('/');
	if (j >= 0) {
	    shortname = name.substring(j+1);
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
    public Set <Zsnapshot> snapshots() {
	if (snapshots == null) {
	    snapshots = new HashSet <Zsnapshot> ();
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
