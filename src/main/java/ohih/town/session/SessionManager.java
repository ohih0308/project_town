package ohih.town.session;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class SessionManager {


    private static HttpSession getSession(HttpServletRequest request) {
        return request.getSession();
    }

    public static void setAttributes(HttpServletRequest request,
                                     String attributeName, Object input) {
        HttpSession session = getSession(request);

        session.setAttribute(attributeName, input);
    }

    public static Object getAttributes(HttpServletRequest request,
                                       String attributeName) {
        HttpSession session = getSession(request);

        Object value = session.getAttribute(attributeName);

        return (value == null) ? "" : value.toString();
    }

    public static void removeAttribute(HttpServletRequest request,
                                       String attributeName) {
        HttpSession session = getSession(request);
        session.removeAttribute(attributeName);
    }

    public static boolean updateAttribute(HttpServletRequest request,
                                          String attributeName, Object input) {
        HttpSession session = request.getSession();
        Object attribute = getAttributes(request, attributeName);
        if (attribute != null) {
            setAttributes(request, attributeName, input);
            return true;
        }
        return false;
    }
}
