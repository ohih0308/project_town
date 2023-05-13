package ohih.town.utilities;

import jakarta.servlet.http.HttpServletRequest;
import ohih.town.constants.*;
import ohih.town.domain.common.dto.AuthorInfo;
import ohih.town.domain.post.dto.Attachment;
import ohih.town.domain.user.dto.UserInfo;
import ohih.town.exception.FileSizeExceedLimitException;
import ohih.town.exception.NotAllowedExtensionException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ohih.town.constants.DomainConst.VALID_GUEST_PASSWORD;
import static ohih.town.constants.EncodeTypeConst.BASE_64;

public class Utilities {
    private static final String CLASS_PATH = "src/main/resources/static/";
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

    public static boolean isValidated(Pattern pattern, String input) {
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

    public static List<String> extractImages(String str) {
        List<String> attachments = new ArrayList<>();
        Pattern pattern = Pattern.compile("<img[^>]*src=[\"']?([^>\"']+)[\"']?[^>]*>");
        Matcher matcher = pattern.matcher(str);
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

    public static String extractExtension(String fileName) {
        return fileName.substring(fileName.indexOf('/') + 1, fileName.indexOf(';'));
    }

    public static String replaceAttachments(String body, List<Attachment> attachments) {
        for (Attachment attachment : attachments) {
            String originalText = BASE_64_PATTERN_OPEN +
                    attachment.getExtension() + ";" +
                    BASE_64 + "," +
                    attachment.getImageData();

            String newText = attachment.getDirectory().substring(
                    attachment.getDirectory().indexOf(CLASS_PATH) + CLASS_PATH.length());

            body = body.replace(originalText, "/" + newText);
        }
        return body;
    }

    public static void isAllowedExtension(String extension) throws NotAllowedExtensionException {
        if (!AllowedExtensionList.isAllowedExtension(extension)) {
            throw new NotAllowedExtensionException();
        }
    }

    public static Integer getBytesLength(String str) {
        if (str == null) {
            return 0;
        }

        byte[] byteArr = str.getBytes();
        return byteArr.length;
    }

    public static void isFileSizeExceedLimit(Integer fileSize) throws FileSizeExceedLimitException {
        if (fileSize > ConfigurationConst.FILE_MAX_SIZE) {
            throw new FileSizeExceedLimitException();
        }
    }


    public static void setAuthor(AuthorInfo authorInfo, UserInfo userInfo, String ip) {
        authorInfo.setIp(ip);
        if (userInfo == null) {
            authorInfo.setUserType(DomainConst.USER_TYPE_GUEST);
        } else {
            authorInfo.setUserId(userInfo.getUserId());
            authorInfo.setUserType(userInfo.getUserType());
            authorInfo.setAuthor(userInfo.getUsername());
            authorInfo.setPassword(VALID_GUEST_PASSWORD);
        }
    }
}
