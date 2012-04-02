/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.rest.entities;

import org.dspace.core.Context;
import org.dspace.workflow.WorkflowItem;
import org.sakaiproject.entitybus.exception.EntityException;

import java.sql.SQLException;

public class WorkflowEntityId {

    private int id;
    protected WorkflowItem res;

    public WorkflowEntityId() {
    }

    public WorkflowEntityId(String uid, Context context) {
        try {
            res = WorkflowItem.find(context, Integer.parseInt(uid));
//            Item item = res.getItem();
            // Check authorisation
//            AuthorizeManager.authorizeAction(context, item, Constants.READ);

            this.id = res.getID();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
//        } catch (AuthorizeException ex) {
//            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
    }

    public WorkflowEntityId(WorkflowItem res) {
        this.id = res.getID();
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "id:" + this.id;
    }
}