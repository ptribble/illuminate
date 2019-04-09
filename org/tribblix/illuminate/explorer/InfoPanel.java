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

    /**
     * The SysItem defining what is displayed in the InfoPanel.
     */
    protected SysItem hi;

    /**
     * The panel into which the item display is placed.
     */
    protected JingleVPanel jvp;

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
     * Stop the panel updating.
     */
    public void stopLoop() {
    }

    /**
     * Add the output from a command in a scrollable text window.
     *
     * @param ic The InfoCommand to display the output of
     */
    protected void addText(InfoCommand ic) {
	if (ic.exists()) {
	    jvp.add(new JScrollPane(new SysCmdPanel(ic)));
	}
    }

    /**
     * Add the output from a command in a scrollable table.
     *
     * @param ct The CommandTableModel to display the output of
     */
    protected void addText(CommandTableModel ct) {
	jvp.add(new JScrollPane(new JTable(ct)));
    }

    /**
     * Add the output from a command in a scrollable text window.
     *
     * @param s The text to display
     */
    protected void addText(String s) {
	jvp.add(new JScrollPane(new SysCmdPanel(s)));
    }
}
