/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.providers;

import org.apache.log4j.Logger;
import org.dspace.content.WorkspaceItem;
import org.dspace.core.Context;
import org.dspace.rest.content.ContentHelper;
import org.dspace.rest.entities.SubmissionEntity;
import org.sakaiproject.entitybus.EntityReference;
import org.sakaiproject.entitybus.entityprovider.CoreEntityProvider;
import org.sakaiproject.entitybus.entityprovider.EntityProviderManager;
import org.sakaiproject.entitybus.entityprovider.search.Search;
import org.sakaiproject.entitybus.exception.EntityException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SubmissionProvider extends AbstractBaseProvider implements CoreEntityProvider {

    private static Logger log = Logger.getLogger(SubmissionProvider.class);

    public SubmissionProvider(EntityProviderManager entityProviderManager) throws NoSuchMethodException {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
        processedEntity = SubmissionEntity.class;
        func2actionMapGET.put("getCount", "count");
        entityConstructor = processedEntity.getDeclaredConstructor();
        initMappings(processedEntity);
    }

    public String getEntityPrefix() {
        return "submissions";
    }

    public boolean entityExists(String id) {
        return true;
    }

    public Object getEntity(EntityReference ref) {
        log.info(userInfo() + "get_submission:" + ref.getId());
        try {
            Integer.parseInt(ref.getId());
        } catch (NumberFormatException ex) {
            return super.getEntity(ref, ref.getId());
        }
        throw new EntityException("Not Acceptable", "The data is not available", 406);
    }

    public List<?> getEntities(EntityReference ref, Search search) {
        log.info(userInfo() + "list_submissions");

        Context context = null;
        try {
            context = new Context();
            refreshParams(context);

            List<Object> entities = new ArrayList<Object>();
            WorkspaceItem[] workspaceItems = ContentHelper.findAllSubmission(context, _start, _limit);
            for (WorkspaceItem workspaceItem : workspaceItems) {
                entities.add(new SubmissionEntity(workspaceItem, context));
            }

            return entities;
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } finally {
            removeConn(context);
        }
    }

    public Object getSampleEntity() {
        return new SubmissionEntity();
    }
}