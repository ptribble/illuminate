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

import java.util.Map;
import java.util.HashMap;
import org.tribblix.illuminate.InfoCommand;

/**
 * BootEnvironments - list and describe boot environments.
 * @author Peter Tribble
 * @version 1.0
 */
public class BootEnvironments {

    protected Map<String, String> forwardmap;
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
    public String getBE(String uuid) {
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
    public String getUUID(String bename) {
	initProperties();
	return forwardmap.get(bename);
    }
}
