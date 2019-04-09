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
import java.awt.BorderLayout;
import org.tribblix.illuminate.InfoCommand;
import uk.co.petertribble.jingle.JingleTextPane;

/**
 * SysCmdPanel - shows the output from a command.
 * @author Peter Tribble
 * @version 1.0
 */
public class SysCmdPanel extends JPanel {

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
