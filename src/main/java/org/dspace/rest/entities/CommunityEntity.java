/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.entities;

import org.dspace.content.Community;
import org.dspace.core.Context;
import org.dspace.rest.util.UserRequestParams;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityFieldRequired;

import java.sql.SQLException;

public class CommunityEntity extends CommunityEntityTrim {

    @EntityFieldRequired
    private String short_description, introductory_text, copyright_text, side_bar_text;

    public CommunityEntity() {
    }

    public CommunityEntity(String uid, Context context, int level, UserRequestParams uparams) {
    }

    public CommunityEntity(String uid, Context context, UserRequestParams uparams) throws SQLException {
        super(uid,context,uparams);
        this.short_description = res.getMetadata("short_description");
        this.introductory_text = res.getMetadata("introductory_text");
        this.copyright_text = res.getMetadata("copyright_text");
        this.side_bar_text = res.getMetadata("side_bar_text");
    }

    public CommunityEntity(Community community, int level, UserRequestParams uparams) throws SQLException {
    }

    public CommunityEntity(Community community, UserRequestParams uparams, boolean hasCollections, boolean hasSubCommunities) throws SQLException {

        super(community, uparams, hasCollections, hasSubCommunities);
        this.short_description = community.getMetadata("short_description");
        this.introductory_text = community.getMetadata("introductory_text");
        this.copyright_text = community.getMetadata("copyright_text");
        this.side_bar_text = community.getMetadata("side_bar_text");
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
}