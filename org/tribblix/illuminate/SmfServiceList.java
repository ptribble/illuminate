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

import java.util.Vector;
import org.tribblix.illuminate.helpers.RunCommand;

/**
 * SmfServiceList - a list of all SMF services.
 * @author Peter Tribble
 * @version 1.0
 */
public class SmfServiceList extends Vector <SmfService> {

    /**
     * Create a list of SMF services for the current system.
     */
    public SmfServiceList() {
	RunCommand svcs = new RunCommand("/usr/bin/svcs -aH");
	// parse the svcs output to get the service name and status
	for (String s : svcs.getOut().split("\n")) {
	    String[] ds = s.split("\\s+", 3);
	    add(new SmfService(ds[2], ds[0]));
	}
    }
}
