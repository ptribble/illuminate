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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

/**
 * A manual page viewer window.
 */
public final class ManViewer extends JFrame {

    private static final long serialVersionUID = 1L;

    /**
     * Create a new manual page viewer.
     *
     * @param name the name of the manual page to display.
     */
    public ManViewer(final String name) {
	add(new ManPane(name));

	addWindowListener(new WinExit());

	setSize(720, 600);
	setVisible(true);
    }

    class WinExit extends WindowAdapter {
	@Override
	public void windowClosing(final WindowEvent we) {
	    System.exit(0);
	}
    }

    /**
     * Create a new manual page viewer.
     *
     * @param args command line arguments, the name of the manual page to
     * display is taken from the first argument
     */
    public static void main(final String[] args) {
	if (args.length == 1) {
	    new ManViewer(args[0]);
	} else {
	    System.err.println("Usage: ManViewer filename");
	}
    }
}
