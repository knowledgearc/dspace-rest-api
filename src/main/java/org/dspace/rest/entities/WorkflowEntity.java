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
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.content.Item;
import org.dspace.content.ItemIterator;
import org.dspace.core.ConfigurationManager;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.eperson.Group;
import org.dspace.rest.util.UserRequestParams;
import org.dspace.workflow.WorkflowItem;
import org.dspace.workflow.WorkflowManager;
import org.sakaiproject.entitybus.EntityReference;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityFieldRequired;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityId;
import org.sakaiproject.entitybus.exception.EntityException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Entity describing workflow
 * @author Lewis
 */
public class WorkflowEntity {

    private int countItems;
    List<Object> workflowItems = new ArrayList<Object>();

    public WorkflowEntity(String uid, Context context, int level, UserRequestParams uparams) {
        System.out.println("creating collection main");
    }

    public WorkflowEntity(WorkflowItem workflowItem, int level, UserRequestParams uparams) throws SQLException {
    }

    public WorkflowEntity() {
    }

    public String submitAccept(EntityReference ref, Map<String, Object> inputVar, Context context) {
        try {
            Integer elid = Integer.parseInt((String) inputVar.get("id"));
            WorkflowItem workflowItem = WorkflowItem.find(context, elid);
            WorkflowManager.claim(context, workflowItem, context.getCurrentUser());
            context.complete();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        } catch (IOException e) {
            throw new EntityException("Internal server error", "IO error", 500);
        }
        return Boolean.toString(true);
    }

    public String submitReturnPool(EntityReference ref, Map<String, Object> inputVar, Context context) {
        try {
            Integer elid = Integer.parseInt((String) inputVar.get("id"));
            WorkflowItem workflowItem = WorkflowItem.find(context, elid);
            WorkflowManager.unclaim(context, workflowItem, context.getCurrentUser());
            context.complete();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        } catch (IOException e) {
            throw new EntityException("Internal server error", "IO error", 500);
        }
        return Boolean.toString(true);
    }

    public String submitApprove(EntityReference ref, Map<String, Object> inputVar, Context context) {
        try {
            Integer elid = Integer.parseInt((String) inputVar.get("id"));
            WorkflowItem workflowItem = WorkflowItem.find(context, elid);
            WorkflowManager.advance(context, workflowItem, context.getCurrentUser());
            context.complete();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        } catch (IOException e) {
            throw new EntityException("Internal server error", "IO error", 500);
        }
        return Boolean.toString(true);
    }

    public String submitReject(EntityReference ref, Map<String, Object> inputVar, Context context) {
        try {
            Integer elid = Integer.parseInt((String) inputVar.get("id"));
            String reason = (String) inputVar.get("reason");
            WorkflowItem workflowItem = WorkflowItem.find(context, elid);
            WorkflowManager.reject(context, workflowItem, context.getCurrentUser(),reason);
            context.complete();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        } catch (IOException e) {
            throw new EntityException("Internal server error", "IO error", 500);
        }
        return Boolean.toString(true);
    }

    public int getCountItems() {
        return countItems;
    }

    public void setCountItems(int countItems) {
        this.countItems = countItems;
    }

    public List<Object> getWorkflowItems() {
        return workflowItems;
    }

    public void setWorkflowItems(List<Object> workflowItems) {
        this.workflowItems = workflowItems;
    }

    @Override
    public String toString() {
        return "";
//        return "workflow id:" + this.id + ", stuff.....";
    }
}
