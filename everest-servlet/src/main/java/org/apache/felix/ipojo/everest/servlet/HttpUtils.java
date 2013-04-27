package org.apache.felix.ipojo.everest.servlet;

import javax.servlet.http.HttpServletRequest;

/**
 * A set of HTTP utility methods
 */
public class HttpUtils {


    public static boolean isHead(HttpServletRequest req) {
        return "HEAD".equals(req.getMethod());
    }

    public static boolean isGet(HttpServletRequest req) {
        return "GET".equals(req.getMethod());
    }

    public static boolean isPost(HttpServletRequest req) {
        return "POST".equals(req.getMethod());
    }

    public static boolean isDelete(HttpServletRequest req) {
        return "DELETE".equals(req.getMethod());
    }

    public static boolean isPut(HttpServletRequest req) {
        return "PUT".equals(req.getMethod());
    }
}
