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

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import org.tribblix.illuminate.helpers.ManFrame;
import uk.co.petertribble.jingle.JingleTextPane;

/**
 * An informational panel, containing a list of items for which information
 * can be shown on the left and a panel showing the informational output on
 * the right.
 */
public class InfoCommandPanel extends JPanel implements ActionListener {

    /**
     * The JingleTextPane where we send output.
     */
    private JingleTextPane tp;
    /**
     * A label for the current command.
     */
    private JLabel jcl;
    /**
     * A JButton to display the manual page for the current command.
     */
    private JButton jmb;
    /**
     * Track the current command we're displaying.
     */
    private InfoCommand currentCmd;

    /**
     * Display an information panel.
     */
    public InfoCommandPanel() {
	setLayout(new BorderLayout());

	JList <InfoCommand> ilist = new InfoJList(new InfoCommandList());
	ilist.addMouseListener(mouseListener);
	ilist.addKeyListener(keyListener);

	JPanel jp = new JPanel(new BorderLayout());
	JToolBar jtb = new JToolBar();
	jtb.setFloatable(false);
	jtb.setRollover(true);
	jtb.setLayout(new BorderLayout());
	jcl = new JLabel(IlluminateResources.getString("INFO.OUTPUT.TEXT"));
	jtb.add(jcl, BorderLayout.LINE_START);
	jtb.addSeparator();
	jmb = new JButton(IlluminateResources.getString("ILLUMINATE.MAN.TEXT"));
	jmb.setEnabled(false);
	jmb.addActionListener(this);
	jtb.add(jmb, BorderLayout.LINE_END);
	jp.add(jtb, BorderLayout.PAGE_START);
	tp = new JingleTextPane();
	jp.add(new JScrollPane(tp), BorderLayout.CENTER);

	JSplitPane psplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
		new JScrollPane(ilist), jp);
	psplit.setOneTouchExpandable(true);
	psplit.setDividerLocation(150);
	add(psplit);
    }

    void setInfo(InfoCommand ic) {
	currentCmd = ic;
	jcl.setText(ic.infoLabel());
	jmb.setEnabled(ic.getManpage() != null);
	Cursor c = getCursor();
	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	tp.setText("<pre>" + ic.getOutput() + "</pre>");
	setCursor(c);
    }

    private void showMan() {
	if (currentCmd != null && currentCmd.getManpage() != null) {
	    new ManFrame(currentCmd.getManpage());
	}
    }

    /**
     * A MouseListener so that clicking on a command in the menu will show its
     * output.
     */
    MouseListener mouseListener = new MouseAdapter() {
	@Override
	public void mouseClicked(MouseEvent e) {
	    @SuppressWarnings("unchecked")
	    JList <InfoCommand> source = (JList <InfoCommand>) e.getSource();
	    setInfo(source.getSelectedValue());
	}
    };

    /**
     * A KeyListener so that selecting a command in the menu will show its
     * output.
     */
    KeyListener keyListener = new KeyAdapter() {
	@Override
	public void keyPressed(KeyEvent e) {
	    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
		@SuppressWarnings("unchecked")
		JList <InfoCommand> source =
			(JList <InfoCommand>) e.getSource();
		setInfo(source.getSelectedValue());
	    }
	}
    };

    @Override
    public void actionPerformed(ActionEvent e) {
	if (e.getSource() == jmb) {
	    showMan();
	}
    }
}
