package org.asl19.paskoocheh.proto;

import com.squareup.wire.FieldEncoding;
import com.squareup.wire.ProtoAdapter;
import com.squareup.wire.ProtoReader;
import com.squareup.wire.ProtoWriter;
import com.squareup.wire.WireField;
import com.squareup.wire.internal.Internal;

import java.io.IOException;
import java.util.List;

import okio.ByteString;

public final class Release extends com.squareup.wire.Message<Release, Release.Builder> {
  public static final ProtoAdapter<Release> ADAPTER = new ProtoAdapter_Release();

  private static final long serialVersionUID = 0L;

  public static final String DEFAULT_VERSION = "";

  public static final Long DEFAULT_DATE_CREATED = 0L;

  public static final Float DEFAULT_RATING = 0.0f;

  public static final Long DEFAULT_DOWNLOAD_COUNT = 0L;

  public static final String DEFAULT_ICON = "";

  public static final Long DEFAULT_DATE_MODIFIED = 0L;

  public static final Long DEFAULT_DATE_RELEASED = 0L;

  public static final String DEFAULT_RELEASE_URL = "";

  public static final String DEFAULT_PACKAGE_NAME = "";

  public static final Integer DEFAULT_BUILD_VERSION = 0;

  /**
   * Version of the release
   */
  @WireField(
      tag = 1,
      adapter = "com.squareup.wire.ProtoAdapter#STRING",
      label = WireField.Label.REQUIRED
  )
  public final String version;

  /**
   * Date the file uploaded in unix time
   */
  @WireField(
      tag = 2,
      adapter = "com.squareup.wire.ProtoAdapter#INT64",
      label = WireField.Label.REQUIRED
  )
  public final Long date_created;

  @WireField(
      tag = 3,
      adapter = "org.asl19.paskoocheh.proto.BinaryFile#ADAPTER",
      label = WireField.Label.REQUIRED
  )
  public final BinaryFile binary;

  /**
   * Rating for this release
   */
  @WireField(
      tag = 4,
      adapter = "com.squareup.wire.ProtoAdapter#FLOAT"
  )
  public final Float rating;

  /**
   * Number of times this release is downloaded
   */
  @WireField(
      tag = 5,
      adapter = "com.squareup.wire.ProtoAdapter#INT64"
  )
  public final Long download_count;

  /**
   * URL for the release icon
   */
  @WireField(
      tag = 6,
      adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  public final String icon;

  /**
   * Last time this release is modified in unix time
   */
  @WireField(
      tag = 7,
      adapter = "com.squareup.wire.ProtoAdapter#INT64"
  )
  public final Long date_modified;

  /**
   * The date this version is released in unix time
   */
  @WireField(
      tag = 8,
      adapter = "com.squareup.wire.ProtoAdapter#INT64",
      label = WireField.Label.REQUIRED
  )
  public final Long date_released;

  /**
   * The original URL for the release
   */
  @WireField(
      tag = 9,
      adapter = "com.squareup.wire.ProtoAdapter#STRING",
      label = WireField.Label.REQUIRED
  )
  public final String release_url;

  /**
   * Screen Shots for this release
   */
  @WireField(
      tag = 10,
      adapter = "com.squareup.wire.ProtoAdapter#STRING",
      label = WireField.Label.REPEATED
  )
  public final List<String> screenshots;

  /**
   * Video Tutorials for this release
   */
  @WireField(
      tag = 11,
      adapter = "com.squareup.wire.ProtoAdapter#STRING",
      label = WireField.Label.REPEATED
  )
  public final List<String> tutorials;

  /**
   * Android package name - android specific
   */
  @WireField(
      tag = 12,
      adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  public final String package_name;

  /**
   * Android build version - android specific
   */
  @WireField(
      tag = 13,
      adapter = "com.squareup.wire.ProtoAdapter#INT32"
  )
  public final Integer build_version;

  /**
   * Release specific updates for the user
   */
  @WireField(
      tag = 20,
      adapter = "org.asl19.paskoocheh.proto.Message#ADAPTER",
      label = WireField.Label.REPEATED
  )
  public final List<Message> update;

  public Release(String version, Long date_created, BinaryFile binary, Float rating, Long download_count, String icon, Long date_modified, Long date_released, String release_url, List<String> screenshots, List<String> tutorials, String package_name, Integer build_version, List<Message> update) {
    this(version, date_created, binary, rating, download_count, icon, date_modified, date_released, release_url, screenshots, tutorials, package_name, build_version, update, ByteString.EMPTY);
  }

  public Release(String version, Long date_created, BinaryFile binary, Float rating, Long download_count, String icon, Long date_modified, Long date_released, String release_url, List<String> screenshots, List<String> tutorials, String package_name, Integer build_version, List<Message> update, ByteString unknownFields) {
    super(ADAPTER, unknownFields);
    this.version = version;
    this.date_created = date_created;
    this.binary = binary;
    this.rating = rating;
    this.download_count = download_count;
    this.icon = icon;
    this.date_modified = date_modified;
    this.date_released = date_released;
    this.release_url = release_url;
    this.screenshots = Internal.immutableCopyOf("screenshots", screenshots);
    this.tutorials = Internal.immutableCopyOf("tutorials", tutorials);
    this.package_name = package_name;
    this.build_version = build_version;
    this.update = Internal.immutableCopyOf("update", update);
  }

  @Override
  public Builder newBuilder() {
    Builder builder = new Builder();
    builder.version = version;
    builder.date_created = date_created;
    builder.binary = binary;
    builder.rating = rating;
    builder.download_count = download_count;
    builder.icon = icon;
    builder.date_modified = date_modified;
    builder.date_released = date_released;
    builder.release_url = release_url;
    builder.screenshots = Internal.copyOf("screenshots", screenshots);
    builder.tutorials = Internal.copyOf("tutorials", tutorials);
    builder.package_name = package_name;
    builder.build_version = build_version;
    builder.update = Internal.copyOf("update", update);
    builder.addUnknownFields(unknownFields());
    return builder;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof Release)) return false;
    Release o = (Release) other;
    return Internal.equals(unknownFields(), o.unknownFields())
        && Internal.equals(version, o.version)
        && Internal.equals(date_created, o.date_created)
        && Internal.equals(binary, o.binary)
        && Internal.equals(rating, o.rating)
        && Internal.equals(download_count, o.download_count)
        && Internal.equals(icon, o.icon)
        && Internal.equals(date_modified, o.date_modified)
        && Internal.equals(date_released, o.date_released)
        && Internal.equals(release_url, o.release_url)
        && Internal.equals(screenshots, o.screenshots)
        && Internal.equals(tutorials, o.tutorials)
        && Internal.equals(package_name, o.package_name)
        && Internal.equals(build_version, o.build_version)
        && Internal.equals(update, o.update);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode;
    if (result == 0) {
      result = unknownFields().hashCode();
      result = result * 37 + (version != null ? version.hashCode() : 0);
      result = result * 37 + (date_created != null ? date_created.hashCode() : 0);
      result = result * 37 + (binary != null ? binary.hashCode() : 0);
      result = result * 37 + (rating != null ? rating.hashCode() : 0);
      result = result * 37 + (download_count != null ? download_count.hashCode() : 0);
      result = result * 37 + (icon != null ? icon.hashCode() : 0);
      result = result * 37 + (date_modified != null ? date_modified.hashCode() : 0);
      result = result * 37 + (date_released != null ? date_released.hashCode() : 0);
      result = result * 37 + (release_url != null ? release_url.hashCode() : 0);
      result = result * 37 + (screenshots != null ? screenshots.hashCode() : 1);
      result = result * 37 + (tutorials != null ? tutorials.hashCode() : 1);
      result = result * 37 + (package_name != null ? package_name.hashCode() : 0);
      result = result * 37 + (build_version != null ? build_version.hashCode() : 0);
      result = result * 37 + (update != null ? update.hashCode() : 1);
      super.hashCode = result;
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    if (version != null) builder.append(", version=").append(version);
    if (date_created != null) builder.append(", date_created=").append(date_created);
    if (binary != null) builder.append(", binary=").append(binary);
    if (rating != null) builder.append(", rating=").append(rating);
    if (download_count != null) builder.append(", download_count=").append(download_count);
    if (icon != null) builder.append(", icon=").append(icon);
    if (date_modified != null) builder.append(", date_modified=").append(date_modified);
    if (date_released != null) builder.append(", date_released=").append(date_released);
    if (release_url != null) builder.append(", release_url=").append(release_url);
    if (screenshots != null) builder.append(", screenshots=").append(screenshots);
    if (tutorials != null) builder.append(", tutorials=").append(tutorials);
    if (package_name != null) builder.append(", package_name=").append(package_name);
    if (build_version != null) builder.append(", build_version=").append(build_version);
    if (update != null) builder.append(", update=").append(update);
    return builder.replace(0, 2, "Release{").append('}').toString();
  }

  public static final class Builder extends com.squareup.wire.Message.Builder<Release, Builder> {
    public String version;

    public Long date_created;

    public BinaryFile binary;

    public Float rating;

    public Long download_count;

    public String icon;

    public Long date_modified;

    public Long date_released;

    public String release_url;

    public List<String> screenshots;

    public List<String> tutorials;

    public String package_name;

    public Integer build_version;

    public List<Message> update;

    public Builder() {
      screenshots = Internal.newMutableList();
      tutorials = Internal.newMutableList();
      update = Internal.newMutableList();
    }

    /**
     * Version of the release
     */
    public Builder version(String version) {
      this.version = version;
      return this;
    }

    /**
     * Date the file uploaded in unix time
     */
    public Builder date_created(Long date_created) {
      this.date_created = date_created;
      return this;
    }

    public Builder binary(BinaryFile binary) {
      this.binary = binary;
      return this;
    }

    /**
     * Rating for this release
     */
    public Builder rating(Float rating) {
      this.rating = rating;
      return this;
    }

    /**
     * Number of times this release is downloaded
     */
    public Builder download_count(Long download_count) {
      this.download_count = download_count;
      return this;
    }

    /**
     * URL for the release icon
     */
    public Builder icon(String icon) {
      this.icon = icon;
      return this;
    }

    /**
     * Last time this release is modified in unix time
     */
    public Builder date_modified(Long date_modified) {
      this.date_modified = date_modified;
      return this;
    }

    /**
     * The date this version is released in unix time
     */
    public Builder date_released(Long date_released) {
      this.date_released = date_released;
      return this;
    }

    /**
     * The original URL for the release
     */
    public Builder release_url(String release_url) {
      this.release_url = release_url;
      return this;
    }

    /**
     * Screen Shots for this release
     */
    public Builder screenshots(List<String> screenshots) {
      Internal.checkElementsNotNull(screenshots);
      this.screenshots = screenshots;
      return this;
    }

    /**
     * Video Tutorials for this release
     */
    public Builder tutorials(List<String> tutorials) {
      Internal.checkElementsNotNull(tutorials);
      this.tutorials = tutorials;
      return this;
    }

    /**
     * Android package name - android specific
     */
    public Builder package_name(String package_name) {
      this.package_name = package_name;
      return this;
    }

    /**
     * Android build version - android specific
     */
    public Builder build_version(Integer build_version) {
      this.build_version = build_version;
      return this;
    }

    /**
     * Release specific updates for the user
     */
    public Builder update(List<Message> update) {
      Internal.checkElementsNotNull(update);
      this.update = update;
      return this;
    }

    @Override
    public Release build() {
      if (version == null
          || date_created == null
          || binary == null
          || date_released == null
          || release_url == null) {
        throw Internal.missingRequiredFields(version, "version",
            date_created, "date_created",
            binary, "binary",
            date_released, "date_released",
            release_url, "release_url");
      }
      return new Release(version, date_created, binary, rating, download_count, icon, date_modified, date_released, release_url, screenshots, tutorials, package_name, build_version, update, buildUnknownFields());
    }
  }

  private static final class ProtoAdapter_Release extends ProtoAdapter<Release> {
    ProtoAdapter_Release() {
      super(FieldEncoding.LENGTH_DELIMITED, Release.class);
    }

    @Override
    public int encodedSize(Release value) {
      return ProtoAdapter.STRING.encodedSizeWithTag(1, value.version)
          + ProtoAdapter.INT64.encodedSizeWithTag(2, value.date_created)
          + BinaryFile.ADAPTER.encodedSizeWithTag(3, value.binary)
          + (value.rating != null ? ProtoAdapter.FLOAT.encodedSizeWithTag(4, value.rating) : 0)
          + (value.download_count != null ? ProtoAdapter.INT64.encodedSizeWithTag(5, value.download_count) : 0)
          + (value.icon != null ? ProtoAdapter.STRING.encodedSizeWithTag(6, value.icon) : 0)
          + (value.date_modified != null ? ProtoAdapter.INT64.encodedSizeWithTag(7, value.date_modified) : 0)
          + ProtoAdapter.INT64.encodedSizeWithTag(8, value.date_released)
          + ProtoAdapter.STRING.encodedSizeWithTag(9, value.release_url)
          + ProtoAdapter.STRING.asRepeated().encodedSizeWithTag(10, value.screenshots)
          + ProtoAdapter.STRING.asRepeated().encodedSizeWithTag(11, value.tutorials)
          + (value.package_name != null ? ProtoAdapter.STRING.encodedSizeWithTag(12, value.package_name) : 0)
          + (value.build_version != null ? ProtoAdapter.INT32.encodedSizeWithTag(13, value.build_version) : 0)
          + Message.ADAPTER.asRepeated().encodedSizeWithTag(20, value.update)
          + value.unknownFields().size();
    }

    @Override
    public void encode(ProtoWriter writer, Release value) throws IOException {
      ProtoAdapter.STRING.encodeWithTag(writer, 1, value.version);
      ProtoAdapter.INT64.encodeWithTag(writer, 2, value.date_created);
      BinaryFile.ADAPTER.encodeWithTag(writer, 3, value.binary);
      if (value.rating != null) ProtoAdapter.FLOAT.encodeWithTag(writer, 4, value.rating);
      if (value.download_count != null) ProtoAdapter.INT64.encodeWithTag(writer, 5, value.download_count);
      if (value.icon != null) ProtoAdapter.STRING.encodeWithTag(writer, 6, value.icon);
      if (value.date_modified != null) ProtoAdapter.INT64.encodeWithTag(writer, 7, value.date_modified);
      ProtoAdapter.INT64.encodeWithTag(writer, 8, value.date_released);
      ProtoAdapter.STRING.encodeWithTag(writer, 9, value.release_url);
      if (value.screenshots != null) ProtoAdapter.STRING.asRepeated().encodeWithTag(writer, 10, value.screenshots);
      if (value.tutorials != null) ProtoAdapter.STRING.asRepeated().encodeWithTag(writer, 11, value.tutorials);
      if (value.package_name != null) ProtoAdapter.STRING.encodeWithTag(writer, 12, value.package_name);
      if (value.build_version != null) ProtoAdapter.INT32.encodeWithTag(writer, 13, value.build_version);
      if (value.update != null) Message.ADAPTER.asRepeated().encodeWithTag(writer, 20, value.update);
      writer.writeBytes(value.unknownFields());
    }

    @Override
    public Release decode(ProtoReader reader) throws IOException {
      Builder builder = new Builder();
      long token = reader.beginMessage();
      for (int tag; (tag = reader.nextTag()) != -1;) {
        switch (tag) {
          case 1: builder.version(ProtoAdapter.STRING.decode(reader)); break;
          case 2: builder.date_created(ProtoAdapter.INT64.decode(reader)); break;
          case 3: builder.binary(BinaryFile.ADAPTER.decode(reader)); break;
          case 4: builder.rating(ProtoAdapter.FLOAT.decode(reader)); break;
          case 5: builder.download_count(ProtoAdapter.INT64.decode(reader)); break;
          case 6: builder.icon(ProtoAdapter.STRING.decode(reader)); break;
          case 7: builder.date_modified(ProtoAdapter.INT64.decode(reader)); break;
          case 8: builder.date_released(ProtoAdapter.INT64.decode(reader)); break;
          case 9: builder.release_url(ProtoAdapter.STRING.decode(reader)); break;
          case 10: builder.screenshots.add(ProtoAdapter.STRING.decode(reader)); break;
          case 11: builder.tutorials.add(ProtoAdapter.STRING.decode(reader)); break;
          case 12: builder.package_name(ProtoAdapter.STRING.decode(reader)); break;
          case 13: builder.build_version(ProtoAdapter.INT32.decode(reader)); break;
          case 20: builder.update.add(Message.ADAPTER.decode(reader)); break;
          default: {
            FieldEncoding fieldEncoding = reader.peekFieldEncoding();
            Object value = fieldEncoding.rawProtoAdapter().decode(reader);
            builder.addUnknownField(tag, fieldEncoding, value);
          }
        }
      }
      reader.endMessage(token);
      return builder.build();
    }

    @Override
    public Release redact(Release value) {
      Builder builder = value.newBuilder();
      if (builder.binary != null) builder.binary = BinaryFile.ADAPTER.redact(builder.binary);
      Internal.redactElements(builder.update, Message.ADAPTER);
      builder.clearUnknownFields();
      return builder.build();
    }
  }
}
