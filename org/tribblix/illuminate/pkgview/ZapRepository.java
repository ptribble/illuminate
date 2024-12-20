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

package org.tribblix.illuminate.pkgview;

/**
 * ZapRepository - describe and query a zap repository.
 * @author Peter Tribble
 * @version 1.0
 */
public class ZapRepository {

    // cannot be final as they're assigned in a loop
    private String repoNAME;
    private String repoDESC;
    private String repoURL;
    private String repoSIGNED;

    /**
     * Create a zap repository configuration.
     *
     * @param pkghdl a PackageHandler for this OS image
     * @param repo  The repository represented by this ZapRepository
     */
    public ZapRepository(PackageHandler pkghdl, String repo) {
	for (String line : pkghdl.getRepository(repo)) {
	    String[] ds = line.split("=", 2);
	    if ("NAME".equals(ds[0])) {
		repoNAME = ds[1];
	    } else if ("DESC".equals(ds[0])) {
		repoDESC = ds[1];
	    } else if ("URL".equals(ds[0])) {
		repoURL = ds[1];
	    } else if ("SIGNED".equals(ds[0])) {
		repoSIGNED = ds[1];
	    }
	}
    }

    /**
     * Get the name of this zap repository.
     *
     * @return the zap repository's name.
     */
    public String getName() {
	return repoNAME;
    }

    /**
     * Get the description of this zap repository.
     *
     * @return the zap repository's description.
     */
    public String getDescription() {
	return repoDESC;
    }

    /**
     * Get the URL of this zap repository.
     *
     * @return the zap repository's URL.
     */
    public String getURL() {
	return repoURL;
    }

    /**
     * Get the signer of packages in this zap repository, if any.
     *
     * @return the signer of packages in this zap repository.
     */
    public String getSIGNED() {
	return repoSIGNED;
    }
}
