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

package org.tribblix.illuminate.explorer;

import uk.co.petertribble.jkstat.api.JKstat;
import uk.co.petertribble.jkstat.api.NativeJKstat;
import uk.co.petertribble.jkstat.demo.JKdemo;

/**
 * Show ZFS ARC (adaptive replacement cache) statistics.
 * @author Peter Tribble
 * @version 1.0
 */
public final class ArcStat extends JKdemo {

    private static final long serialVersionUID = 1L;

    /**
     * The main display panel.
     */
    private ArcStatPanel mainPanel;

    /**
     * Create an ArcStat demo.
     */
    public ArcStat() {
	this(new NativeJKstat(), true);
    }

    /**
     * Create an ArcStat demo.
     *
     * @param jkstat a JKstat object
     * @param standalone if false, indicates that this demo is being called
     * from another application
     */
    public ArcStat(JKstat jkstat, boolean standalone) {
	super("ArcStat", standalone);

	mainPanel = new ArcStatPanel(jkstat, DEFAULT_INTERVAL);
	setContentPane(mainPanel);

	pack();
	setVisible(true);
    }

    /**
     * Start the timer loop, so that the accessory updates itself.
     */
    public void startLoop() {
	mainPanel.startLoop();
    }

    @Override
    public void stopLoop() {
	mainPanel.stopLoop();
    }

    @Override
    public void setDelay(int i) {
	mainPanel.setDelay(i);
    }

    /**
     * Create a standalone ArcStat demo application.
     *
     * @param args Command line arguments, unused
     */
    public static void main(String[] args) {
	new ArcStat();
    }
}
