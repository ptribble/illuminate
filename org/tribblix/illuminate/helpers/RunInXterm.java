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
