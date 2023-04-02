package ohih.town.utilities;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import ohih.town.constants.PagingConst;
import ohih.town.domain.post.dto.Attachment;
import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {
    private static final String CLASS_PATH = "src/main/resources/";
    private static final String BASE_64_PATTERN_OPEN = "data:image/";


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

    public static String getDate(String dateFormat) {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);

        return today.format(formatter);
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

    public static List<String> extractAttachmentsFromBody(String body) {
        List<String> attachments = new ArrayList<>();
        Pattern pattern = Pattern.compile("<img[^>]*src=[\"']?([^>\"']+)[\"']?[^>]*>");
        Matcher matcher = pattern.matcher(body);
        while (matcher.find()) {
            String imgSrc = matcher.group(1);
            if (imgSrc.startsWith("data:image")) { // base64 인코딩된 이미지인 경우
                attachments.add(imgSrc);
            } else if (imgSrc.startsWith("/uploads/")) { // 업로드된 이미지인 경우
                attachments.add(imgSrc);
            }
        }
        return attachments;
    }

    public static String extractExtension(String file) {
        return file.substring(file.indexOf('/') + 1, file.indexOf(';'));
    }

    public static String replaceAttachmentsInBody(String body, Attachment attachment, String ENCODE_TYPE) {
        String originalText = BASE_64_PATTERN_OPEN +
                attachment.getExtension() + ";" +
                ENCODE_TYPE + "," +
                attachment.getImageDate();

        String newText = attachment.getDirectory().substring(
                attachment.getDirectory().indexOf(CLASS_PATH) + CLASS_PATH.length());

        return body.replace(originalText, newText);
    }
}
