/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.rest.entities;

import org.dspace.authorize.AuthorizeException;
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

public class WorkflowEntity extends WorkflowEntityId {

    private int state;
    private Object item;
    private Object reviewer;
    private Object collection;

    public WorkflowEntity() {
    }

    public WorkflowEntity(String uid, Context context) {
        super(uid, context);
        try {
            this.state = res.getState();
            this.item = new ItemEntityTrim(res.getItem(), context);
            if (res.getOwner() != null) {
                this.reviewer = new UserEntityTrim(res.getOwner());
            }
            this.collection = new CollectionEntityTrimC(res.getCollection());
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }
    }

    public WorkflowEntity(WorkflowItem res, Context context) {
        super(res);
        try {
            this.state = res.getState();
            this.item = new ItemEntityTrim(res.getItem(), context);
            if (res.getOwner() != null) {
                this.reviewer = new UserEntityTrim(res.getOwner());
            }
            this.collection = new CollectionEntityTrimC(res.getCollection());
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }
    }

    public Object getCount(EntityReference ref, UserRequestParams uparams, Context context) {
        try {
            return ContentHelper.countItemsWorkflow(context, uparams.getReviewer(), uparams.getSubmitter(), uparams.getFields(), uparams.getStatus());
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }
    }

    public Object getSubmittersCount(EntityReference ref, UserRequestParams uparams, Context context) {
        try {
            return ContentHelper.countItemsSubmitters(context, uparams.getQuery());
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
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
            return entities;
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

    public int getState() {
        return state;
    }

    public Object getItem() {
        return item;
    }

    public Object getReviewer() {
        return reviewer;
    }

    public Object getCollection() {
        return collection;
    }
}