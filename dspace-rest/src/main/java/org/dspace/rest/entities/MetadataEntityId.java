/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.entities;

import org.dspace.content.MetadataValue;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityId;

public class MetadataEntityId {

    @EntityId
    private int id;

    protected MetadataEntityId(MetadataValue mdValue) {
        this.id = mdValue.getValueId();
    }

    public int getId() {
        return this.id;
    }

    @Override
    public String toString() {
        return "id:" + this.id;
    }
}
