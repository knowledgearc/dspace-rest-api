/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.rest.providers;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.dspace.authorize.AuthorizeException;
import org.dspace.content.Collection;
import org.dspace.content.Community;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.rest.util.UserRequestParams;
import org.dspace.rest.util.UtilHelper;
import org.sakaiproject.entitybus.EntityReference;
import org.sakaiproject.entitybus.EntityView;
import org.sakaiproject.entitybus.entityprovider.EntityProvider;
import org.sakaiproject.entitybus.entityprovider.EntityProviderManager;
import org.sakaiproject.entitybus.entityprovider.capabilities.*;
import org.sakaiproject.entitybus.entityprovider.extension.Formats;
import org.sakaiproject.entitybus.entityprovider.extension.RequestGetter;
import org.sakaiproject.entitybus.entityprovider.extension.RequestStorage;
import org.sakaiproject.entitybus.exception.EntityException;
import org.sakaiproject.entitybus.rest.EntityEncodingManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public abstract class AbstractBaseProvider implements EntityProvider, Resolvable, CollectionResolvable, InputTranslatable, RequestAware, Outputable, ActionsExecutable, Redirectable, RequestStorable, RequestInterceptor{

    protected RequestStorage reqStor;
    protected boolean withdrawn;
    protected String user = "";
    protected String pass = "";
    protected String userc = "";
    protected String passc = "";
    protected String query, _order, _sort, loggedUser, _sdate, _edate;
    protected int _start, _limit;
    protected List<Integer> sortOptions = new ArrayList<Integer>();
    protected Collection _collection = null;
    protected Community _community = null;
    private static Logger log = Logger.getLogger(AbstractBaseProvider.class);
    protected Map<String, String> func2actionMapGET = new HashMap<String, String>();
    protected Map<String, String> func2actionMapPUT = new HashMap<String, String>();
    protected Map<String, String> func2actionMapPOST = new HashMap<String, String>();
    protected Map<String, String> func2actionMapDELETE = new HashMap<String, String>();
    protected Map<String, Class<?>[]> funcParamsGET = new HashMap<String, Class<?>[]>();
    protected Map<String, Class<?>[]> funcParamsPUT = new HashMap<String, Class<?>[]>();
    protected Map<String, Class<?>[]> funcParamsPOST = new HashMap<String, Class<?>[]>();
    protected Map<String, Class<?>[]> funcParamsDELETE = new HashMap<String, Class<?>[]>();
    protected Map<String, String[]> inputParamsPOST = new HashMap<String, String[]>();
    protected Map<String, String> func2actionMapGET_rev = new HashMap<String, String>();
    protected Map<String, String> func2actionMapPUT_rev = new HashMap<String, String>();
    protected Map<String, String> func2actionMapPOST_rev = new HashMap<String, String>();
    protected Map<String, String> func2actionMapDELETE_rev = new HashMap<String, String>();
    protected Class<?> processedEntity = null;
    protected Constructor<?> entityConstructor = null;
    protected RequestGetter requestGetter;
    protected EntityProviderManager entityProviderManager;

    protected String format = "";
    protected String[] fields;
    protected String status;
    protected String submitter, reviewer;

    protected boolean collections = false;
    protected boolean trim = false;
    protected boolean parents = false;
    protected boolean children = false;
    protected boolean groups = false;
    protected boolean replies = false;

    protected String[] type;
//    protected String action;

    public AbstractBaseProvider(EntityProviderManager entityProviderManager) {
        this.entityProviderManager = entityProviderManager;
        entityProviderManager.registerEntityProvider(this);
    }

    protected void initMappings(Class<?> processedEntity) {
        // scan for methods;
        Method[] entityMethods = processedEntity.getMethods();
        for (Method m : entityMethods) {
            //System.out.println("checked method " + m.getName());
            String fieldPUT = func2actionMapPUT.get(m.getName());
            if (fieldPUT != null) {
//                System.out.println("added " + fieldPUT + ":" + m.getName());
                addParameters(fieldPUT, m.getParameterTypes(), funcParamsPUT);
                addMethod(fieldPUT, m.getName(), func2actionMapPUT_rev);
            }
            String fieldGET = func2actionMapGET.get(m.getName());
            if (fieldGET != null) {
                addParameters(fieldGET, m.getParameterTypes(), funcParamsGET);
                addMethod(fieldGET, m.getName(), func2actionMapGET_rev);
            }
            String fieldPOST = func2actionMapPOST.get(m.getName());
            if (fieldPOST != null) {
                addParameters(fieldPOST, m.getParameterTypes(), funcParamsPOST);
                addMethod(fieldPOST, m.getName(), func2actionMapPOST_rev);
            }
            String fieldDELETE = func2actionMapDELETE.get(m.getName());
            if (fieldDELETE != null) {
                addParameters(fieldDELETE, m.getParameterTypes(), funcParamsDELETE);
                addMethod(fieldDELETE, m.getName(), func2actionMapDELETE_rev);
            }
        }
    }

    public void setRequestStorage(RequestStorage rStor) {
        this.reqStor = rStor;
    }

    public void destroy() {
        entityProviderManager.unregisterEntityProvider(this);
    }

    public String userInfo() {
        String ipaddr = "";

        try {
            ipaddr = this.entityProviderManager.getRequestGetter().getRequest().getRemoteAddr();
        } catch (NullPointerException ex) {
        }
        return "user:" + loggedUser + ":ip_addr=" + ipaddr + ":";
    }

    public String readIStoString(InputStream is) throws IOException {
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                is.close();
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    public void before(EntityView view, HttpServletRequest req, HttpServletResponse res) {

        log.info(userInfo() + "starting to write for collection adding");
        try {
            if (req.getContentType().equals("application/json")) {
                view.setExtension("json");
                format = "json";
            } else if (req.getContentType().equals("application/xml")) {
                view.setExtension("xml");
                format = "xml";
            } else if (req.getContentType().startsWith("multipart/form-data")) {
                view.setExtension("stream");
                format = "stream";
            } else {
                view.setExtension("json");
                format = "json";
            }
        } catch (Exception ex) {
            if (view.getFormat().equals("xml")) {
                view.setExtension("xml");
                format = "xml";
            } else {
                view.setExtension("json");
                format = "json";
            }
        }

        try {
            if (!(req.getHeader("user").isEmpty() && req.getHeader("pass").isEmpty())) {
                userc = req.getHeader("user");
                passc = req.getHeader("pass");
            }
        } catch (NullPointerException nu) {
            userc = "";
            passc = "";
        }
    }

    public void after(EntityView view, HttpServletRequest req, HttpServletResponse res) {
    }

    public UserRequestParams refreshParams(Context context) {

        UserRequestParams uparam = new UserRequestParams();

        try {
            user = reqStor.getStoredValue("user").toString();
        } catch (NullPointerException ex) {
            user = "";
        }

        try {
            pass = reqStor.getStoredValue("pass").toString();
        } catch (NullPointerException ex) {
            pass = "";
        }

        try {
            if (!(userc.isEmpty() && passc.isEmpty())) {
                user = userc;
                pass = passc;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } // now try to login user
        loggedUser = "anonymous";

        try {
            EPerson eUser = EPerson.findByEmail(context, user);
            if ((eUser.canLogIn()) && (eUser.checkPassword(pass))) {
                context.setCurrentUser(eUser);
                loggedUser = eUser.getName();
            } else {
                throw new EntityException("Bad username or password", user, 403);
            }
        } catch (SQLException sql) {
//            System.out.println(sql.toString());
            sql.printStackTrace();
        } catch (AuthorizeException auth) {
            throw new EntityException("Unauthorised", user, 401);
        } catch (NullPointerException ne) {
            if (!(user.equals("") && pass.equals(""))) {
                throw new EntityException("Bad username or password", user, 403);
            }
        }

        this.collections = "true".equals(reqStor.getStoredValue("collections"));
        uparam.setCollections(this.collections);
        this.trim = "true".equals(reqStor.getStoredValue("trim"));
        uparam.setTrim(this.trim);
        this.parents = "true".equals(reqStor.getStoredValue("parents"));
        uparam.setParents(this.parents);
        this.children = "true".equals(reqStor.getStoredValue("children"));
        uparam.setChildren(this.children);
        this.groups = "true".equals(reqStor.getStoredValue("groups"));
        uparam.setGroups(this.groups);
        this.replies = "true".equals(reqStor.getStoredValue("replies"));
        uparam.setReplies(this.replies);

//        try {
//            action = reqStor.getStoredValue("action").toString();
//            uparam.setAction(action);
//        } catch (NullPointerException ex) {
//            action = "";
//        }

        try {
            query = reqStor.getStoredValue("query").toString();
            uparam.setQuery(query);
        } catch (NullPointerException ex) {
            query = "";
        }

        try {
            Object o = reqStor.getStoredValue("fields");
            if (o instanceof String) {
                fields = new String[]{o.toString()};
            } else if (o instanceof String[]) {
                fields = (String[]) o;
            } else if (o == null) {
                fields = null;
            }
            uparam.setFields(fields);
        } catch (NullPointerException ex) {
            fields = null;
        }

        try {
            status = reqStor.getStoredValue("status").toString();
            uparam.setStatus(status);
        } catch (NullPointerException ex) {
            status = "";
        }

        try {
            submitter = reqStor.getStoredValue("submitter").toString();
            uparam.setSubmitter(submitter);
        } catch (NullPointerException ex) {
            submitter = "";
        }

        try {
            reviewer = reqStor.getStoredValue("reviewer").toString();
            uparam.setReviewer(reviewer);
        } catch (NullPointerException ex) {
            reviewer = "";
        }

        try {
            String bundleStr = reqStor.getStoredValue("type").toString();
            uparam.setType(bundleStr.split(","));
        } catch (NullPointerException ex) {
            type = null;
        }

        try {
            _order = reqStor.getStoredValue("order").toString();
            uparam.setOrder(_order);
        } catch (NullPointerException ex) {
            _order = "";
        }

        try {
            _sort = reqStor.getStoredValue("sort").toString();
            uparam.setSort(_sort);
        } catch (NullPointerException ex) {
            _sort = "";


        } // both parameters are used according to requirements
        if (_order.length() > 0 && _sort.equals("")) {
            _sort = _order;
        }

        try {
            _start = Integer.parseInt(reqStor.getStoredValue("start").toString());
            uparam.setStart(_start);
        } catch (NullPointerException ex) {
            _start = 0;
        }

        try {
            _limit = Integer.parseInt(reqStor.getStoredValue("limit").toString());
            uparam.setLimit(_limit);
        } catch (NullPointerException ex) {
            _limit = 0;
        } // some checking for invalid values

        if (_limit < 0) {
            _limit = 0;
        }


        try {
            _sdate = reqStor.getStoredValue("startdate").toString();
        } catch (NullPointerException ex) {
            _sdate = null;
        }

        try {
            _edate = reqStor.getStoredValue("enddate").toString();
        } catch (NullPointerException ex) {
            _edate = null;
        }

        try {
            withdrawn = reqStor.getStoredValue("withdrawn").toString().equalsIgnoreCase("true");
        } catch (NullPointerException ex) {
            withdrawn = false;
        }
        // defining sort fields and values
        _sort = _sort.toLowerCase();
        String[] sort_arr = _sort.split(",");

        for (String option : sort_arr) {
            if (option.startsWith("submitter")) {
                sortOptions.add(UtilHelper.SORT_SUBMITTER);
            } else if (option.startsWith("email")) {
                sortOptions.add(UtilHelper.SORT_EMAIL);
            } else if (option.startsWith("firstname")) {
                sortOptions.add(UtilHelper.SORT_FIRSTNAME);
            } else if (option.startsWith("lastname")) {
                sortOptions.add(UtilHelper.SORT_LASTNAME);
            } else if (option.startsWith("fullname")) {
                sortOptions.add(UtilHelper.SORT_FULL_NAME);
            } else if (option.startsWith("language")) {
                sortOptions.add(UtilHelper.SORT_LANGUAGE);
            } else if (option.startsWith("lastmodified")) {
                sortOptions.add(UtilHelper.SORT_LASTMODIFIED);
            } else if (option.startsWith("countitems")) {
                sortOptions.add(UtilHelper.SORT_COUNT_ITEMS);
            } else if (option.startsWith("name")) {
                sortOptions.add(UtilHelper.SORT_NAME);
            } else {
                sortOptions.add(UtilHelper.SORT_ID);
            }
            if ((option.endsWith("_desc") || option.endsWith("_reverse"))) {
                int i = sortOptions.get(sortOptions.size() - 1);
                sortOptions.remove(sortOptions.size() - 1);
                i += 100;
                sortOptions.add(i);
            }
        }

        int intcommunity = 0;
        int intcollection = 0;

        // integer values used in some parts
        try {
            intcommunity = Integer.parseInt(reqStor.getStoredValue("community").toString());
        } catch (NullPointerException nul) {
        }

        try {
            _community = Community.find(context, intcommunity);
        } catch (SQLException sql) {
        }

        try {
            intcollection = Integer.parseInt(reqStor.getStoredValue("collection").toString());
        } catch (NullPointerException nul) {
        }

        try {
            _collection = Collection.find(context, intcollection);
        } catch (SQLException sql) {
        }

        if ((intcommunity > 0) && (intcollection > 0)) {
            throw new EntityException("Bad request", "Community and collection selected", 400);
        }

        if ((intcommunity > 0) && (_community == null)) {
            throw new EntityException("Bad request", "Unknown community", 400);
        }

        if ((intcollection > 0) && (_collection == null)) {
            throw new EntityException("Bad request", "Unknown collection", 400);
        }

        return uparam;
    }

    public String[] getHandledInputFormats() {
        return new String[]{Formats.HTML, Formats.XML, Formats.JSON, "stream"};
    }

    public Object translateFormattedData(EntityReference ref, String format, InputStream input, Map<String, Object> params) {
        String IS = "";
        if (format.equals("xml") || format.equals("json")) {
            try {
                IS = readIStoString(input);
//                System.out.println("is+= " + IS);
            } catch (IOException ex) {
                ex.printStackTrace();
            }    
        }
        
        Map<String, Object> decodedInput = null;
        EntityEncodingManager em = new EntityEncodingManager(null, null);
        if (format.equals("xml")) {
            decodedInput = em.decodeData(IS, Formats.XML);
        } else if (format.equals("json")) {
            decodedInput = em.decodeData(IS, Formats.JSON);
        } else if (format.equals("stream")) {
            try {
                ServletFileUpload upload = new ServletFileUpload();
                FileItemIterator iter = upload.getItemIterator(requestGetter.getRequest());
                while (iter.hasNext()) {
                    FileItemStream item = iter.next();
                    return item.openStream();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("== translate formated data called");
        System.out.println("got: \n" + IS + "\ndecoded " + decodedInput);
        return decodedInput;
    }

    public void removeConn(Context context) {
        // close connection to prevent connection problems
        try {
            if (context != null) {
                context.complete();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setRequestGetter(RequestGetter requestGetter) {
        this.requestGetter = requestGetter;
    }

    public String[] getHandledOutputFormats() {
        return new String[]{Formats.JSON, Formats.XML, Formats.FORM, Formats.ATOM};
    }

    public void addParameters(String function, Class<?>[] parameters, Map<String, Class<?>[]> mappings_parameters) {
        mappings_parameters.put(function, parameters);
    }

    public void addMethod(String field, String function, Map<String, String> mappings_rev) {
        mappings_rev.put(field, function);
    }

    public String getMethod(String field, Map<String, String> mappings_rev) {
        return mappings_rev.get(field);
    }

    public String[] getSegments() {
        return getSegments(null);
    }

    public String[] getSegments(Map<String, Object> params) {
        String segments[] = {};

        if (params != null) {
            if (params.containsKey("pathInfo")) {
                segments = params.get("pathInfo").toString().split("/");
            }
        } else {
            if (reqStor.getStoredValue("pathInfo") != null) {
                segments = reqStor.getStoredValue("pathInfo").toString().split("/");
            }
        }
        return segments;
    }

    public Object getEntity(EntityReference ref) {
        String segments[] = getSegments();

        if (segments[segments.length - 1].startsWith("count")) {
            return getEntity(ref, segments[segments.length - 2] + "count");
        }

        return getEntity(ref, segments[3]);
    }

    public Object getEntity(EntityReference ref, String action) {

        if (action.lastIndexOf(".") > 0) {
            action = action.substring(0, action.lastIndexOf("."));
        }

        if (func2actionMapGET_rev.containsKey(action)) {
            Object result;
            String function = getMethod(action, func2actionMapGET_rev);
            if (function == null) {
                throw new EntityException("Bad request", "Method not supported - not defined", 400);
            }

            Context context = null;
            try {
                context = new Context();
                UserRequestParams uparams = refreshParams(context);

                Object CE = entityConstructor.newInstance();
                Method method = CE.getClass().getMethod(function, funcParamsGET.get(action));
                result = method.invoke(CE, ref, uparams, context);
            } catch (NoSuchMethodException ex) {
                throw new EntityException("Not found", "Method not supported ", 404);
            } catch (SQLException ex) {
                throw new EntityException("Internal server error", "SQL error", 500);
            } catch (InvocationTargetException ex) {
                if (ex.getCause() != null) {
                    throw (RuntimeException) ex.getCause();
                } else {
                    throw new EntityException("Internal server error", "Unknown error", 500);
                }
            } catch (Exception ex) {
                throw new EntityException("Internal server error", "Unknown error", 500);
            } finally {
                removeConn(context);
            }
            return result;
        } else {
            throw new EntityException("Bad request", "Method not supported " + action, 400);
        }
    }

    public void deleteEntity(EntityReference ref, Map<String, Object> params) {
        String segments[] = getSegments(params);
        String action = "";
        Map<String, Object> inputVar = new HashMap<String, Object>();

        for (int x = 0; x < segments.length; x++) {
            switch (x) {
                case 1:
                    inputVar.put("base", segments[x]);
                    break;
                case 2:
                    inputVar.put("id", segments[x]);
                    break;
                case 3:
                    inputVar.put("element", segments[x]);
                    break;
                case 4:
                    String s = segments[x];
                    if(s!=null && !"".equals(s)) {
                        if (s.lastIndexOf(".") > 0) {
                            s = s.substring(0, s.lastIndexOf("."));
                        }
                    }
                    inputVar.put("eid", s);
                    break;
                case 5:
                    inputVar.put("trail", segments[x]);
                    break;
                default:
                    break;
            }
        }

        if (segments.length > 3) {
            action = segments[3];
        }

        if (func2actionMapDELETE_rev.containsKey(action)) {
            String function = getMethod(action, func2actionMapDELETE_rev);
            if (function == null) {
                throw new EntityException("Bad request", "Method not supported - not defined", 400);
            }
            Context context = null;
            try {
                context = new Context();
                refreshParams(context);

                Object CE = entityConstructor.newInstance();
                Method method = CE.getClass().getMethod(function, funcParamsDELETE.get(action));
                method.invoke(CE, ref, inputVar, context);
            } catch (NoSuchMethodException ex) {
                throw new EntityException("Not found", "Meethod not supported " + segments[3], 404);
            } catch (SQLException ex) {
                throw new EntityException("Internal server error", "SQL error", 500);
            } catch (InvocationTargetException ex) {
                if (ex.getCause() != null) {
                    throw (RuntimeException) ex.getCause();
                } else {
                    throw new EntityException("Internal server error", "Unknown error", 500);
                }
            } catch (Exception ex) {
                throw new EntityException("Internal server error", "Unknown error", 500);
            } finally {
                removeConn(context);
            }
        } else {
            throw new EntityException("Bad request", "Method not supported " + action, 400);
        }
    }

    public void updateEntity(EntityReference ref, Object entity, Map<String, Object> params) {
        Map<String, Object> inputVar = (HashMap<String, Object>) entity;
        String segments[] = getSegments(params);

        String action = "";
        if (segments.length > 3) {
            action = segments[3];
            if (action.lastIndexOf(".") > 0) {
                action = action.substring(0, action.lastIndexOf("."));
            }
        }

        if (func2actionMapPUT_rev.containsKey(action)) {
            String function = getMethod(action, func2actionMapPUT_rev);
            if (function == null) {
                throw new EntityException("Bad request", "Method not supported - not defined", 400);
            }

            Context context = null;
            try {
                context = new Context();
                refreshParams(context);

                Object CE = entityConstructor.newInstance();
                Method method = CE.getClass().getMethod(function, funcParamsPUT.get(action));
                method.invoke(CE, ref, inputVar, context);
            } catch (NoSuchMethodException ex) {
                throw new EntityException("Not found", "Meethod not supported " + segments[3], 404);
            } catch (SQLException ex) {
                throw new EntityException("Internal server error", "SQL error", 500);
            } catch (InvocationTargetException ex) {
                if (ex.getCause() != null) {
                    throw (RuntimeException) ex.getCause();
                } else {
                    throw new EntityException("Internal server error", "Unknown error", 500);
                }
            } catch (Exception ex) {
                throw new EntityException("Internal server error", "Unknown error", 500);
            } finally {
                removeConn(context);
            }
        } else {
            throw new EntityException("Bad request", "Maethod not supported " + action, 400);
        }
    }

    public String createEntity(EntityReference ref, Object entity, Map<String, Object> params) {
        String result;
        Map<String, Object> inputVar = new HashMap<String, Object>();
        if(entity instanceof Map){
            inputVar = (HashMap<String, Object>) entity;
        }

        String function;
        String[] mandatory_params;

        String action = "";
        String segments[] = getSegments(params);
        if (segments.length > 2) {
            action = segments[segments.length - 1];
            if (action.lastIndexOf(".") > 0) {
                action = action.substring(0, action.lastIndexOf("."));
            }
        }

        if (func2actionMapPOST_rev.containsKey(action)) {
            function = func2actionMapPOST_rev.get(action);
            if (function == null) {
                throw new EntityException("Bad request", "Method not supported - not defined", 400);
            }
            mandatory_params = inputParamsPOST.get(function);
            if (mandatory_params != null) {
                for (String param : mandatory_params) {
                    if (inputVar.get(param) == null) {
                        throw new EntityException("Bad request", "Incomplete request [mandatory param]", 400);
                    }
                }
            }

            Context context = null;
            try {
                context = new Context();
                refreshParams(context);

                Object CE = entityConstructor.newInstance();
                Method method = CE.getClass().getMethod(function, funcParamsPOST.get(action));
                if(entity instanceof Map){
                    result = (String) method.invoke(CE, ref, inputVar, context);
                }else {
                    result = (String) method.invoke(CE, ref, entity, context);
                }
            } catch (NoSuchMethodException ex) {
                throw new EntityException("Not found", "Method not supported ", 404);
            } catch (SQLException ex) {
                throw new EntityException("Internal server error", "SQL error", 500);
            } catch (InvocationTargetException ex) {
                if (ex.getCause() != null) {
                    throw (RuntimeException) ex.getCause();
                } else {
                    throw new EntityException("Internal server error", "Unknown error", 500);
                }
            } catch (Exception ex) {
                throw new EntityException("Internal server error", "Unknown error", 500);
            } finally {
                removeConn(context);
            }
            return result;
        } else {
            throw new EntityException("Bad request", "Method not supported " + action, 400);
        }
    }
}