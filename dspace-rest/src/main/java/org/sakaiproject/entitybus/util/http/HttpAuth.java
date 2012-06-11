/**
 * $Id: HttpAuth.java 90 2009-10-11 23:05:01Z azeckoski $
 * $URL: https://entitybus.googlecode.com/svn/trunk/utils/src/main/java/org/sakaiproject/entitybus/util/http/HttpRESTUtils.java $
 * HttpAuth.java - entity-broker - Jul 20, 2008 11:42:19 AM - azeckoski
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

package org.sakaiproject.entitybus.util.http;

/**
 * Stores the auth data extracted from the request
 * 
 * @author Aaron Zeckoski (azeckoski @ gmail.com)
 */
public class HttpAuth {

    public static enum AuthType {PARAMS, BASIC};

    AuthType type = AuthType.PARAMS;
    String loginName;
    String password;

    public HttpAuth(AuthType type, String loginName, String password) {
        this.type = type;
        this.loginName = loginName;
        this.password = password;
    }

    public String getLoginName() {
        return loginName;
    }

    public String getPassword() {
        return password;
    }

    public AuthType getType() {
        return type;
    }

}
