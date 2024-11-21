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

/**
 * Zproperty - represent a ZFS property.
 * @author Peter Tribble
 * @version 1.0
 */
public class Zproperty implements Comparable<Zproperty> {

    private String property;
    private String value;
    private String source;

    /**
     * Create a new Zproperty object, to store details of a ZFS property.
     *
     * @param property the name of the ZFS property
     * @param value the value of the ZFS property
     * @param source the source of the ZFS property
     */
    public Zproperty(String property, String value, String source) {
	this.property = property;
	this.value = value;
	this.source = source;
    }

    /**
     * Return the name of this property.
     *
     * @return the name of the property described by this Zproperty
     */
    public String getProperty() {
	return property;
    }

    /**
     * Return the value of this property.
     *
     * @return the value of the property described by this Zproperty
     */
    public String getValue() {
	return value;
    }

    /**
     * Return the source of this property.
     *
     * @return the source of the property described by this Zproperty
     */
    public String getSource() {
	return source;
    }

    /**
     * Return the String representation of this ZFS property.
     *
     * @return a summary of this property
     */
    @Override
    public String toString() {
	return property + "|" + value + "|" + source;
    }

    /**
     * Compare this Zproperty to the given Zproperty.
     *
     * @param otherzprop the Zproperty to be compared
     *
     * @return an int representing whether this Zproperty's name is less
     * than, equal to, or greater than the name of the supplied Zproperty
     */
    @Override
    public int compareTo(Zproperty otherzprop) {
	return property.compareTo(otherzprop.getProperty());
    }
}
