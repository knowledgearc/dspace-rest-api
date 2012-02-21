/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.rest.entities;

import org.dspace.authorize.AuthorizeManager;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.rest.util.UserRequestParams;
import org.dspace.workflow.WorkflowItem;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityFieldRequired;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityId;
import org.sakaiproject.entitybus.exception.EntityException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity describing workflow
 * @author Lewis
 */
public class WorkflowItemEntity {

    private int id;
    private ItemEntity itemEntity;
    private UserEntity reviewer;

    public WorkflowItemEntity(String uid, Context context, int level, UserRequestParams uparams) {
        System.out.println("creating collection main");
        try {
            WorkflowItem res = WorkflowItem.find(context, Integer.parseInt(uid));

            this.id = res.getID();
            this.itemEntity = new ItemEntity(String.valueOf(res.getItem().getID()),context,level,uparams);
            if (res.getOwner() != null) {
                this.reviewer = new UserEntity(res.getOwner());
            }

            context.complete();
        } catch (NumberFormatException ex) {
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }

    }

    public WorkflowItemEntity(WorkflowItem res, int level, UserRequestParams uparams) throws SQLException {
        System.out.println("creating collection main 2");
        try {
            this.id = res.getID();
            this.itemEntity = new ItemEntity(res.getItem(),level,uparams);
            if (res.getOwner() != null) {
                this.reviewer = new UserEntity(res.getOwner());
            }

        } catch (NumberFormatException ex) {
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }
    }

    public WorkflowItemEntity() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ItemEntity getItemEntity() {
        return itemEntity;
    }

    public void setItemEntity(ItemEntity itemEntity) {
        this.itemEntity = itemEntity;
    }

    public UserEntity getReviewer() {
        return reviewer;
    }

    public void setReviewer(UserEntity reviewer) {
        this.reviewer = reviewer;
    }

    @Override
    public String toString() {
        return "";
//        return "workflow id:" + this.id + ", stuff.....";
    }
}
