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

public final class Config extends com.squareup.wire.Message<Config, Config.Builder> {
  public static final ProtoAdapter<Config> ADAPTER = new ProtoAdapter_Config();

  private static final long serialVersionUID = 0L;

  public static final String DEFAULT_BUCKET = "";

  public static final String DEFAULT_VERSION = "";

  /**
   * Address of the S3 bucket
   */
  @WireField(
      tag = 1,
      adapter = "com.squareup.wire.ProtoAdapter#STRING",
      label = WireField.Label.REQUIRED
  )
  public final String bucket;

  /**
   * Configuration version
   */
  @WireField(
      tag = 2,
      adapter = "com.squareup.wire.ProtoAdapter#STRING",
      label = WireField.Label.REQUIRED
  )
  public final String version;

  /**
   * Top level Paskoocheh images
   */
  @WireField(
      tag = 3,
      adapter = "com.squareup.wire.ProtoAdapter#STRING",
      label = WireField.Label.REPEATED
  )
  public final List<String> images;

  /**
   * Platform of the tool
   */
  @WireField(
      tag = 4,
      adapter = "org.asl19.paskoocheh.proto.Platform#ADAPTER",
      label = WireField.Label.REPEATED
  )
  public final List<Platform> platforms;

  /**
   * Update messages that are general for all users
   */
  @WireField(
      tag = 20,
      adapter = "org.asl19.paskoocheh.proto.Message#ADAPTER",
      label = WireField.Label.REPEATED
  )
  public final List<Message> update;

  public Config(String bucket, String version, List<String> images, List<Platform> platforms, List<Message> update) {
    this(bucket, version, images, platforms, update, ByteString.EMPTY);
  }

  public Config(String bucket, String version, List<String> images, List<Platform> platforms, List<Message> update, ByteString unknownFields) {
    super(ADAPTER, unknownFields);
    this.bucket = bucket;
    this.version = version;
    this.images = Internal.immutableCopyOf("images", images);
    this.platforms = Internal.immutableCopyOf("platforms", platforms);
    this.update = Internal.immutableCopyOf("update", update);
  }

  @Override
  public Builder newBuilder() {
    Builder builder = new Builder();
    builder.bucket = bucket;
    builder.version = version;
    builder.images = Internal.copyOf("images", images);
    builder.platforms = Internal.copyOf("platforms", platforms);
    builder.update = Internal.copyOf("update", update);
    builder.addUnknownFields(unknownFields());
    return builder;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof Config)) return false;
    Config o = (Config) other;
    return Internal.equals(unknownFields(), o.unknownFields())
        && Internal.equals(bucket, o.bucket)
        && Internal.equals(version, o.version)
        && Internal.equals(images, o.images)
        && Internal.equals(platforms, o.platforms)
        && Internal.equals(update, o.update);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode;
    if (result == 0) {
      result = unknownFields().hashCode();
      result = result * 37 + (bucket != null ? bucket.hashCode() : 0);
      result = result * 37 + (version != null ? version.hashCode() : 0);
      result = result * 37 + (images != null ? images.hashCode() : 1);
      result = result * 37 + (platforms != null ? platforms.hashCode() : 1);
      result = result * 37 + (update != null ? update.hashCode() : 1);
      super.hashCode = result;
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    if (bucket != null) builder.append(", bucket=").append(bucket);
    if (version != null) builder.append(", version=").append(version);
    if (images != null) builder.append(", images=").append(images);
    if (platforms != null) builder.append(", platforms=").append(platforms);
    if (update != null) builder.append(", update=").append(update);
    return builder.replace(0, 2, "Config{").append('}').toString();
  }

  public static final class Builder extends com.squareup.wire.Message.Builder<Config, Builder> {
    public String bucket;

    public String version;

    public List<String> images;

    public List<Platform> platforms;

    public List<Message> update;

    public Builder() {
      images = Internal.newMutableList();
      platforms = Internal.newMutableList();
      update = Internal.newMutableList();
    }

    /**
     * Address of the S3 bucket
     */
    public Builder bucket(String bucket) {
      this.bucket = bucket;
      return this;
    }

    /**
     * Configuration version
     */
    public Builder version(String version) {
      this.version = version;
      return this;
    }

    /**
     * Top level Paskoocheh images
     */
    public Builder images(List<String> images) {
      Internal.checkElementsNotNull(images);
      this.images = images;
      return this;
    }

    /**
     * Platform of the tool
     */
    public Builder platforms(List<Platform> platforms) {
      Internal.checkElementsNotNull(platforms);
      this.platforms = platforms;
      return this;
    }

    /**
     * Update messages that are general for all users
     */
    public Builder update(List<Message> update) {
      Internal.checkElementsNotNull(update);
      this.update = update;
      return this;
    }

    @Override
    public Config build() {
      if (bucket == null
          || version == null) {
        throw Internal.missingRequiredFields(bucket, "bucket",
            version, "version");
      }
      return new Config(bucket, version, images, platforms, update, buildUnknownFields());
    }
  }

  private static final class ProtoAdapter_Config extends ProtoAdapter<Config> {
    ProtoAdapter_Config() {
      super(FieldEncoding.LENGTH_DELIMITED, Config.class);
    }

    @Override
    public int encodedSize(Config value) {
      return ProtoAdapter.STRING.encodedSizeWithTag(1, value.bucket)
          + ProtoAdapter.STRING.encodedSizeWithTag(2, value.version)
          + ProtoAdapter.STRING.asRepeated().encodedSizeWithTag(3, value.images)
          + Platform.ADAPTER.asRepeated().encodedSizeWithTag(4, value.platforms)
          + Message.ADAPTER.asRepeated().encodedSizeWithTag(20, value.update)
          + value.unknownFields().size();
    }

    @Override
    public void encode(ProtoWriter writer, Config value) throws IOException {
      ProtoAdapter.STRING.encodeWithTag(writer, 1, value.bucket);
      ProtoAdapter.STRING.encodeWithTag(writer, 2, value.version);
      if (value.images != null) ProtoAdapter.STRING.asRepeated().encodeWithTag(writer, 3, value.images);
      if (value.platforms != null) Platform.ADAPTER.asRepeated().encodeWithTag(writer, 4, value.platforms);
      if (value.update != null) Message.ADAPTER.asRepeated().encodeWithTag(writer, 20, value.update);
      writer.writeBytes(value.unknownFields());
    }

    @Override
    public Config decode(ProtoReader reader) throws IOException {
      Builder builder = new Builder();
      long token = reader.beginMessage();
      for (int tag; (tag = reader.nextTag()) != -1;) {
        switch (tag) {
          case 1: builder.bucket(ProtoAdapter.STRING.decode(reader)); break;
          case 2: builder.version(ProtoAdapter.STRING.decode(reader)); break;
          case 3: builder.images.add(ProtoAdapter.STRING.decode(reader)); break;
          case 4: builder.platforms.add(Platform.ADAPTER.decode(reader)); break;
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
    public Config redact(Config value) {
      Builder builder = value.newBuilder();
      Internal.redactElements(builder.platforms, Platform.ADAPTER);
      Internal.redactElements(builder.update, Message.ADAPTER);
      builder.clearUnknownFields();
      return builder.build();
    }
  }
}
