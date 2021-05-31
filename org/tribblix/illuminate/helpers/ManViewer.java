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

package org.tribblix.illuminate.helpers;

import java.awt.event.*;
import javax.swing.*;

public class ManViewer extends JFrame {

    public ManViewer(String name) {
	add(new ManPane(name));

	addWindowListener(new WinExit());

	setSize(720, 600);
	setVisible(true);
    }

    class WinExit extends WindowAdapter {
	@Override
	public void windowClosing(WindowEvent we) {
	    System.exit(0);
	}
    }

    public static void main(String args[]) {
	if (args.length == 1) {
	    new ManViewer(args[0]);
	} else {
	    System.err.println("Usage: ManViewer filename");
	}
    }
}
