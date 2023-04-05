package ohih.town.constants;

import java.util.regex.Pattern;

public interface ValidationPatterns {

    Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    Pattern USERNAME = Pattern.compile("^[a-zA-Z0-9_-]{3,50}$");
    Pattern PASSWORD = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z0-9\\d]{8,50}$");

    Pattern SUBJECT = Pattern.compile("^\\S.{5,50}\\S$");
    Pattern BODY = Pattern.compile("^\\S.{5,20000}\\S$");

    Pattern COMMENT = Pattern.compile("^\\S.{0,9998}\\S$");
}
