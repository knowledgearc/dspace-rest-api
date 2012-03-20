/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.rest.entities;

import org.dspace.content.Collection;
import org.dspace.core.Context;
import org.dspace.rest.util.UserRequestParams;

import java.sql.SQLException;

public class CollectionEntity extends CollectionEntityTrim{

    private String licence;
    private String short_description, introductory_text, copyright_text, side_bar_text;
    private String provenance;

    public CollectionEntity(String uid, Context context, int level, UserRequestParams uparams) {
    }

    public CollectionEntity(Collection collection, int level, UserRequestParams uparams) throws SQLException {
    }

    public CollectionEntity(Collection collection, UserRequestParams uparams) throws SQLException {

        super(collection,uparams);
        this.licence = collection.getLicense();
        this.short_description = collection.getMetadata("short_description");
        this.introductory_text = collection.getMetadata("introductory_text");
        this.copyright_text = collection.getMetadata("copyright_text");
        this.side_bar_text = collection.getMetadata("side_bar_text");
        this.provenance = collection.getMetadata("provenance_description");
    }

    public CollectionEntity() {
    }

    public String getLicence() {
        return this.licence;
    }

    public String getShortDescription() {
        return this.short_description;
    }

    public String getCopyrightText() {
        return this.copyright_text;
    }

    public String getSidebarText() {
        return this.side_bar_text;
    }

    public String getIntroductoryText() {
        return this.introductory_text;
    }

    public String getProvenance() {
        return this.provenance;
    }
}