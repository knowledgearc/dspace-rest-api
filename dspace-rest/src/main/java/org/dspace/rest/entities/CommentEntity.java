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
import org.sakaiproject.entitybus.EntityReference;
import org.sakaiproject.entitybus.exception.EntityException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CommentEntity extends CommentEntityTrim {

    private List<Object> replies = new ArrayList<Object>();

    public CommentEntity() {
    }

    public CommentEntity(String uid, Context context) throws SQLException {
        super(uid, context);
        Comment[] coms = res.getReplies();
        for (Comment c : coms) {
            this.replies.add(new CommentEntity(c));
        }
    }

    public CommentEntity(Comment comment) throws SQLException {
        super(comment);
        Comment[] coms = comment.getReplies();
        for (Comment c : coms) {
            this.replies.add(new CommentEntity(c));
        }
    }

    public String create(EntityReference ref, Map<String, Object> inputVar, Context context) {

        try {
            //Check if user null
            if (context.getCurrentUser() == null) {
                throw new EntityException("Forbidden", "Forbidden", 403);
            }
            //Check if user has read item auth
            Integer itemId = Integer.parseInt((String) inputVar.get("itemId"));
            Item item = Item.find(context, itemId);
            AuthorizeManager.authorizeAction(context, item, Constants.READ);
            //Check if commenter group exist and user is in commenter group
            Group commenters = Group.findByName(context, "Create/Edit Comments");
            if (commenters == null || !Group.isMember(context, commenters.getID())) {
                throw new EntityException("Forbidden", "Forbidden", 403);
            }

            Comment comment = Comment.create(context);
            comment.setItemID(itemId);
            comment.setLastModified(new Date());
            Integer replyId = Integer.parseInt((String) inputVar.get("replyId"));
            if (replyId != null && replyId > 0) {
                Comment replyComment = Comment.find(context, replyId);
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

            return String.valueOf(comment.getID());
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
    }

    public void edit(EntityReference ref, Map<String, Object> inputVar, Context context) {
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

            comment.setLastModified(new Date());
            comment.update();
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
    }

    public void remove(EntityReference ref, Map<String, Object> inputVar, Context context) {
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
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
    }

    public String approve(EntityReference ref, Map<String, Object> inputVar, Context context) {
        try {

            //Check if the  Review and Approve Comments group existing
            Group approvers = Group.findByName(context, "Review and Approve Comments");
            if (approvers == null || !Group.isMember(context, approvers.getID())) {
                throw new EntityException("Forbidden", "Forbidden", 403);
            }

            Integer id = Integer.parseInt(ref.getId());
            Comment comment = Comment.find(context, id);
            AuthorizeManager.authorizeAction(context, comment, Constants.WRITE);

            comment.setVisible(true);
            comment.setApproved(true);
            comment.setApprover(context.getCurrentUser());
            comment.setLastModified(new Date());

            comment.update();

            return String.valueOf(comment.getID());
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (AuthorizeException ae) {
            throw new EntityException("Forbidden", "Forbidden", 403);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        }
    }

    public List<Object> getReplies() {
        return replies;
    }
}
