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

package org.tribblix.illuminate;

import java.util.*;
import org.tribblix.illuminate.helpers.RunCommand;

/**
 * SmfUtils - shows available SMF services.
 * @author Peter Tribble
 * @version 1.0
 */
public class SmfUtils {

    private Set <SmfService> services;
    private Map <String, SmfService> serviceMap;
    private Map <String, Set <SmfService>> statusMap;

    public SmfUtils() {
	services = new TreeSet <SmfService> ();
	serviceMap = new HashMap <String, SmfService> ();
	statusMap = new LinkedHashMap <String, Set <SmfService>> ();

	RunCommand svcs = new RunCommand("/usr/bin/svcs -aH");
	// parse the svcs output to get the service name and status
	StringTokenizer st = new StringTokenizer(svcs.getOut(), "\n");
	while (st.hasMoreTokens()) {
	    String[] ds = st.nextToken().split("\\s+", 3);
	    SmfService serv = new SmfService(ds[2], ds[0]);
	    serviceMap.put(ds[2], serv);
	    services.add(serv);

	    Set <SmfService> statset = statusMap.get(ds[0]);
	    if (statset == null) {
		statset = new TreeSet <SmfService> ();
		statusMap.put(ds[0], statset);
	    }
	    statset.add(serv);
	}
    }

    /**
     * Get all services.
     *
     * @return the Set of all services
     */
    public Set <SmfService> getServices() {
	return services;
    }

    /**
     * Get a Set of all services in the given status.
     *
     * @param status the status to return services for
     *
     * @return the Set of SmfServices in the given status
     */
    public Set <SmfService> getServices(String status) {
	return statusMap.get(status);
    }
}
