/**
 * $Id: CoreEntityProvider.java 17 2009-02-17 12:33:06Z azeckoski $
 * $URL: http://entitybus.googlecode.com/svn/tags/entitybus-1.0.8/api/src/main/java/org/sakaiproject/entitybus/entityprovider/CoreEntityProvider.java $
 * AutoRegister.java - entity-broker - 31 May 2007 7:01:11 PM - azeckoski
 **************************************************************************
 * Copyright (c) 2007, 2008 Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package org.sakaiproject.entitybus.entityprovider;

import org.sakaiproject.entitybus.EntityBroker;

/**
 * This is the base unit for working with EB entities, by implementing this interface and
 * registering it with the {@link EntityProviderManager} service you are adding entity data
 * to the provider manager system, there are many other interfaces
 * which you can implement to extend the interaction of your entities the system<br/>
 * You (the implementor) will want to create one implementation of this interface for each type of
 * entity you want to link to the system to track events, provide URL access, etc.<br/>
 * <br/>
 * Usage:<br/>
 * 1) Implement this interface<br/> 
 * 2) Implement any additional capabilities interfaces (optional)<br/>
 * 3) Register this implementation with the {@link EntityProviderManager} service<br/>
 * <br/> 
 * You and others should now be able to use the {@link EntityBroker} to access information about your entities 
 * and register events for your entities (among other things).
 * 
 * @author Aaron Zeckoski (aaronz@vt.edu)
 */
public interface CoreEntityProvider extends EntityProvider {

   /**
    * Check if a specific entity managed by this provider exists.<br/>
    * This is primarily used to validate references before making other calls or operating on them.<br/>
    * <b>WARNING:</b> This will be called many times and AT LEAST right before calls are made to
    * any methods or capabilities related to specific entities, please make sure this is
    * very efficient. If you are concerned about efficiency, it is ok for this method to always
    * return true but you will no longer be able to be sure that calls through to your capability
    * implementations are always valid.
    * 
    * @param id a locally unique id for an entity managed by this provider<br/>
    * <b>NOTE:</b> this will be an empty string if this is an entity space (singleton entity) without an id available
    * @return true if an entity with given local id exists, false otherwise
    */
   public boolean entityExists(String id);

}
