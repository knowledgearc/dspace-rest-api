/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

package org.dspace.rest.providers;

import org.apache.log4j.Logger;
import org.dspace.core.Context;
import org.dspace.rest.content.ContentHelper;
import org.dspace.rest.entities.WorkflowEntity;
import org.dspace.workflow.WorkflowItem;
import org.sakaiproject.entitybus.EntityReference;
import org.sakaiproject.entitybus.entityprovider.CoreEntityProvider;
import org.sakaiproject.entitybus.entityprovider.EntityProviderManager;
import org.sakaiproject.entitybus.entityprovider.capabilities.Updateable;
import org.sakaiproject.entitybus.entityprovider.search.Search;
import org.sakaiproject.entitybus.exception.EntityException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WorkflowProvider extends AbstractBaseProvider implements CoreEntityProvider, Updateable {

    private static Logger log = Logger.getLogger(WorkflowProvider.class);

    public WorkflowProvider(EntityProviderManager entityProviderManager) throws NoSuchMethodException {
        super(entityProviderManager);
        entityProviderManager.registerEntityProvider(this);
        processedEntity = WorkflowEntity.class;
        func2actionMapGET.put("getCount", "count");
        func2actionMapGET.put("getSubmittersCount", "submitterscount");
        func2actionMapGET.put("getSubmitters", "submitters");
        func2actionMapPUT.put("accept", "accept");
        func2actionMapPUT.put("approve", "approve");
        func2actionMapPUT.put("reject", "reject");
        func2actionMapPUT.put("returnToPool", "returnToPool");
        entityConstructor = processedEntity.getDeclaredConstructor();
        initMappings(processedEntity);
    }

    public String getEntityPrefix() {
        return "workflows";
    }

    public boolean entityExists(String id) {
        log.info(userInfo() + "workflow_exists:" + id);

        try {
            Integer.parseInt(id);
        } catch (NumberFormatException ex) {
            return true;
        }

        Context context = null;
        try {
            context = new Context();
            refreshParams(context);

            WorkflowItem comm = WorkflowItem.find(context, Integer.parseInt(id));
            return comm != null ? true : false;
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } finally {
            removeConn(context);
        }
    }

    public Object getEntity(EntityReference ref) {
        log.info(userInfo() + "get_workflow:" + ref.getId());
        String segments[] = getSegments();

        if (segments.length > 3) {
            return super.getEntity(ref);
        } else {
            try {
                Integer.parseInt(ref.getId());
            } catch (NumberFormatException ex) {
                return super.getEntity(ref, ref.getId());
            }
        }

        Context context = null;
        try {
            context = new Context();
            refreshParams(context);

            if (entityExists(ref.getId())) {
                return new WorkflowEntity(ref.getId(), context);
            }
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } finally {
            removeConn(context);
        }
        throw new IllegalArgumentException("Invalid id:" + ref.getId());
    }

    public List<?> getEntities(EntityReference ref, Search search) {
        log.info(userInfo() + "list_workflows");

        Context context = null;
        try {
            context = new Context();
            refreshParams(context);

            List<Object> entities = new ArrayList<Object>();
            WorkflowItem[] workflowItems = ContentHelper.findAllWorkflow(context, reviewer, submitter, fields, status, _start, _limit, _sort.replaceAll("_", " "));
            for (WorkflowItem wfi : workflowItems) {
                entities.add(new WorkflowEntity(wfi, context));
            }

            return entities;
        } catch (SQLException ex) {
            throw new EntityException("Internal server error", "SQL error", 500);
        } finally {
            removeConn(context);
        }
    }

    public Object getSampleEntity() {
        return new WorkflowEntity();
    }
}