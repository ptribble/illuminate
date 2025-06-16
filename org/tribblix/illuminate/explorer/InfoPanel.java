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

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.BoxLayout;
import java.awt.BorderLayout;
import uk.co.petertribble.jingle.JingleVPanel;
import org.tribblix.illuminate.InfoCommand;

/**
 * A panel to display system information.
 * @author Peter Tribble
 * @version 1.0
 */
public class InfoPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    /**
     * The SysItem defining what is displayed in the InfoPanel.
     */
    protected transient SysItem hi;

    /**
     * The panel into which the item display is placed.
     */
    private JingleVPanel jvp;

    /**
     * Create an information panel. Subclasses should call super()
     * with the same arguments as the first part of their constructor,
     * and then have code to actually create the panel contents, and
     * then must call startLoop().
     *
     * @param hi The item to display
     */
    public InfoPanel(SysItem hi) {
	this.hi = hi;
	setLayout(new BorderLayout());
	jvp = new JingleVPanel();
	jvp.setLayout(new BoxLayout(jvp, BoxLayout.PAGE_AXIS));
	add(jvp);
    }

    /**
     * Stop the panel updating. Extending classes should override this method
     * to stop updates of any of their component panels.
     */
    public void stopLoop() {
	// implementation here
    }

    /**
     * Add a centered JLabel with the given text.
     *
     * @param text The text to place on the label
     */
    protected void addLabel(String text) {
	JLabel jl = new JLabel(text);
	jl.setAlignmentX(CENTER_ALIGNMENT);
	jvp.add(jl);
    }

    /**
     * Add a JComponent.
     *
     * @param jc The JComponent to add
     */
    protected void addComponent(JComponent jc) {
	jvp.add(jc);
    }

    /**
     * Add a component wrapped in a JScrollPane.
     *
     * @param jc The JComponent to add
     */
    protected void addScrollPane(JComponent jc) {
	jvp.add(new JScrollPane(jc));
    }

    /**
     * Add the output from a command in a scrollable text window.
     *
     * @param ic The InfoCommand to display the output of
     */
    protected void addText(InfoCommand ic) {
	if (ic.exists()) {
	    addScrollPane(new SysCmdPanel(ic));
	}
    }

    /**
     * Add the output from a command in a scrollable table.
     *
     * @param ct The CommandTableModel to display the output of
     */
    protected void addText(CommandTableModel ct) {
	addScrollPane(new JTable(ct));
    }

    /**
     * Add the output from a command in a scrollable text window.
     *
     * @param s The text to display
     */
    protected void addText(String s) {
        addScrollPane(new SysCmdPanel(s));
    }
}
