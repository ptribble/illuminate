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

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * RunBrowser - open a URL in a browser.
 * @author Peter Tribble
 * @version 1.1
 */
public final class RunBrowser {

    private static String browserbin;
    /*
     * We search all the following locations for a browser.
     */
    static {
	String[] xsearch = {"/usr/bin/palemoon",
			    "/usr/bin/firefox",
			    "/usr/bin/dillo",
			    "/usr/bin/netsurf",
			    "/usr/bin/xdg-open"};

	for (String s : xsearch) {
	    if (new File(s).exists()) {
		browserbin = s;
		break;
	    }
	}
    }

    /**
     * Open a URL in a browser.
     *
     * @param url The URL to open
     */
    public RunBrowser(final URL url) {
	try {
	    Desktop.getDesktop().browse(url.toURI());
	} catch (Exception e) {
	    try {
		if (browserbin != null) {
		    String[] fullcmd = {browserbin, url.toString()};
		    Runtime.getRuntime().exec(fullcmd);
		}
	    } catch (IOException ioe) { }
	}
    }
}
