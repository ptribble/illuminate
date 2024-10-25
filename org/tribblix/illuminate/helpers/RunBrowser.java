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

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * RunBrowser - open a URL in a browser
 * @author Peter Tribble
 * @version 1.1
 */
public class RunBrowser {

    private static String browserbin;
    /*
     * We search all the following locations for a browser.
     */
    static {
	String[] xsearch = { "/usr/bin/palemoon",
			     "/usr/bin/firefox",
			     "/usr/bin/dillo",
			     "/usr/bin/netsurf",
			     "/usr/bin/xdg-open" };

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
    public RunBrowser(URL url) {
	try {
	    Desktop.getDesktop().browse(url.toURI());
	} catch (Exception e) { //NOPMD
	    try {
		if (browserbin != null) {
		    String[] fullcmd = {browserbin, url.toString()};
		    Runtime.getRuntime().exec(fullcmd);
		}
	    } catch (IOException ioe) {}
	}
    }
}
