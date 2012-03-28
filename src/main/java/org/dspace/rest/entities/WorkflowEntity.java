/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.rest.entities;

import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Item;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.rest.content.ContentHelper;
import org.dspace.rest.util.UserRequestParams;
import org.dspace.workflow.WorkflowItem;
import org.dspace.workflow.WorkflowManager;
import org.sakaiproject.entitybus.EntityReference;
import org.sakaiproject.entitybus.exception.EntityException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WorkflowEntity {

    private int id;
    private Object item;
    private Object reviewer;

    public WorkflowEntity() {
    }

    public WorkflowEntity(String uid, Context context) {
        try {
            WorkflowItem res = WorkflowItem.find(context, Integer.parseInt(uid));
//            Item item = res.getItem();
            // Check authorisation
//            AuthorizeManager.authorizeAction(context, item, Constants.READ);

            this.id = res.getID();
            this.item = new ItemEntityTrim(res.getItem());
            if (res.getOwner() != null) {
                this.reviewer = new UserEntityTrim(res.getOwner());
            }
//            context.complete();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
//        } catch (AuthorizeException ex) {
//            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
    }

    public WorkflowEntity(WorkflowItem res) {
        try {
            Item item = res.getItem();
//            AuthorizeManager.authorizeAction(context, item, Constants.READ);

            this.id = res.getID();
            this.item = new ItemEntityTrim(res.getItem());
            if (res.getOwner() != null) {
                this.reviewer = new UserEntityTrim(res.getOwner());
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
//        } catch (AuthorizeException ex) {
//            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
    }

    public Object getSubmitters(EntityReference ref, UserRequestParams uparams, Context context) {
        try {
            List<Object> entities = new ArrayList<Object>();
            EPerson[] ePersons = ContentHelper.findAllSubmitters(context, uparams.getQuery(), uparams.getStart(),
                    uparams.getLimit(), uparams.getSort().replaceAll("_", " "));
            for (EPerson e : ePersons) {
                entities.add(new UserEntityTrim(e));
            }
            int count = ContentHelper.countItemsSubmitters(context, uparams.getQuery());
            return new SubmittersEntity(count, entities);
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }
    }

    public void accept(EntityReference ref, Map<String, Object> inputVar, Context context) {
        try {
            Integer id = Integer.parseInt(ref.getId());
            WorkflowItem workflowItem = WorkflowItem.find(context, id);
            WorkflowManager.claim(context, workflowItem, context.getCurrentUser());
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        } catch (IOException e) {
            throw new EntityException("Internal server error", "IO error", 500);
        }
    }

    public void returnToPool(EntityReference ref, Map<String, Object> inputVar, Context context) {
        try {
            Integer id = Integer.parseInt(ref.getId());
            WorkflowItem workflowItem = WorkflowItem.find(context, id);
            WorkflowManager.unclaim(context, workflowItem, context.getCurrentUser());
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        } catch (IOException e) {
            throw new EntityException("Internal server error", "IO error", 500);
        }
    }

    public void approve(EntityReference ref, Map<String, Object> inputVar, Context context) {
        try {
            Integer id = Integer.parseInt(ref.getId());
            WorkflowItem workflowItem = WorkflowItem.find(context, id);
            WorkflowManager.advance(context, workflowItem, context.getCurrentUser());
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        } catch (IOException e) {
            throw new EntityException("Internal server error", "IO error", 500);
        }
    }

    public void reject(EntityReference ref, Map<String, Object> inputVar, Context context) {
        try {
            Integer id = Integer.parseInt(ref.getId());
            String reason = (String) inputVar.get("reason");
            WorkflowItem workflowItem = WorkflowItem.find(context, id);
            WorkflowManager.reject(context, workflowItem, context.getCurrentUser(), reason);
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        } catch (IOException e) {
            throw new EntityException("Internal server error", "IO error", 500);
        }
    }

    public int getId() {
        return id;
    }

    public Object getItem() {
        return item;
    }

    public Object getReviewer() {
        return reviewer;
    }

    @Override
    public String toString() {
        return "id:" + this.id;
    }
}