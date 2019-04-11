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

import java.io.File;
import java.util.Map;
import java.util.HashMap;

import org.tribblix.illuminate.helpers.RunCommand;

/**
 * SmfService - describes an SMF service.
 * @author Peter Tribble
 * @version 1.0
 */
public class SmfService implements Comparable<SmfService> {

    /*
     * FIXME refresh service details. Update status. We can't return the status
     * dynamically because the UI calls getStatus() incessantly for cell
     * rendering.
     */

    private String fmri;
    private String status;
    private Map <String, String> props;

    /**
     * Create a new SmfService object.
     *
     * @param fmri the FMRI of this SMF service
     * @param status the current status of this service
     */
    public SmfService(String fmri, String status) {
	this.fmri = fmri;
	this.status = status;
    }

    /**
     * Return the FMRI. (Fault management resource identifier.)
     *
     * @return the fmri of this service.
     */
    public String getFMRI() {
	return fmri;
    }

    /**
     * Return the service's status.
     *
     * @return the status of this service.
     */
    public String getStatus() {
	return status;
    }

    /**
     * Return the service's start time.
     *
     * @return the start time of this service.
     */
    public String getStartTime() {
	return getDetails(2);
    }

    /**
     * Return the numeric ID of the process contract associated with this
     * service. If there is no process contract, return 0.
     *
     * @return the contract id of this service
     */
    public int getContract() {
	try {
	    return Integer.parseInt(getDetails(3));
	} catch (NumberFormatException nfe) {
	    return 0;
	}
    }

    /**
     * Get the explanation of why this service isn't running. This is exactly
     * the output from svcs -xv.
     *
     * @return an explanation of why this service isn't running
     */
    public String getExplanation() {
	return getSVCS("-xv");
    }

    /**
     * Get the list of processes associated with this service. This is exactly
     * the output from svcs -p.
     *
     * @return the list of processes associated with this service
     */
    public String getProcesses() {
	return getSVCS("-p");
    }

    /**
     * Get those services upon which this service depends. This is exactly
     * the output from svcs -dH.
     *
     * @return the services upon which this service depends
     */
    public String getDependencies() {
	return getSVCS("-dH");
    }

    /**
     * Get those services which depend on this service. This is exactly
     * the output from svcs -DH.
     *
     * @return the services which depend on this service
     */
    public String getDependents() {
	return getSVCS("-DH");
    }

    /*
     * Get the i'th field of svcs -vH output. 0=status 1=nstate
     * 2=start time 3=ctid 4=fmri
     */
    private String getDetails(int i) {
	String[] ds = getSVCS("-vH").split("\\s+", 5);
	return ds[i];
    }

    private String getSVCS(String s) {
	RunCommand svcs = new RunCommand("/usr/bin/svcs " + s + " " + fmri);
	return svcs.getOut();
    }

    /**
     * Get the name of this service, for display purposes
     *
     * @return the human readable name of this service
     */
    public String getName() {
	return getProperty("name");
    }

    /**
     * Return the log file for this service, if it exists. If the log
     * file doesn't exist, return null.
     *
     * @return a File referring to the log file for this service, if it
     * exists, else null.
     */
    public File getLog() {
	String logfile = getProperty("logfile");
	if (logfile != null) {
	    File fl = new File(logfile);
	    if (fl.exists()) {
		return fl;
	    }
	}
	return null;
    }

    /*
     * Get the value of the given property. This is based on the output of
     * svcs -l broken into keys and values. This is only valid for unique
     * keys, so you can't get dependency information this way.
     */
    private String getProperty(String s) {
	if (props == null) {
	    props = new HashMap <String, String> ();
	    /*
	     * Properties aren't supported for legacy sevices.
	     */
	    if (!fmri.startsWith("lrc:")) {
		for (String line : getSVCS("-l").split("\n")) {
		    String[] ds = line.split("\\s+", 2);
		    props.put(ds[0], ds[1]);
		}
	    }
	}
	return props.get(s);
    }

    /**
     * For Comparable.
     *
     * @param ss the SmfService to compare with this SmfService
     *
     * @return whether the given SmfService is greater than or less than this
     * SmfService
     */
    public int compareTo(SmfService ss) {
	return fmri.compareTo(ss.getFMRI());
    }
}
