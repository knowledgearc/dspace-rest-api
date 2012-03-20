/**
 * $Id: CRUDable.java 17 2009-02-17 12:33:06Z azeckoski $
 * $URL: http://entitybus.googlecode.com/svn/tags/entitybus-1.0.8/api/src/main/java/org/sakaiproject/entitybus/entityprovider/capabilities/CRUDable.java $
 * CRUDable.java - entity-broker - Apr 8, 2008 11:47:03 AM - azeckoski
 **************************************************************************
 * Copyright (c) 2008 Sakai Foundation
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
 */

package org.sakaiproject.entitybus.entityprovider.capabilities;

import org.sakaiproject.entitybus.entityprovider.EntityProvider;


/**
 * A convenient interface to define that this entity type supports all CRUD operations<br/>
 * This is one of the capability extensions for the {@link EntityProvider} interface<br/>
 * 
 * @author Aaron Zeckoski (aaron@caret.cam.ac.uk)
 */
public interface CRUDable extends EntityProvider, Saveable, Createable, Resolvable, Updateable, Deleteable {

   // this space intentionally left blank

}
