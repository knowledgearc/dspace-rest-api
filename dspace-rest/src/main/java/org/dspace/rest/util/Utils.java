package org.dspace.rest.util;

import org.sakaiproject.entitybus.entityprovider.extension.Formats;
import org.sakaiproject.entitybus.rest.EntityEncodingManager;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipInputStream;

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

    private static String readIStoString(InputStream is) throws IOException {
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;

//            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
//            } finally {
//                is.close();
//            }
            return sb.toString();
        } else {
            return "";
        }
    }

    public static Object translateFormattedData(String format, InputStream input) {
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
        }

        System.out.println("== Utils translate formated data called");
        System.out.println("got: \n" + IS + "\ndecoded " + decodedInput);
        return decodedInput;
    }

    public static InputStream transferInputStream(ZipInputStream zipInputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = zipInputStream.read(buffer)) > -1 ) {
            baos.write(buffer, 0, len);
        }
        baos.flush();

        return new ByteArrayInputStream(baos.toByteArray());
    }
}
