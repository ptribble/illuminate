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
import uk.co.petertribble.jkstat.api.Kstat;
import uk.co.petertribble.jkstat.api.KstatAggregate;
import uk.co.petertribble.jkstat.gui.KstatAccessoryPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.SwingConstants;

/**
 * An accessory panel that graphically represents aggregate cpu activity in the
 * style of xcpustate.
 *
 * @author Peter Tribble
 * @version 1.0
 */
public final class AggregateCpuPanel extends KstatAccessoryPanel {

    private static final long serialVersionUID = 1L;

    private transient KstatAggregate ksa;
    /**
     * Saved user time.
     */
    private long luser;
    /**
     * Saved system time.
     */
    private long lsys;
    /**
     * Saved wait time.
     */
    private long lwait;
    /**
     * Saved idle time.
     */
    private long lidle;
    /**
     * Change in user time.
     */
    private double duser;
    /**
     * Change in system time.
     */
    private double dsys;
    /**
     * Change in wait time.
     */
    private double dwait;
    /**
     * Change in idle time.
     */
    private double didle;

    /**
     * Current display orientation.
     */
    private int orientation;

    /**
     * Create a panel showing the cpu activity.
     *
     * @param ksa  a KstatAggregate containing aggregate cpu statistics
     * @param interval  the update interval in seconds
     * @param jkstat  a JKstat
     */
    public AggregateCpuPanel(KstatAggregate ksa, int interval, JKstat jkstat) {
	this(ksa, interval, jkstat, SwingConstants.HORIZONTAL);
    }

    /**
     * Create a panel showing the cpu activity.
     *
     * @param ksa  a KstatAggregate containing aggregate cpu statistics
     * @param interval  the update interval in seconds
     * @param jkstat  a JKstat
     * @param orientation The desired orientation of the accessory, which
     * should be either SwingConstants.VERTICAL or SwingConstants.HORIZONTAL
     */
    public AggregateCpuPanel(KstatAggregate ksa, int interval, JKstat jkstat,
			int orientation) {
	super(new Kstat("", 0, ""), interval, jkstat);
	this.orientation = orientation;

	this.ksa = ksa;

	if (orientation == SwingConstants.VERTICAL) {
	    setMinimumSize(new Dimension(12, 48));
	    setPreferredSize(new Dimension(12, 48));
	} else {
	    setMinimumSize(new Dimension(64, 32));
	    setPreferredSize(new Dimension(64, 32));
	}

	updateAccessory();

	// kick a timer into life
	startLoop();
    }

    @Override
    public void updateAccessory() {
	ksa.read();
	long nuser = ksa.aggregate("user");
	long nsys = ksa.aggregate("kernel");
	long nwait = ksa.aggregate("wait");
	long nidle = ksa.aggregate("idle");

	duser = nuser - luser;
	dsys = nsys - lsys;
	dwait = nwait - lwait;
	didle = nidle - lidle;

	repaint();

	luser = nuser;
	lsys = nsys;
	lwait = nwait;
	lidle = nidle;
    }

    @Override
    public void paint(Graphics g) {
	Graphics2D g2 = (Graphics2D) g;
	Dimension d = getSize();

	double h = d.height;
	double w = d.width;
	double x = 0.0d;
	double dscale = ((orientation == SwingConstants.VERTICAL) ? h : w)
	    / (duser + dsys + dwait + didle);
	double dx = dscale * didle;
	g2.setPaint(Color.BLUE);
	if (orientation == SwingConstants.VERTICAL) {
	    g2.fill(new Rectangle2D.Double(0.0d, x, w, dx));
	} else {
	    g2.fill(new Rectangle2D.Double(x, 0.0d, dx, h));
	}
	x += dx;
	dx = dscale * dwait;
	g2.setPaint(Color.RED);
	if (orientation == SwingConstants.VERTICAL) {
	    g2.fill(new Rectangle2D.Double(0.0d, x, w, dx));
	} else {
	    g2.fill(new Rectangle2D.Double(x, 0.0d, dx, h));
	}
	x += dx;
	dx = dscale * duser;
	g2.setPaint(Color.GREEN);
	if (orientation == SwingConstants.VERTICAL) {
	    g2.fill(new Rectangle2D.Double(0.0d, x, w, dx));
	} else {
	    g2.fill(new Rectangle2D.Double(x, 0.0d, dx, h));
	}
	x += dx;
	dx = dscale * dsys;
	g2.setPaint(Color.YELLOW);
	if (orientation == SwingConstants.VERTICAL) {
	    g2.fill(new Rectangle2D.Double(0.0d, x, w, dx));
	} else {
	    g2.fill(new Rectangle2D.Double(x, 0.0d, dx, h));
	}
    }
}
