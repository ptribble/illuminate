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

package org.tribblix.illuminate.explorer;

import uk.co.petertribble.jkstat.api.*;
import uk.co.petertribble.jkstat.gui.KstatAccessoryPanel;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import javax.swing.SwingConstants;

/**
 * An accessory panel that graphically represents aggregate cpu activity in the
 * style of xcpustate.
 *
 * @author Peter Tribble
 * @version 1.0
 */
public class AggregateCpuPanel extends KstatAccessoryPanel {

    private KstatAggregate ksa;
    private long luser;
    private long lsys;
    private long lwait;
    private long lidle;
    private double duser;
    private double dsys;
    private double dwait;
    private double didle;

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
	    /(duser + dsys + dwait + didle);
	double dx = dscale*didle;
	g2.setPaint(Color.BLUE);
	if (orientation == SwingConstants.VERTICAL) {
	    g2.fill(new Rectangle2D.Double(0.0d, x, w, dx));
	} else {
	    g2.fill(new Rectangle2D.Double(x, 0.0d, dx, h));
	}
	x += dx;
	dx = dscale*dwait;
	g2.setPaint(Color.RED);
	if (orientation == SwingConstants.VERTICAL) {
	    g2.fill(new Rectangle2D.Double(0.0d, x, w, dx));
	} else {
	    g2.fill(new Rectangle2D.Double(x, 0.0d, dx, h));
	}
	x += dx;
	dx = dscale*duser;
	g2.setPaint(Color.GREEN);
	if (orientation == SwingConstants.VERTICAL) {
	    g2.fill(new Rectangle2D.Double(0.0d, x, w, dx));
	} else {
	    g2.fill(new Rectangle2D.Double(x, 0.0d, dx, h));
	}
	x += dx;
	dx = dscale*dsys;
	g2.setPaint(Color.YELLOW);
	if (orientation == SwingConstants.VERTICAL) {
	    g2.fill(new Rectangle2D.Double(0.0d, x, w, dx));
	} else {
	    g2.fill(new Rectangle2D.Double(x, 0.0d, dx, h));
	}
    }
}
