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
import org.dspace.content.Comment;
import org.dspace.content.Item;
import org.dspace.core.Constants;
import org.dspace.core.Context;
import org.dspace.eperson.Group;
import org.dspace.rest.util.UserRequestParams;
import org.sakaiproject.entitybus.EntityReference;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityFieldRequired;
import org.sakaiproject.entitybus.entityprovider.annotations.EntityId;
import org.sakaiproject.entitybus.exception.EntityException;

import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

/**
 * @author Lewis
 */
public class CommentEntity extends CommentEntityId {

    @EntityId
    private int id;
    @EntityFieldRequired
    private String subject;
    private String body;
    private UserEntity commenter;
    private UserEntity reviewer;
    private UserEntity approver;
    private Boolean isVisible;
    private Boolean isApproved;
    private Boolean isDeleted;
    private Integer itemID;
    private Integer replyCommentID;
    private Date lastModified;

    public CommentEntity() {
    }

    public CommentEntity(String uid, Context context, int level, UserRequestParams uparams) {
        System.out.println("creating comment main");
        try {
            Comment res = Comment.find(context, Integer.parseInt(uid));
            // Check authorisation
//            AuthorizeManager.authorizeAction(context, res, Constants.READ);

            this.id = res.getID();
            this.subject = res.getSubject();
            this.body = res.getBody();
            this.commenter = res.getCommenter() != null ? new UserEntity(res.getCommenter()) : null;
            this.reviewer = res.getReviewer() != null ? new UserEntity(res.getReviewer()) : null;
            this.approver = res.getApprover() != null ? new UserEntity(res.getApprover()) : null;
            this.isVisible = res.isVisible();
            this.isApproved = res.isApproved();
            this.isDeleted = res.isDeleted();
            this.itemID = res.getItemID();
            this.replyCommentID = res.getReplyCommentID();
            this.lastModified = res.getLastModified();

//            context.complete();
        } catch (NumberFormatException ex) {
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        }
    }

    public CommentEntity(Comment comment) throws SQLException {
        this.id = comment.getID();
        this.subject = comment.getSubject();
        this.body = comment.getBody();
        this.commenter = comment.getCommenter() != null ? new UserEntity(comment.getCommenter()) : null;
        this.reviewer = comment.getReviewer() != null ? new UserEntity(comment.getReviewer()) : null;
        this.approver = comment.getApprover() != null ? new UserEntity(comment.getApprover()) : null;
        this.isVisible = comment.isVisible();
        this.isApproved = comment.isApproved();
        this.isDeleted = comment.isDeleted();
        this.itemID = comment.getItemID();
        this.replyCommentID = comment.getReplyCommentID();
        this.lastModified = comment.getLastModified();
    }

    public String createComment(EntityReference ref, Map<String, Object> inputVar, Context context) {

        try {
            //Check if user null
            if (context.getCurrentUser() == null) {
                throw new EntityException("Forbidden", "Forbidden", 403);
            }
            //Check if user has read item auth
            Integer id = Integer.parseInt((String) inputVar.get("id"));
            Item item = Item.find(context, id);
            AuthorizeManager.authorizeAction(context, item, Constants.READ);
            //Check if commenter group exist and user is in commenter group
            Group commenters = Group.findByName(context, "Create/Edit Comments");
            if (commenters == null || !Group.isMember(context, commenters.getID())) {
                throw new EntityException("Forbidden", "Forbidden", 403);
            }

            Comment comment = Comment.create(context);
            comment.setItemID(id);
            comment.setLastModified(new Date());
            Integer replyCommentID = Integer.parseInt((String) inputVar.get("replyCommentID"));
            if (replyCommentID != null && replyCommentID > 0) {
                Comment replyComment = Comment.find(context, replyCommentID);
                if (replyComment != null) {
                    comment.setItemID(null);
                    comment.setReplyCommentID(replyComment.getID());
                }
            }

            String subject = (String) inputVar.get("subject");
            String body = (String) inputVar.get("body");
            comment.setSubject(subject);
            comment.setBody(body);
            comment.setCommenter(context.getCurrentUser());

            //For policy
            AuthorizeManager.addPolicy(context, comment, Constants.ADMIN, context.getCurrentUser());
            Group reviewers = Group.findByName(context, "Review Comments");
            if (reviewers != null) {
                AuthorizeManager.addPolicy(context, comment, Constants.WRITE, reviewers);
            }
            //Check if the  Review and Approve Comments group existing
            Group approvers = Group.findByName(context, "Review and Approve Comments");
            if (approvers != null) {
                //For policy
                AuthorizeManager.addPolicy(context, comment, Constants.WRITE, approvers);

                comment.setVisible(false);
            } else {
                comment.setVisible(true);
            }

            comment.update();
            context.complete();
            return String.valueOf(comment.getID());

        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
    }

    public String editComment(EntityReference ref, Map<String, Object> inputVar, Context context) {
        try {
            Integer id = Integer.parseInt(ref.getId());
            Comment comment = Comment.find(context, id);
            AuthorizeManager.authorizeAction(context, comment, Constants.ADMIN);
            if (comment.isApproved()) {
                throw new EntityException("Forbidden", "Forbidden", 403);
            }
            String subject = (String) inputVar.get("subject");
            String body = (String) inputVar.get("body");
            comment.setSubject(subject);
            comment.setBody(body);
            if(inputVar.get("deleted")!=null&&!"".equals(inputVar.get("deleted"))){
                Boolean deleted = Boolean.valueOf((String) inputVar.get("deleted"));
                comment.setDeleted(deleted);
            }

            comment.setLastModified(new Date());

            comment.update();
            context.complete();

            return String.valueOf(comment.getID());
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
    }

    public void removeComment(EntityReference ref, Map<String, Object> inputVar, Context context) {
        try {
            Integer id = Integer.parseInt(ref.getId());
            Comment comment = Comment.find(context, id);
            AuthorizeManager.authorizeAction(context, comment, Constants.WRITE);

            if (comment.hasReplyComment(context)) {//has child
                comment.setDeleted(true);
                if (comment.getCommenter().getID() != context.getCurrentUser().getID()) {
                    comment.setReviewer(context.getCurrentUser());
                }
                comment.setLastModified(new Date());
                comment.update();
            } else {
                AuthorizeManager.removeAllPolicies(context, comment);
                comment.delete(context);
            }

            context.complete();

        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
    }

    public String approveComment(EntityReference ref, Map<String, Object> inputVar, Context context) {
        try {

            //Check if the  Review and Approve Comments group existing
            Group approvers = Group.findByName(context, "Review and Approve Comments");
            if (approvers == null || !Group.isMember(context, approvers.getID())) {
                throw new EntityException("Forbidden", "Forbidden", 403);
            }

            Integer id = Integer.parseInt((String) inputVar.get("id"));
            Comment comment = Comment.find(context, id);
            AuthorizeManager.authorizeAction(context, comment, Constants.WRITE);

            Boolean approve = Boolean.valueOf((String) inputVar.get("approved"));
            comment.setVisible(approve);
            comment.setApproved(approve);
            comment.setApprover(context.getCurrentUser());
            comment.setLastModified(new Date());

            comment.update();
            context.complete();

            return String.valueOf(comment.getID());
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
    }

    public int getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public UserEntity getCommenter() {
        return commenter;
    }

    public UserEntity getReviewer() {
        return reviewer;
    }

    public UserEntity getApprover() {
        return approver;
    }

    public Boolean getVisible() {
        return isVisible;
    }

    public Boolean getApproved() {
        return isApproved;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public Integer getItemID() {
        return itemID;
    }

    public Integer getReplyCommentID() {
        return replyCommentID;
    }

    public Date getLastModified() {
        return lastModified;
    }

    @Override
    public String toString() {
        return "comment id:" + this.id + ", stuff.....";
    }
}
