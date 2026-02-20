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
 * Copyright 2026 Peter Tribble
 *
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

    private final transient KstatAggregate ksa;
    /**
     * Saved user time.
     */
    private long luser;
    /**
     * Saved system time.
     */
    private long lsys;
    /**
     * Saved idle time.
     */
    private long lidle;

    /**
     * Stripchart displaying the aggregate data.
     */
    private JStackedStripChart2 jsc;

    /**
     * Create a panel showing a strip chart of the aggregate cpu utilization
     * that updates every interval seconds.
     *
     * @param ksaggr a KstatAggregate containing aggregate cpu statistics
     * @param interval the update interval in seconds
     * @param jkstat a JKstat
     */
    public AggregateCpuChart(final KstatAggregate ksaggr, final int interval,
			     final JKstat jkstat) {
	super(new Kstat("", 0, ""), interval, jkstat);
	ksa = ksaggr;
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

	jsc.add(dsys / dscale, duser / dscale);

	luser = nuser;
	lsys = nsys;
	lidle = nidle;
    }
}
