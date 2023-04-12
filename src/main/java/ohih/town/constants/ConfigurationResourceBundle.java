package ohih.town.constants;

import org.springframework.context.support.MessageSourceResourceBundle;

import java.util.ResourceBundle;

public interface ConfigurationResourceBundle {
    ResourceBundle FILE_PATHS = ResourceBundle.getBundle("config.file-paths");
    ResourceBundle MAIL = MessageSourceResourceBundle.getBundle("town-mail");
}
