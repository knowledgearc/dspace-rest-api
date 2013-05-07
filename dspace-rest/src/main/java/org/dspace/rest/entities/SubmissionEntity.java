/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.rest.entities;

import org.dspace.content.WorkspaceItem;
import org.dspace.core.Context;
import org.dspace.rest.content.ContentHelper;
import org.dspace.rest.util.UserRequestParams;
import org.sakaiproject.entitybus.EntityReference;
import org.sakaiproject.entitybus.exception.EntityException;

import java.sql.SQLException;

public class SubmissionEntity {

    private Object item;
    private Object collection;

    public SubmissionEntity() {
    }

    public SubmissionEntity(WorkspaceItem res, Context context) {
        try {
//            Item item = res.getItem();
//            AuthorizeManager.authorizeAction(context, item, Constants.READ);

            this.item = new ItemEntityTrim(res.getItem(), context);
            this.collection = new CollectionEntityTrimC(res.getCollection());
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
//        } catch (AuthorizeException ex) {
//            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
    }

    public Object getCount(EntityReference ref, UserRequestParams uparams, Context context) {
        try {
            return ContentHelper.countItemsSubmission(context);
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }
    }

    public Object getItem() {
        return item;
    }

    public Object getCollection() {
        return collection;
    }
}