/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.entities;

import org.dspace.content.Item;
import org.dspace.content.MetadataField;
import org.dspace.content.MetadataSchema;
import org.dspace.content.MetadataValue;
import org.dspace.core.Context;
import org.dspace.rest.content.ContentHelper;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityFieldRequired;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ItemEntityTrim extends ItemEntityId {

    @EntityFieldRequired
    private String name;
    private String handle;
    private List<Object> metadata = new ArrayList<Object>();
    private Object submitter;
    private Date lastModified;
    private boolean isArchived, isWithdrawn;

    public ItemEntityTrim() {
    }

    public ItemEntityTrim(String uid, Context context) throws SQLException {
        super(uid, context);

        this.handle = res.getHandle();
        this.name = res.getName();
        this.lastModified = res.getLastModified();
        this.isArchived = res.isArchived();
        this.isWithdrawn = res.isWithdrawn();
        this.submitter = new UserEntityTrim(res.getSubmitter());

        MetadataValue[] mdValues = ContentHelper.retrieveMetadata(context, res.getID());
        for (MetadataValue mdValue : mdValues) {
            MetadataField mdField = MetadataField.find(context, mdValue.getFieldId());
            MetadataSchema mdSchema = MetadataSchema.find(context, mdField.getSchemaID());
            this.metadata.add(new MetadataEntity(mdValue, mdField, mdSchema));
        }
    }

    public ItemEntityTrim(Item item, Context context) throws SQLException {
        super(item);
        this.handle = item.getHandle();
        this.name = item.getName();
        this.lastModified = item.getLastModified();
        this.isArchived = item.isArchived();
        this.isWithdrawn = item.isWithdrawn();
        this.submitter = new UserEntityTrim(item.getSubmitter());

        MetadataValue[] mdValues = ContentHelper.retrieveMetadata(context,item.getID());
        for (MetadataValue mdValue : mdValues) {
            MetadataField mdField = MetadataField.find(context, mdValue.getFieldId());
            MetadataSchema mdSchema = MetadataSchema.find(context, mdField.getSchemaID());
            this.metadata.add(new MetadataEntity(mdValue, mdField, mdSchema));
        }
    }

    public List getMetadata() {
        return this.metadata;
    }

    public boolean getIsArchived() {
        return this.isArchived;
    }

    public boolean getIsWithdrawn() {
        return this.isWithdrawn;
    }

    public Date getLastModified() {
        return this.lastModified;
    }

    public String getName() {
        return this.name;
    }

    public String getHandle() {
        return this.handle;
    }

    public Object getSubmitter() {
        return submitter;
    }
}