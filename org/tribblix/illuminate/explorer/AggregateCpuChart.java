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

import uk.co.petertribble.jkstat.api.JKstat;
import uk.co.petertribble.jkstat.api.Kstat;
import uk.co.petertribble.jkstat.api.KstatAggregate;
import uk.co.petertribble.jkstat.gui.KstatAccessoryPanel;
import uk.co.petertribble.jstripchart.JStripChart;
import uk.co.petertribble.jstripchart.JStackedStripChart2;
import java.awt.BorderLayout;
import java.awt.Color;

/**
 * An accessory panel that graphically charts aggregate cpu utilization.
 *
 * @author Peter Tribble
 * @version 1.0
 */
public final class AggregateCpuChart extends KstatAccessoryPanel {

    private static final long serialVersionUID = 1L;

    private transient KstatAggregate ksa;
    private long luser;
    private long lsys;
    private long lidle;

    private JStackedStripChart2 jsc;

    /**
     * Create a panel showing a strip chart of the aggregate cpu utilization
     * that updates every interval seconds.
     *
     * @param ksa  a KstatAggregate containing aggregate cpu statistics
     * @param interval  the update interval in seconds
     * @param jkstat  a JKstat
     */
    public AggregateCpuChart(KstatAggregate ksa, int interval, JKstat jkstat) {
	super(new Kstat("", 0, ""), interval, jkstat);
	this.ksa = ksa;
	init();
    }

    private void init() {
	setLayout(new BorderLayout());

	jsc = new JStackedStripChart2(150, 64, Color.BLUE, Color.YELLOW,
				Color.GREEN);
	jsc.setStyle(JStripChart.STYLE_SOLID);
	jsc.setMax(1.0d);
	add(jsc);

	updateAccessory();

	// kick a timer into life
	startLoop();
    }

    @Override
    public void updateAccessory() {
	ksa.read();

	long nuser = ksa.aggregate("user");
	long nsys = ksa.aggregate("kernel");
	long nidle = ksa.aggregate("idle");

	double duser = nuser - luser;
	double dsys = nsys - lsys;
	double didle = nidle - lidle;

	double dscale = duser + dsys + didle;

	jsc.add(dsys/dscale, duser/dscale);

	luser = nuser;
	lsys = nsys;
	lidle = nidle;
    }
}
