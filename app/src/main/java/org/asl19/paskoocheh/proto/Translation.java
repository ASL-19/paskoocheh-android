package org.asl19.paskoocheh.proto;

import com.squareup.wire.FieldEncoding;
import com.squareup.wire.Message;
import com.squareup.wire.ProtoAdapter;
import com.squareup.wire.ProtoReader;
import com.squareup.wire.ProtoWriter;
import com.squareup.wire.WireField;
import com.squareup.wire.internal.Internal;

import java.io.IOException;

import okio.ByteString;

/**
 * Fields that can have translations in different languages
 */
public final class Translation extends Message<Translation, Translation.Builder> {
  public static final ProtoAdapter<Translation> ADAPTER = new ProtoAdapter_Translation();

  private static final long serialVersionUID = 0L;

  public static final String DEFAULT_LANG = "";

  public static final String DEFAULT_NAME = "";

  public static final String DEFAULT_DESCRIPTION = "";

  /**
   * Language of the translation
   */
  @WireField(
      tag = 1,
      adapter = "com.squareup.wire.ProtoAdapter#STRING",
      label = WireField.Label.REQUIRED
  )
  public final String lang;

  /**
   * Name if the tool in the lang
   */
  @WireField(
      tag = 2,
      adapter = "com.squareup.wire.ProtoAdapter#STRING",
      label = WireField.Label.REQUIRED
  )
  public final String name;

  /**
   * Description for the tool in the lang
   */
  @WireField(
      tag = 3,
      adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  public final String description;

  public Translation(String lang, String name, String description) {
    this(lang, name, description, ByteString.EMPTY);
  }

  public Translation(String lang, String name, String description, ByteString unknownFields) {
    super(ADAPTER, unknownFields);
    this.lang = lang;
    this.name = name;
    this.description = description;
  }

  @Override
  public Builder newBuilder() {
    Builder builder = new Builder();
    builder.lang = lang;
    builder.name = name;
    builder.description = description;
    builder.addUnknownFields(unknownFields());
    return builder;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof Translation)) return false;
    Translation o = (Translation) other;
    return Internal.equals(unknownFields(), o.unknownFields())
        && Internal.equals(lang, o.lang)
        && Internal.equals(name, o.name)
        && Internal.equals(description, o.description);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode;
    if (result == 0) {
      result = unknownFields().hashCode();
      result = result * 37 + (lang != null ? lang.hashCode() : 0);
      result = result * 37 + (name != null ? name.hashCode() : 0);
      result = result * 37 + (description != null ? description.hashCode() : 0);
      super.hashCode = result;
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    if (lang != null) builder.append(", lang=").append(lang);
    if (name != null) builder.append(", name=").append(name);
    if (description != null) builder.append(", description=").append(description);
    return builder.replace(0, 2, "Translation{").append('}').toString();
  }

  public static final class Builder extends Message.Builder<Translation, Builder> {
    public String lang;

    public String name;

    public String description;

    public Builder() {
    }

    /**
     * Language of the translation
     */
    public Builder lang(String lang) {
      this.lang = lang;
      return this;
    }

    /**
     * Name if the tool in the lang
     */
    public Builder name(String name) {
      this.name = name;
      return this;
    }

    /**
     * Description for the tool in the lang
     */
    public Builder description(String description) {
      this.description = description;
      return this;
    }

    @Override
    public Translation build() {
      if (lang == null
          || name == null) {
        throw Internal.missingRequiredFields(lang, "lang",
            name, "name");
      }
      return new Translation(lang, name, description, buildUnknownFields());
    }
  }

  private static final class ProtoAdapter_Translation extends ProtoAdapter<Translation> {
    ProtoAdapter_Translation() {
      super(FieldEncoding.LENGTH_DELIMITED, Translation.class);
    }

    @Override
    public int encodedSize(Translation value) {
      return ProtoAdapter.STRING.encodedSizeWithTag(1, value.lang)
          + ProtoAdapter.STRING.encodedSizeWithTag(2, value.name)
          + (value.description != null ? ProtoAdapter.STRING.encodedSizeWithTag(3, value.description) : 0)
          + value.unknownFields().size();
    }

    @Override
    public void encode(ProtoWriter writer, Translation value) throws IOException {
      ProtoAdapter.STRING.encodeWithTag(writer, 1, value.lang);
      ProtoAdapter.STRING.encodeWithTag(writer, 2, value.name);
      if (value.description != null) ProtoAdapter.STRING.encodeWithTag(writer, 3, value.description);
      writer.writeBytes(value.unknownFields());
    }

    @Override
    public Translation decode(ProtoReader reader) throws IOException {
      Builder builder = new Builder();
      long token = reader.beginMessage();
      for (int tag; (tag = reader.nextTag()) != -1;) {
        switch (tag) {
          case 1: builder.lang(ProtoAdapter.STRING.decode(reader)); break;
          case 2: builder.name(ProtoAdapter.STRING.decode(reader)); break;
          case 3: builder.description(ProtoAdapter.STRING.decode(reader)); break;
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
    public Translation redact(Translation value) {
      Builder builder = value.newBuilder();
      builder.clearUnknownFields();
      return builder.build();
    }
  }
}
