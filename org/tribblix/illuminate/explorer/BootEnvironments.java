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

import java.util.Map;
import java.util.HashMap;
import org.tribblix.illuminate.InfoCommand;

/**
 * BootEnvironments - list and describe boot environments.
 * @author Peter Tribble
 * @version 1.0
 */
public class BootEnvironments {

    /**
     * A Map of BEs, from name to uuid.
     */
    protected Map<String, String> forwardmap;
    /**
     * A Map of BEs, from uuid to name.
     */
    protected Map<String, String> backwardmap;

    /*
     * Initialize all the properties if we haven't already done so.
     */
    private void initProperties() {
	if (forwardmap == null) {
	    forwardmap = new HashMap<>();
	    backwardmap = new HashMap<>();
	    InfoCommand ic = new InfoCommand("BE", "/usr/sbin/beadm",
				"list -H");
	    if (ic.exists()) {
		for (String line : ic.getOutputLines()) {
		    String[] ds = line.split(";", 3);
		    // 0 is the be name 1 the uuid
		    forwardmap.put(ds[0], ds[1]);
		    backwardmap.put(ds[1], ds[0]);
		}
	    }
	}
    }

    /**
     * Return the name of the BE with a given uuid.
     *
     * @param uuid the uuid to query for
     *
     * @return the name of the BE corresponding to the given uuid
     */
    public String getBE(final String uuid) {
	initProperties();
	return backwardmap.get(uuid);
    }

    /**
     * Retrieve the value of the specified property.
     *
     * @param bename the name of the BE to query
     *
     * @return the requested property
     */
    public String getUUID(final String bename) {
	initProperties();
	return forwardmap.get(bename);
    }
}
