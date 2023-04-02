package ohih.town.constants;

import lombok.Getter;

@Getter
public enum AllowedExtensionList {
    JPG("jpg"),
    JPEG("jpeg"),
    PNG("png"),
    GIF("gif");

    private final String extension;

    AllowedExtensionList(String extension) {
        this.extension = extension;
    }

    public static boolean isAllowedExtension(String extension) {
        for (AllowedExtensionList allowedExtension : AllowedExtensionList.values()) {
            if (allowedExtension.getExtension().equals(extension)) {
                return true;
            }
        }
        return false;
    }
}
