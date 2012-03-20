/**
 * $Id: ActionsExecutable.java 17 2009-02-17 12:33:06Z azeckoski $
 * $URL: http://entitybus.googlecode.com/svn/tags/entitybus-1.0.8/api/src/main/java/org/sakaiproject/entitybus/entityprovider/capabilities/ActionsExecutable.java $
 * ActionsExecutable.java - entity-broker - Jul 25, 2008 2:46:26 PM - azeckoski
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
import org.sakaiproject.entitybus.entityprovider.annotations.EntityCustomAction;


/**
 * This entity supports custom actions (as defined by RoR and REST microformat:
 * http://microformats.org/wiki/rest/urls)<br/>
 * This means that there are custom actions which can be invoked on entities or entity spaces,
 * custom actions can augment the current entity operation or they can completely
 * change the behavior and skip the current operation entirely<br/>
 * You can create methods in your entity provider which either end with {@value #ACTION_METHOD_SUFFIX}
 * or use the {@link EntityCustomAction} suffix to define the custom actions<br/>
 * You can describe the actions using the {@link Describeable} key: <prefix>.action.<actionKey> = description<br/>
 * If you want more control then you can use {@link ActionsDefineable} and {@link ActionsExecutionControllable}<br/>
 * @see EntityCustomAction for more details about the custom action methods
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public interface ActionsExecutable extends EntityProvider {

   /**
    * Use this suffix or the {@link EntityCustomAction} annotation to indicate custom actions
    * for your entities
    */
   public static String ACTION_METHOD_SUFFIX = "CustomAction";

}
