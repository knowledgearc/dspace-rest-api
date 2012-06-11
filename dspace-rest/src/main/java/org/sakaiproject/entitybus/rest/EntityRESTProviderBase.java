/**
 * $Id: EntityRESTProviderBase.java 67 2009-04-14 07:10:48Z azeckoski $
 * $URL: http://entitybus.googlecode.com/svn/tags/entitybus-1.0.8/rest/src/main/java/org/sakaiproject/entitybus/rest/EntityRESTProviderBase.java $
 * EntityRESTProviderBase.java - entity-broker - Jan 14, 2009 12:54:57 AM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Aaron Zeckoski
 * Licensed under the Apache License, Version 2.0
 * 
 * A copy of the Apache License has been included in this 
 * distribution and is available at: http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Aaron Zeckoski (azeckoski @ gmail.com) (aaronz @ vt.edu) (aaron @ caret.cam.ac.uk)
 */

package org.sakaiproject.entitybus.rest;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.sakaiproject.entitybus.EntityBrokerManager;
import org.sakaiproject.entitybus.EntityReference;
import org.sakaiproject.entitybus.EntityView;
import org.sakaiproject.entitybus.entityprovider.capabilities.ActionsExecutable;
import org.sakaiproject.entitybus.entityprovider.extension.ActionReturn;
import org.sakaiproject.entitybus.entityprovider.extension.EntityData;
import org.sakaiproject.entitybus.providers.EntityRESTProvider;
import org.sakaiproject.entitybus.utils.EntityResponse;


/**
 * This is the standard entity REST provider which will be created and set in the entity broker manager,
 * it will register itself with the entitybusmanager on startup or construction,
 * it should also be unregistered correctly
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class EntityRESTProviderBase implements EntityRESTProvider {

    protected EntityRESTProviderBase() { }

    public EntityRESTProviderBase(EntityBrokerManager entityBrokerManager,
            EntityActionsManager entityActionsManager,
            EntityEncodingManager entityEncodingManager,
            EntityHandlerImpl entityRequestHandler) {
        super();
        this.entityBrokerManager = entityBrokerManager;
        this.entityActionsManager = entityActionsManager;
        this.entityEncodingManager = entityEncodingManager;
        this.entityRequestHandler = entityRequestHandler;
        init();
    }

    private EntityBrokerManager entityBrokerManager;
    public void setEntityBrokerManager(EntityBrokerManager entityBrokerManager) {
        this.entityBrokerManager = entityBrokerManager;
    }
    private EntityActionsManager entityActionsManager;
    public void setEntityActionsManager(EntityActionsManager entityActionsManager) {
        this.entityActionsManager = entityActionsManager;
    }
    private EntityEncodingManager entityEncodingManager;
    public void setEntityEncodingManager(EntityEncodingManager entityEncodingManager) {
        this.entityEncodingManager = entityEncodingManager;
    }
    private EntityHandlerImpl entityRequestHandler;
    public void setEntityRequestHandler(EntityHandlerImpl entityRequestHandler) {
        this.entityRequestHandler = entityRequestHandler;
    }

    public void init() {
        System.out.println("INFO EntityRESTProviderBase init");
        // register with the entity broker manager
        this.entityBrokerManager.setEntityRESTProvider(this);
    }

    public void destroy() {
        System.out.println("INFO EntityRESTProviderBase destroy");
        // unregister
        this.entityBrokerManager.setEntityRESTProvider(null);
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.entitybus.providers.EntityRESTProvider#decodeData(java.lang.String, java.lang.String)
     */
    public Map<String, Object> decodeData(String data, String format) {
        return this.entityEncodingManager.decodeData(data, format);
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.entitybus.providers.EntityRESTProvider#encodeData(java.lang.Object, java.lang.String, java.lang.String, java.util.Map)
     */
    public String encodeData(Object data, String format, String name, Map<String, Object> properties) {
        return this.entityEncodingManager.encodeData(data, format, name, properties);
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.entitybus.providers.EntityRESTProvider#formatAndOutputEntity(org.sakaiproject.entitybus.EntityReference, java.lang.String, java.util.List, java.io.OutputStream, java.util.Map)
     */
    public void formatAndOutputEntity(EntityReference ref, String format,
            List<EntityData> entities, OutputStream outputStream, Map<String, Object> params) {
        this.entityEncodingManager.formatAndOutputEntity(ref, format, entities, outputStream, params);
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.entitybus.providers.EntityRESTProvider#handleCustomActionExecution(org.sakaiproject.entitybus.entityprovider.capabilities.ActionsExecutable, org.sakaiproject.entitybus.EntityReference, java.lang.String, java.util.Map, java.io.OutputStream, org.sakaiproject.entitybus.EntityView, java.util.Map)
     */
    public ActionReturn handleCustomActionExecution(ActionsExecutable actionProvider,
            EntityReference ref, String action, Map<String, Object> actionParams,
            OutputStream outputStream, EntityView view, Map<String, Object> searchParams) {
        return this.entityActionsManager.handleCustomActionExecution(actionProvider, ref, action, actionParams, outputStream, view, searchParams);
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.entitybus.providers.EntityRESTProvider#translateInputToEntity(org.sakaiproject.entitybus.EntityReference, java.lang.String, java.io.InputStream, java.util.Map)
     */
    public Object translateInputToEntity(EntityReference ref, String format,
            InputStream inputStream, Map<String, Object> params) {
        return this.entityEncodingManager.translateInputToEntity(ref, format, inputStream, params);
    }

    /* (non-Javadoc)
     * @see org.sakaiproject.entitybus.providers.EntityRESTProvider#handleEntityRequest(java.lang.String, java.lang.String, java.lang.String, java.util.Map, java.lang.Object)
     */
    public EntityResponse handleEntityRequest(String reference, String viewKey, String format,
            Map<String, String> params, Object entity) {
        return this.entityRequestHandler.fireEntityRequestInternal(reference, viewKey, format, params, entity);
    }

}