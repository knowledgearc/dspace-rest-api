/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.entities;

import org.dspace.content.DCValue;

public class MetadataEntity {

    private String element;
    private String qualifier;
    private String schema;
    private String value;

    public MetadataEntity(DCValue dcValue) {
        this.element = dcValue.element;
        this.qualifier = dcValue.qualifier;
        this.schema = dcValue.schema;
        this.value = dcValue.value;

    }

    public String getElement() {
        return this.element;
    }

    public String getQualifier() {
        return qualifier;
    }

    public String getSchema() {
        return schema;
    }

    public String getValue() {
        return value;
    }
}