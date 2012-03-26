/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.entities;

import org.dspace.content.Comment;
import org.dspace.core.Context;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityFieldRequired;
import org.sakaiproject.entitybus.exception.EntityException;

import java.sql.SQLException;
import java.util.Date;

public class CommentEntityTrim extends CommentEntityId {

    @EntityFieldRequired
    private String subject;
    private String body;
    private Object commenter;
    private Object reviewer;
    private Object approver;
    private Boolean visible;
    private Boolean approved;
    private Boolean deleted;
    private Integer itemID;
    private Date lastModified;

    public CommentEntityTrim() {
    }

    public CommentEntityTrim(String uid, Context context) {

        super(uid, context);
        try {
            this.subject = res.getSubject();
            this.body = res.getBody();
            this.commenter = res.getCommenter() != null ? new UserEntityTrim(res.getCommenter()) : null;
            this.reviewer = res.getReviewer() != null ? new UserEntityTrim(res.getReviewer()) : null;
            this.approver = res.getApprover() != null ? new UserEntityTrim(res.getApprover()) : null;
            this.visible = res.isVisible();
            this.approved = res.isApproved();
            this.deleted = res.isDeleted();
            this.itemID = res.getItemID();
            this.lastModified = res.getLastModified();
//            context.complete();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }
    }

    public CommentEntityTrim(Comment comment) throws SQLException {
        super(comment);
        this.subject = comment.getSubject();
        this.body = comment.getBody();
        this.commenter = comment.getCommenter() != null ? new UserEntityTrim(comment.getCommenter()) : null;
        this.reviewer = comment.getReviewer() != null ? new UserEntityTrim(comment.getReviewer()) : null;
        this.approver = comment.getApprover() != null ? new UserEntityTrim(comment.getApprover()) : null;
        this.visible = comment.isVisible();
        this.approved = comment.isApproved();
        this.deleted = comment.isDeleted();
        this.itemID = comment.getItemID();
        this.lastModified = comment.getLastModified();
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public Object getCommenter() {
        return commenter;
    }

    public Object getReviewer() {
        return reviewer;
    }

    public Object getApprover() {
        return approver;
    }

    public Boolean getVisible() {
        return visible;
    }

    public Boolean getApproved() {
        return approved;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public Integer getItemID() {
        return itemID;
    }

    public Date getLastModified() {
        return lastModified;
    }
}
