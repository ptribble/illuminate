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

package org.tribblix.illuminate;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.io.File;
import uk.co.petertribble.jingle.JingleTextPane;
import uk.co.petertribble.jproc.api.JProc;
import uk.co.petertribble.jproc.api.JProcessFilter;
import uk.co.petertribble.jproc.gui.JPinfoTable;
import uk.co.petertribble.jumble.JumbleFile;

/**
 * SmfInfoPanel - shows smf status.
 * @author Peter Tribble
 * @version 1.0
 */
public class SmfInfoPanel extends JPanel {

    private JingleTextPane tp;
    private JingleTextPane tpl;
    private JingleTextPane dtp;
    private JTabbedPane jtp;
    private JProc jproc;
    private JProcessFilter jpf;
    private JPinfoTable jpi;
    private File logfile;

    /**
     * Display an smf information panel.
     */
    public SmfInfoPanel() {
	setLayout(new BorderLayout());
	jtp = new JTabbedPane();
	add(jtp);

	jproc = new JProc();
	jpf = new JProcessFilter();
	jpi = new JPinfoTable(jproc, jpf, 1);
	jpi.setContract(0);
	jpi.removeColumn("CT");
	jpi.removeColumn("ZONE");
	jpi.removeColumn("TASK");
	jpi.removeColumn("PROJ");
	jpi.removeColumn("ppid");

	tp = new JingleTextPane();

	JSplitPane jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
		new JScrollPane(tp), new JScrollPane(jpi));
	jsp.setOneTouchExpandable(true);
	jsp.setDividerLocation(180);

	jtp.add(IlluminateResources.getString("SMF.STATUS"), jsp);

	dtp = new JingleTextPane();
	jtp.add(IlluminateResources.getString("SMF.DEPS"),
		new JScrollPane(dtp));

	tpl = new JingleTextPane();
	jtp.add(IlluminateResources.getString("SMF.LOG"), new JScrollPane(tpl));

	/*
	 * Add a change listener so that, when the log tab is
	 * selected, the logfile is automatically displayed.
	 */
	jtp.addChangeListener(new ChangeListener() {
	    public void stateChanged(ChangeEvent ce) {
		JTabbedPane jtpc = (JTabbedPane) ce.getSource();
		if (jtpc.getSelectedIndex() == 2) {
		    displayLog();
		}
	    }
	});
    }

    /**
     * Update the information shown to correspond to the given service.
     *
     * @param svc  The SmfService to show information on.
     */
    public void setInfo(SmfService svc) {
	tp.setText(svc.getHtmlInfo());
	setDep(svc.getDepInfo());
	setLog(svc.getLog());
	// FIXME bug no contract is -1, but the table interprets -1
	// as no filtering
	jpi.setContract(svc.getContract());
    }

    private void setDep(String s) {
	if (s == null) {
	    jtp.setEnabledAt(1, false);
	    jtp.setSelectedIndex(0);
	} else {
	    jtp.setEnabledAt(1, true);
	    dtp.setText(s);
	}
    }

    private void setLog(File f) {
	logfile = f;
	if (f == null) {
	    jtp.setEnabledAt(2, false);
	    jtp.setSelectedIndex(0);
	} else {
	    jtp.setEnabledAt(2, true);
	    if (jtp.getSelectedIndex() == 2) {
		displayLog();
	    }
	}
    }

    private void displayLog() {
	Cursor c = getCursor();
	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	StringBuilder sb = new StringBuilder();
	sb.append("<pre>\n");
	sb.append(JumbleFile.getStringContents(logfile));
	sb.append("</pre>\n");
	tpl.setText(sb.toString());
	setCursor(c);
    }
}
