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

package org.tribblix.illuminate.pkgview;

import javax.swing.JEditorPane;
import java.awt.Desktop;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.tribblix.illuminate.helpers.ManFrame;
import org.tribblix.illuminate.helpers.RunBrowser;
import uk.co.petertribble.jingle.JingleUtils;

/**
 * A Scrollable panel containing Text.
 *
 * @author Peter C. Tribble (peter.tribble@gmail.com)
 */
public final class PackageTextPane extends JEditorPane
        implements HyperlinkListener {

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
    public PackageTextPane(final String contentType) {
	super();
	setContentType(contentType);
	addHyperlinkListener(this);
    }

    @Override
    public void setText(final String s) {
	super.setText(s);
	setMargin(JingleUtils.defInsets());
	setCaretPosition(0);
	setEditable(false);
    }

    @Override
    public void hyperlinkUpdate(final HyperlinkEvent ev) {
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
