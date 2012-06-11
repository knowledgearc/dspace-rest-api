package org.dspace.rest.util;

import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static String getMapValue(Map<String, Object> inputVar, String key) {
        if (inputVar.containsKey(key)) {
            return inputVar.get(key) == null ? "" : (String) inputVar.get(key);
        }
        return null;
    }

    public static String ADMIN     = "admin";
    public static String SUBMIT    = "submit";
    public static String WF_STEP_1 = "workflow_step_1";
    public static String WF_STEP_2 = "workflow_step_2";
    public static String WF_STEP_3 = "workflow_step_3";

    public static Map<String, Integer> actionRoles = new HashMap<String, Integer>();

    static {
        actionRoles.put("admin", 1);
        actionRoles.put("submit", 2);
        actionRoles.put("default_read", 3);
        actionRoles.put("workflow_step_1", 4);
        actionRoles.put("workflow_step_2", 5);
        actionRoles.put("workflow_step_3", 6);
    }

    public static int getActionRole(String action) {
        return actionRoles.get(action) == null ? 0 : actionRoles.get(action);
    }
}
