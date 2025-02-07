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

import javax.swing.JPanel;
import java.awt.BorderLayout;
import org.tribblix.illuminate.InfoCommand;
import uk.co.petertribble.jingle.JingleTextPane;

/**
 * SysCmdPanel - shows the output from a command.
 * @author Peter Tribble
 * @version 1.0
 */
public class SysCmdPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    /**
     * Create a SysCmdPanel that shows the output from an InfoCommand.
     *
     * @param ic The InfoCommand to show the output of
     */
    public SysCmdPanel(InfoCommand ic) {
	this(ic.getOutput());
    }

    /**
     * Create a SysCmdPanel that shows the given text.
     *
     * @param s The String to display
     */
    public SysCmdPanel(String s) {
	setLayout(new BorderLayout());
	JingleTextPane tp = new JingleTextPane("text/plain");
	add(tp);
	tp.setText(s);
    }
}
