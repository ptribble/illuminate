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

/**
 * Zsnapshot - represent a ZFS snapshot.
 * @author Peter Tribble
 * @version 1.0
 */
public class Zsnapshot extends Zdataset {

    private String snapname;

    /**
     * Create a new Zsnapshot object, to store details of a ZFS snapshot.
     *
     * @param name the name of the ZFS snapshot
     */
    public Zsnapshot(final String name) {
	snapname = name;
    }
}
