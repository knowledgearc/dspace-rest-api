/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.providers;

import org.apache.log4j.Logger;
import org.dspace.content.Comment;
import org.dspace.core.Context;
import org.dspace.rest.entities.CommentEntity;
import org.dspace.rest.entities.CommentEntityTrim;
import org.dspace.rest.util.UserRequestParams;
import org.sakaiproject.entitybus.EntityReference;
import org.sakaiproject.entitybus.entityprovider.CoreEntityProvider;
import org.sakaiproject.entitybus.entityprovider.EntityProviderManager;
import org.sakaiproject.entitybus.entityprovider.capabilities.Createable;
import org.sakaiproject.entitybus.entityprovider.capabilities.Deleteable;
import org.sakaiproject.entitybus.entityprovider.capabilities.Updateable;
import org.sakaiproject.entitybus.entityprovider.search.Search;
import org.sakaiproject.entitybus.exception.EntityException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommentsProvider extends AbstractBaseProvider implements CoreEntityProvider, Updateable, Createable, Deleteable {

    private static Logger log = Logger.getLogger(CommentsProvider.class);

    public CommentsProvider(EntityProviderManager entityProviderManager) throws SQLException, NoSuchMethodException {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
        processedEntity = CommentEntity.class;

        func2actionMapPOST.put("create", "");
        inputParamsPOST.put("create", new String[]{"itemId", "subject", "body"});
        func2actionMapPUT.put("edit", "");
        func2actionMapPUT.put("approve", "approve");
        func2actionMapDELETE.put("remove", "");

        entityConstructor = processedEntity.getDeclaredConstructor();
        initMappings(processedEntity);
    }

    public String getEntityPrefix() {
        return "comments";
    }

    public boolean entityExists(String id) {
        log.info(userInfo() + "comment_exists:" + id);

        Context context = null;
        try {
            context = new Context();

            refreshParams(context);

            Comment comm = Comment.find(context, Integer.parseInt(id));
            return comm != null ? true : false;
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } catch (NumberFormatException ex) {
            throw new EntityException("Bad request", "Could not parse input", 400);
        } finally {
            removeConn(context);
        }
    }

    public Object getEntity(EntityReference reference) {
        log.info(userInfo() + "get_comment:" + reference.getId());
        String segments[] = {};

        if (reqStor.getStoredValue("pathInfo") != null) {
            segments = reqStor.getStoredValue("pathInfo").toString().split("/");
        }

        if (segments.length > 3) {
            return super.getEntity(reference);
        }

        Context context = null;
        try {
            context = new Context();

            UserRequestParams uparams = refreshParams(context);
            boolean replies = uparams.getReplies();
            if (entityExists(reference.getId())) {
                return replies ? new CommentEntity(reference.getId(), context) : new CommentEntityTrim(reference.getId(), context);
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } finally {
            removeConn(context);
        }
        throw new IllegalArgumentException("Invalid id:" + reference.getId());
    }

    public List<?> getEntities(EntityReference ref, Search search) {
        log.info(userInfo() + "list_comments");

        Context context = null;
        try {
            context = new Context();

            refreshParams(context);
            List<Object> entities = new ArrayList<Object>();

            Comment[] comments = Comment.findAllTop(context);
            for (Comment comment : comments) {
                entities.add(new CommentEntity(comment));
            }

            return entities;
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } finally {
            removeConn(context);
        }
    }

    public Object getSampleEntity() {
        return new CommentEntity();
    }
}