package ohih.town.utilities;

import jakarta.servlet.http.HttpServletRequest;
import ohih.town.constants.PagingConst;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {
    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    // paging revised
    public static Paging getPaging(Long totalCount, Integer presentPage, Integer itemsPerPage) {
        int pagesPerBlock = PagingConst.pagesPerBlock;
        int totalPages = (int) Math.ceil(totalCount / (double) itemsPerPage);

        // 현재 페이지가 null이면 0으로 설정
        presentPage = presentPage == null ? 0 : presentPage;

        int startPage = presentPage - presentPage % pagesPerBlock;

        int endPage = startPage + pagesPerBlock - 1;
        if (endPage >= totalPages) {
            endPage = totalPages - 1;
        }
        if (endPage < 0) {
            endPage = 0;
        }

        long firstContent = (long) presentPage * itemsPerPage;

        return new Paging(totalCount, totalPages, startPage, endPage, presentPage, firstContent, itemsPerPage);
    }

    public static Boolean isValidPattern(Pattern pattern, String input) {
        if (input == null) {
            return false;
        }
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    public static String createCode(int length) {
        UUID uuid = UUID.randomUUID();

        return uuid.toString().substring(0, length);
    }
}
