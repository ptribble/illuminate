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
import java.util.Map;
import java.util.HashMap;
import org.tribblix.illuminate.InfoCommand;

/**
 * ZoneConfig - represent the list of zones and their configuration
 * @author Peter Tribble
 * @version 1.0
 */
public final class ZoneConfig {
    private static final ZoneConfig INSTANCE = new ZoneConfig();

    private Set <String> zones;
    private Map <String, ZoneEntry> zmap;
    private boolean isglobal;

    private ZoneConfig() {
	zones = new HashSet <String> ();
	zmap = new HashMap <String, ZoneEntry> ();
	parseZones();
    }

    /**
     * Get the singleton ZoneConfig instance.
     *
     * @return the singleton ZoneConfig instance
     */
    public static ZoneConfig getInstance() {
	return INSTANCE;
    }

    /*
     * Get the list of zones, ignore global.
     * Populate the ZoneEntry map;
     */
    private void parseZones() {
	InfoCommand ic =
	    new InfoCommand("ZL", "/usr/sbin/zoneadm", "list -icp");
	if (ic.exists()) {
	    for (String line : ic.getOutputLines()) {
		String[] lentries = line.split(":");
		int izone = 0;
		/*
		 * If the zone is running it has a numeric ID
		 * but if not, it gets "-", catch that and set
		 * the zoneid (which will not be used) to -1
		 */
		try {
		    izone = Integer.parseInt(lentries[0]);
		} catch (NumberFormatException nfe) {
		    izone = -1;
		}
		if ("global".equals(lentries[1])) {
		    isglobal = true;
		} else {
		    zones.add(lentries[1]);
		    zmap.put(lentries[1], new ZoneEntry(
				izone,
				lentries[1],
				lentries[2],
				lentries[3],
				lentries[4],
				lentries[5],
				lentries[6]));
		}
	    }
	}
    }

    /**
     * Return the list of available zones.
     *
     * @return a Set of all zone names
     */
    public Set <String> names() {
	return zones;
    }

    /**
     * Return the number of available zones.
     *
     * @return the number of available (non-global) zones
     */
    public int size() {
	return zones.size();
    }

    /**
     * Return whether we're running in the global zone. If we're in the global
     * zone, then there's a zone called global that is not added to the list
     * of zones.
     *
     * @return true if run in the global zone
     */
    public boolean isGlobal() {
	return isglobal;
    }

    /**
     * Return the ZoneEntry for the given zone
     *
     * @param zname The name of the zone of interest
     *
     * @return the named zone's ZoneEntry
     */
    public ZoneEntry getZoneEntry(String zname) {
	return zmap.get(zname);
    }
}
