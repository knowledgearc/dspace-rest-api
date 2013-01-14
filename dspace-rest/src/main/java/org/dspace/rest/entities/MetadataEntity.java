/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.entities;

import org.dspace.content.MetadataField;
import org.dspace.content.MetadataSchema;
import org.dspace.content.MetadataValue;

public class MetadataEntity extends MetadataEntityId {

    private String element;
    private String qualifier;
    private String schema;
    private String value;

    public MetadataEntity(MetadataValue mdValue, MetadataField mdField, MetadataSchema mdSchema) {
        super(mdValue);
        this.element = mdField.getElement();
        this.qualifier = mdField.getQualifier();
        this.schema = mdSchema.getName();
        this.value = mdValue.getValue();

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