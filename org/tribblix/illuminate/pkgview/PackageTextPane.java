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
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.tribblix.illuminate.helpers.ManFrame;
import org.tribblix.illuminate.helpers.RunBrowser;

/**
 * A Scrollable panel containing Text.
 *
 * @author Peter C. Tribble (peter.tribble@gmail.com)
 */
public class PackageTextPane extends JEditorPane implements HyperlinkListener {

    private static final long serialVersionUID = 1L;

    /**
     * Create a Scrollable panel containing Text.
     */
    public PackageTextPane() {
	this("text/html");
    }

    /**
     * Create a Scrollable panel containing Text.
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
	if (ev.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
	    /*
	     * Is this a regular URL?
	     */
	    if (ev.getURL() != null && Desktop.isDesktopSupported()) {
		new RunBrowser(ev.getURL());
	    } else {
		/*
		 * try a man page
		 */
		new ManFrame(ev.getDescription());
	    }
	}
    }
}
