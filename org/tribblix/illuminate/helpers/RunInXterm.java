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

package org.tribblix.illuminate.helpers;

import java.io.File;
import java.io.IOException;

/**
 * RunInXterm - run the given command in an xterm.
 * @author Peter Tribble
 * @version 1.1
 */
public final class RunInXterm {

    private static String xtermbin;
    /*
     * We search all the following locations for a terminal emulator. It
     * must support the -e option. If none of them work, we fall back to
     * an unqualified xterm and cross our fingers.
     */
    static {
	String[] xsearch = {"/usr/bin/sakura",
			    "/usr/bin/Terminal",
			    "/usr/bin/xterm",
			    "/usr/bin/urxvt",
			    "/usr/bin/gnome-terminal",
			    "/usr/dt/bin/dtterm"};

	for (String s : xsearch) {
	    if (new File(s).exists()) {
		xtermbin = s;
		break;
	    }
	    if (xtermbin == null) {
		xtermbin = "xterm";
	    }
	}
    }

    /**
     * Run a command in an xterm window.
     *
     * @param cmd The command to run
     */
    public RunInXterm(String cmd) {
	try {
	    String[] fullcmd = {xtermbin, "-e", cmd};
	    Runtime.getRuntime().exec(fullcmd);
	} catch (IOException ioe) { }
    }
}
