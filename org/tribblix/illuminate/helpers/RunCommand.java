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

import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 * RunCommand - run a command and return the output.
 * @author Peter Tribble
 * @version 1.0
 */
public final class RunCommand {

    private StringBuilder stdout; //NOPMD
    private StringBuilder stderr; //NOPMD

    /**
     * Run a command and record its output.
     *
     * @param cmd The command to run
     */
    public RunCommand(String cmd) {
	stdout = new StringBuilder();
	stderr = new StringBuilder();
	try {
	    Process p = Runtime.getRuntime().exec(cmd, (String[]) null,
						new File("/tmp"));

	    try (BufferedReader reader1 =
		new BufferedReader(new InputStreamReader(p.getInputStream()));
		    BufferedReader reader2 =
		new BufferedReader(new InputStreamReader(p.getErrorStream()))) {

		String s;
		while ((s = reader1.readLine()) != null) {
		    stdout.append(s).append('\n');
		}
		while ((s = reader2.readLine()) != null) {
		    stderr.append(s).append('\n');
		}
		try {
		    p.waitFor();
		} catch (InterruptedException ie) {}
	    } catch (IOException ioe1) {}
	} catch (IOException ioe) {}
    }

    /**
     * Returns the standard output.
     *
     * @return The contents of stdout
     */
    public String getOut() {
	return stdout.toString();
    }

    /**
     * Returns the standard error.
     *
     * @return The contents of stderr
     */
    public String getErr() {
	return stderr.toString();
    }
}
