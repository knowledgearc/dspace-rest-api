/**
 * $Id: EntityBrokerCoreServiceManager.java 17 2009-02-17 12:33:06Z azeckoski $
 * $URL: http://entitybus.googlecode.com/svn/tags/entitybus-1.0.8/impl/src/main/java/org/sakaiproject/entitybus/impl/EntityBrokerCoreServiceManager.java $
 * EntityBrokerCoreServiceManager.java - entity-broker - Jan 14, 2009 5:59:42 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Aaron Zeckoski
 * Licensed under the Apache License, Version 2.0
 * 
 * A copy of the Apache License has been included in this 
 * distribution and is available at: http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Aaron Zeckoski (azeckoski @ gmail.com) (aaronz @ vt.edu) (aaron @ caret.cam.ac.uk)
 */

package org.sakaiproject.entitybus.impl;

import org.sakaiproject.entitybus.DeveloperHelperService;
import org.sakaiproject.entitybus.EntityBroker;
import org.sakaiproject.entitybus.EntityBrokerManager;
import org.sakaiproject.entitybus.access.EntityViewAccessProviderManager;
import org.sakaiproject.entitybus.access.HttpServletAccessProviderManager;
import org.sakaiproject.entitybus.entityprovider.EntityProviderManager;
import org.sakaiproject.entitybus.entityprovider.EntityProviderMethodStore;
import org.sakaiproject.entitybus.entityprovider.extension.RequestGetterWrite;
import org.sakaiproject.entitybus.entityprovider.extension.RequestStorageWrite;
import org.sakaiproject.entitybus.impl.entityprovider.EntityProviderManagerImpl;
import org.sakaiproject.entitybus.providers.EntityPropertiesService;
import org.sakaiproject.entitybus.util.access.EntityViewAccessProviderManagerImpl;
import org.sakaiproject.entitybus.util.access.HttpServletAccessProviderManagerImpl;
import org.sakaiproject.entitybus.util.core.EntityPropertiesServiceSimple;
import org.sakaiproject.entitybus.util.core.EntityProviderMethodStoreImpl;
import org.sakaiproject.entitybus.util.request.RequestGetterImpl;
import org.sakaiproject.entitybus.util.request.RequestStorageImpl;

/**
 * This allows easy startup of the core entitybus services in a way which avoids the developer
 * having to know anything about it, anyone who wants to startup the entitybus core would create
 * an instance of this class
 * Note that the {@link DeveloperHelperService} has to be started separately
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
@SuppressWarnings("deprecation")
public class EntityBrokerCoreServiceManager {

    private static volatile EntityBrokerCoreServiceManager instance;
    public static EntityBrokerCoreServiceManager getInstance() {
        if (instance == null) {
            instance = new EntityBrokerCoreServiceManager();
        }
        return instance;
    }
    public static void setInstance(EntityBrokerCoreServiceManager sm) {
        instance = sm;
    }

    private HttpServletAccessProviderManagerImpl httpServletAccessProviderManager;

    private RequestStorageImpl requestStorage;
    private RequestGetterImpl requestGetter;
    private EntityProviderMethodStoreImpl entityProviderMethodStore;
    private EntityPropertiesService entityPropertiesService;
    private EntityProviderManagerImpl entityProviderManager;
    private EntityBrokerManagerImpl entityBrokerManager;
    private EntityViewAccessProviderManagerImpl entityViewAccessProviderManager;
    private EntityBrokerImpl entityBroker;

    /**
     * Create the core services,
     * they can be accessed using the getters on this class
     */
    public EntityBrokerCoreServiceManager() {
        init();
        setInstance(this);
    }

    /**
     * WARNING: If you use the non-empty constructors to make this object then do not run this,
     * it has already been run and should not be run a second time <br/>
     * Startup all the Core services for the EB system,
     * this can only be run after this is constructed with a full constructor or 
     * the {@link #setEntityBrokerManager(EntityBrokerManager)} method has been called
     * (i.e. all the required services are set)
     */
    public void init() {
        // initialize all the parts
        this.requestGetter = new RequestGetterImpl();
        this.entityPropertiesService = new EntityPropertiesServiceSimple();
        this.httpServletAccessProviderManager = new HttpServletAccessProviderManagerImpl();
        this.entityViewAccessProviderManager = new EntityViewAccessProviderManagerImpl();
        this.entityProviderMethodStore = new EntityProviderMethodStoreImpl();
        this.requestStorage = new RequestStorageImpl(requestGetter);
        this.entityProviderManager = new EntityProviderManagerImpl(requestStorage, requestGetter, entityPropertiesService, entityProviderMethodStore);
        this.entityBrokerManager = new EntityBrokerManagerImpl(entityProviderManager, entityPropertiesService, entityViewAccessProviderManager);
        this.entityBroker = new EntityBrokerImpl(entityProviderManager, entityBrokerManager, requestStorage);
    }

    /**
     * Shutdown the services
     * (just calls over to destroy)
     */
    public void shutdown() {
        destroy();
    }

    /**
     * Shuts down all services and cleans up
     */
    public void destroy() {
        // cleanup everything
        setInstance(null);
        this.entityBroker = null;
        this.entityBrokerManager = null;
        this.entityProviderManager = null;
        this.requestStorage.reset();
        this.requestStorage = null;
        this.entityProviderMethodStore = null;
        this.entityViewAccessProviderManager = null;
        this.httpServletAccessProviderManager = null;
        this.entityPropertiesService = null;
        this.requestGetter.destroy();
        this.requestGetter = null;
    }

    // GETTERS

    public EntityBroker getEntityBroker() {
        return entityBroker;
    }

    public HttpServletAccessProviderManager getHttpServletAccessProviderManager() {
        return httpServletAccessProviderManager;
    }

    public RequestStorageWrite getRequestStorage() {
        return requestStorage;
    }

    public RequestGetterWrite getRequestGetter() {
        return requestGetter;
    }

    public EntityProviderMethodStore getEntityProviderMethodStore() {
        return entityProviderMethodStore;
    }

    public EntityPropertiesService getEntityPropertiesService() {
        return entityPropertiesService;
    }

    public EntityProviderManager getEntityProviderManager() {
        return entityProviderManager;
    }

    public EntityBrokerManager getEntityBrokerManager() {
        return entityBrokerManager;
    }

    public EntityViewAccessProviderManager getEntityViewAccessProviderManager() {
        return entityViewAccessProviderManager;
    }

}
