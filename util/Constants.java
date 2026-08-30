package com.unimate.util;

public class Constants {
    public static final String ROLE_STUDENT    = "STUDENT";
    public static final String ROLE_INSTRUCTOR = "INSTRUCTOR";
    public static final String ROLE_ADMIN      = "ADMIN";

    public static final String STATUS_PENDING  = "PENDING";
    public static final String STATUS_ACTIVE   = "ACTIVE";
    public static final String STATUS_BLOCKED  = "BLOCKED";

    public static final String ITEM_PENDING = "PENDING";
    public static final String ITEM_LIVE    = "LIVE";
    public static final String ITEM_SOLD    = "SOLD";
    public static final String ITEM_REMOVED = "REMOVED";

    public static final String MATERIAL_PENDING  = "PENDING";
    public static final String MATERIAL_LIVE     = "LIVE";
    public static final String MATERIAL_REJECTED = "REJECTED";

    /** A fixed folder on disk. Survives redeploys, unlike getRealPath(). */
    public static final String UPLOAD_ROOT = "C:" + java.io.File.separator + "unimate-uploads";
    public static final String MATERIAL_DIR = UPLOAD_ROOT + java.io.File.separator + "materials";
    public static final String ITEM_DIR     = UPLOAD_ROOT + java.io.File.separator + "items";

    public static java.io.File folder(String path) {
        java.io.File f = new java.io.File(path);
        if (!f.exists()) f.mkdirs();
        return f;
    }

    public static final String[] ALLOWED_DOMAINS = {
        "northsouth.edu", "bracu.ac.bd", "aiub.edu", "iub.edu.bd"
    };

    public static boolean isUniversityEmail(String email) {
        if (email == null || !email.contains("@")) return false;
        String domain = email.substring(email.indexOf("@") + 1).toLowerCase();
        for (String d : ALLOWED_DOMAINS) {
            if (domain.equals(d)) return true;
        }
        return false;
    }
}
