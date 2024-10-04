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

import java.util.ResourceBundle;

/**
 * Manage text resources for pkgview.
 *
 * @author Peter Tribble
 */
public final class PkgResources {

    private static final ResourceBundle PKGRES =
			ResourceBundle.getBundle("properties/pkgview");

    private PkgResources() {
    }

    /**
     * Get a text resource.
     *
     * @param key the key of the String to be looked up
     *
     * @return the text resource matching the specified key
     */
    public static String getString(String key) {
	return PKGRES.getString(key);
    }
}
