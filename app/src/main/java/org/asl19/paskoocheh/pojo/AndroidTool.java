package org.asl19.paskoocheh.pojo;

import com.squareup.wire.Wire;

import org.asl19.paskoocheh.proto.BinaryFile;
import org.asl19.paskoocheh.proto.Release;
import org.asl19.paskoocheh.proto.Tool;
import org.asl19.paskoocheh.proto.Translation;
import org.parceler.Parcel;

import java.util.List;

import lombok.Data;

import static org.asl19.paskoocheh.Constants.APP_BADGES;
import static org.asl19.paskoocheh.Constants.DEFAULT_RESPONDER_EMAIL;
import static org.asl19.paskoocheh.Constants.TOOL_TYPES;

/**
 * Tool Object.
 */
@Parcel
@Data
public class AndroidTool {
    String englishName;
    String appType;
    String mailResponder;
    int badgeId;
    boolean featured;
    Long toolId;
    String name;
    String description;
    String iconUrl;
    String downloadUrl;
    String checksum;
    String packageName;
    Integer versionCode;
    String buildVersion;
    List<String> screenshots;
    boolean installed;
    boolean updateAvailable;

    public AndroidTool() {}

    /**
     * Create new AndroidTool from Tool Protocol Buffer Object.
     *
     * @param tool Protocol Buffer tool.
     * @param locale set locale.
     */
    public AndroidTool(Tool tool, String locale) {
        this.englishName = Wire.get(tool.contact.name, "");

        this.appType = TOOL_TYPES.get(Wire.get(tool.types.get(0).getValue(), 0));

        this.badgeId = APP_BADGES.get(Wire.get(tool.types.get(0).getValue(), 0));

        this.featured = Wire.get(tool.is_featured, Tool.DEFAULT_IS_FEATURED);

        this.toolId = Wire.get(tool.tool_id, Tool.DEFAULT_TOOL_ID);

        this.mailResponder = Wire.get(tool.contact.mail_responder_email, DEFAULT_RESPONDER_EMAIL);

        Translation translation = getTranslation(tool.contact.translation, locale);
        this.name = Wire.get(translation.name, Translation.DEFAULT_NAME);
        this.description = Wire.get(translation.description, Translation.DEFAULT_DESCRIPTION);

        Release release = tool.releases.get(0);
        this.iconUrl = Wire.get(release.icon, Release.DEFAULT_ICON);
        this.downloadUrl = Wire.get(release.binary.path, BinaryFile.DEFAULT_PATH);
        this.checksum = Wire.get(release.binary.checksum, BinaryFile.DEFAULT_CHECKSUM);
        this.packageName = Wire.get(release.package_name, Release.DEFAULT_PACKAGE_NAME);
        this.versionCode = Wire.get(release.build_version, Release.DEFAULT_BUILD_VERSION);
        this.buildVersion = Wire.get(release.version, Release.DEFAULT_VERSION);
        this.screenshots = release.screenshots;
    }

    private Translation getTranslation(List<Translation> translations, String locale) {

        if (translations.size() > 0) {
            Translation defaultTranslation = translations.get(0);
            for (int i = 0; i < translations.size(); i++) {
                Translation translation = translations.get(i);
                if (translation.lang.equals(locale)) {
                    return translation;
                }
            }
            return defaultTranslation;
        }

        return new Translation(
                Translation.DEFAULT_LANG,
                Translation.DEFAULT_NAME,
                Translation.DEFAULT_DESCRIPTION
        );
    }
}
