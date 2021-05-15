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

package org.tribblix.illuminate.pkgview;

import javax.swing.JEditorPane;
import java.awt.Insets;
import java.awt.Desktop;
import javax.swing.event.*;
import java.io.File;
import java.io.IOException;

/**
 * A Scrollable panel containing Text
 *
 * @author Peter C. Tribble (peter.tribble@gmail.com)
 */
public class PackageTextPane extends JEditorPane implements HyperlinkListener {

    private static String browserExe;
    private static final String[] browsers = { "exo-open", "firefox" };

    /**
     * Create a Scrollable panel containing Text
     */
    public PackageTextPane() {
	this("text/html");
    }

    /**
     * Create a Scrollable panel containing Text
     *
     * @param contentType the desired content type of the panel
     */
    public PackageTextPane(String contentType) {
	super();
	setContentType(contentType);
	addHyperlinkListener(this);
    }

    @Override
    public void setText(String s) {
	super.setText(s);
	setMargin(new Insets(5, 5, 5, 5));
	setCaretPosition(0);
	setEditable(false);
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent ev) {
	if (ev.getEventType() == HyperlinkEvent.EventType.ACTIVATED
		&& Desktop.isDesktopSupported()) {
	    try {
		Desktop.getDesktop().browse(ev.getURL().toURI());
	    } catch (Exception e) {
		try {
		    if (browserExe == null) {
			for (String b : browsers) {
			    File f = new File("/usr/bin", b);
			    if (f.exists()) {
				browserExe = "/usr/bin/" + b;
				break;
			    }
			}
		    }
		    if (browserExe != null) {
			Runtime.getRuntime().exec(new String[]
			    {browserExe, ev.getURL().toString()});
		    }
		} catch (IOException e2) {
			System.out.println(e2);
		}
	    }
	}
    }
}
