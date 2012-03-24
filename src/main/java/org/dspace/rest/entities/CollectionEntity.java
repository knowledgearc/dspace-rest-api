/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.rest.entities;

import org.dspace.authorize.AuthorizeException;
import org.dspace.authorize.AuthorizeManager;
import org.dspace.content.Bitstream;
import org.dspace.content.Collection;
import org.dspace.content.Item;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.eperson.Group;
import org.dspace.rest.content.ContentHelper;
import org.dspace.rest.util.UserRequestParams;
import org.sakaiproject.entitybus.EntityReference;
import org.sakaiproject.entitybus.exception.EntityException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CollectionEntity extends CollectionEntityTrim{

    private String licence;
    private String short_description, introductory_text, copyright_text, side_bar_text;
    private String provenance;

    public CollectionEntity() {

    }

    public CollectionEntity(String uid, Context context) throws SQLException {
        super(uid,context);
        this.licence = res.getLicense();
        this.short_description = res.getMetadata("short_description");
        this.introductory_text = res.getMetadata("introductory_text");
        this.copyright_text = res.getMetadata("copyright_text");
        this.side_bar_text = res.getMetadata("side_bar_text");
        this.provenance = res.getMetadata("provenance_description");
    }

    public CollectionEntity(Collection collection) throws SQLException {

        super(collection);
        this.licence = collection.getLicense();
        this.short_description = collection.getMetadata("short_description");
        this.introductory_text = collection.getMetadata("introductory_text");
        this.copyright_text = collection.getMetadata("copyright_text");
        this.side_bar_text = collection.getMetadata("side_bar_text");
        this.provenance = collection.getMetadata("provenance_description");
    }

    public Object getItems(EntityReference ref, UserRequestParams uparams, Context context) {

        try {
            List<Object> entities = new ArrayList<Object>();

            entities.add(ContentHelper.countItemsItem(context, Integer.parseInt(ref.getId())));
            Item[] items = ContentHelper.findAllItem(context,Integer.parseInt(ref.getId()), uparams.getStart(), uparams.getLimit());
            for (Item item : items) {
                entities.add(new ItemEntity(item));
            }
            return entities;
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }
    }

    public Object getRoles(EntityReference ref, UserRequestParams uparams, Context context) {

        try {
            Collection res = Collection.find(context, Integer.parseInt(ref.getId()));
            AuthorizeManager.authorizeAction(context, res, Constants.READ);
            int type = Integer.parseInt(uparams.getType());
            Group role = null;
            switch (type) {
                case 1 : {role = res.getAdministrators(); break;}
                case 2 : {role = res.getSubmitters(); break;}
                case 3 : {role = res.getWorkflowGroup(1); break;}
                case 4 : {role = res.getWorkflowGroup(2); break;}
                case 5 : {role = res.getWorkflowGroup(3); break;}
            }

            if (role != null) {
                return new GroupEntity(role);
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ex) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
        return new GroupEntityTrim();
    }

    public Object getLogo(EntityReference ref, UserRequestParams uparams, Context context) {
        try {
            Collection res = Collection.find(context, Integer.parseInt(ref.getId()));
            AuthorizeManager.authorizeAction(context, res, Constants.READ);
            Bitstream logo = res.getLogo();

            if (logo != null) {
                return new BitstreamEntityId(logo);
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ex) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
        return new BitstreamEntityId();
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