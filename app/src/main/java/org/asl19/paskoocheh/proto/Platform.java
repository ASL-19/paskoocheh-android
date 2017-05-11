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

public final class Platform extends com.squareup.wire.Message<Platform, Platform.Builder> {
  public static final ProtoAdapter<Platform> ADAPTER = new ProtoAdapter_Platform();

  private static final long serialVersionUID = 0L;

  public static final PlatformName DEFAULT_NAME = PlatformName.WINDOWS;

  public static final PlatformType DEFAULT_TYPE = PlatformType.DESKTOP;

  /**
   * Platform name
   */
  @WireField(
      tag = 1,
      adapter = "org.asl19.paskoocheh.proto.PlatformName#ADAPTER",
      label = WireField.Label.REQUIRED
  )
  public final PlatformName name;

  /**
   * Platform type
   */
  @WireField(
      tag = 2,
      adapter = "org.asl19.paskoocheh.proto.PlatformType#ADAPTER",
      label = WireField.Label.REQUIRED
  )
  public final PlatformType type;

  /**
   * Different tools for this platform
   */
  @WireField(
      tag = 3,
      adapter = "org.asl19.paskoocheh.proto.Tool#ADAPTER",
      label = WireField.Label.REPEATED
  )
  public final List<Tool> tools;

  /**
   * Update messages that are platform specific
   */
  @WireField(
      tag = 20,
      adapter = "org.asl19.paskoocheh.proto.Message#ADAPTER",
      label = WireField.Label.REPEATED
  )
  public final List<Message> update;

  public Platform(PlatformName name, PlatformType type, List<Tool> tools, List<Message> update) {
    this(name, type, tools, update, ByteString.EMPTY);
  }

  public Platform(PlatformName name, PlatformType type, List<Tool> tools, List<Message> update, ByteString unknownFields) {
    super(ADAPTER, unknownFields);
    this.name = name;
    this.type = type;
    this.tools = Internal.immutableCopyOf("tools", tools);
    this.update = Internal.immutableCopyOf("update", update);
  }

  @Override
  public Builder newBuilder() {
    Builder builder = new Builder();
    builder.name = name;
    builder.type = type;
    builder.tools = Internal.copyOf("tools", tools);
    builder.update = Internal.copyOf("update", update);
    builder.addUnknownFields(unknownFields());
    return builder;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof Platform)) return false;
    Platform o = (Platform) other;
    return Internal.equals(unknownFields(), o.unknownFields())
        && Internal.equals(name, o.name)
        && Internal.equals(type, o.type)
        && Internal.equals(tools, o.tools)
        && Internal.equals(update, o.update);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode;
    if (result == 0) {
      result = unknownFields().hashCode();
      result = result * 37 + (name != null ? name.hashCode() : 0);
      result = result * 37 + (type != null ? type.hashCode() : 0);
      result = result * 37 + (tools != null ? tools.hashCode() : 1);
      result = result * 37 + (update != null ? update.hashCode() : 1);
      super.hashCode = result;
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    if (name != null) builder.append(", name=").append(name);
    if (type != null) builder.append(", type=").append(type);
    if (tools != null) builder.append(", tools=").append(tools);
    if (update != null) builder.append(", update=").append(update);
    return builder.replace(0, 2, "Platform{").append('}').toString();
  }

  public static final class Builder extends com.squareup.wire.Message.Builder<Platform, Builder> {
    public PlatformName name;

    public PlatformType type;

    public List<Tool> tools;

    public List<Message> update;

    public Builder() {
      tools = Internal.newMutableList();
      update = Internal.newMutableList();
    }

    /**
     * Platform name
     */
    public Builder name(PlatformName name) {
      this.name = name;
      return this;
    }

    /**
     * Platform type
     */
    public Builder type(PlatformType type) {
      this.type = type;
      return this;
    }

    /**
     * Different tools for this platform
     */
    public Builder tools(List<Tool> tools) {
      Internal.checkElementsNotNull(tools);
      this.tools = tools;
      return this;
    }

    /**
     * Update messages that are platform specific
     */
    public Builder update(List<Message> update) {
      Internal.checkElementsNotNull(update);
      this.update = update;
      return this;
    }

    @Override
    public Platform build() {
      if (name == null
          || type == null) {
        throw Internal.missingRequiredFields(name, "name",
            type, "type");
      }
      return new Platform(name, type, tools, update, buildUnknownFields());
    }
  }

  private static final class ProtoAdapter_Platform extends ProtoAdapter<Platform> {
    ProtoAdapter_Platform() {
      super(FieldEncoding.LENGTH_DELIMITED, Platform.class);
    }

    @Override
    public int encodedSize(Platform value) {
      return PlatformName.ADAPTER.encodedSizeWithTag(1, value.name)
          + PlatformType.ADAPTER.encodedSizeWithTag(2, value.type)
          + Tool.ADAPTER.asRepeated().encodedSizeWithTag(3, value.tools)
          + Message.ADAPTER.asRepeated().encodedSizeWithTag(20, value.update)
          + value.unknownFields().size();
    }

    @Override
    public void encode(ProtoWriter writer, Platform value) throws IOException {
      PlatformName.ADAPTER.encodeWithTag(writer, 1, value.name);
      PlatformType.ADAPTER.encodeWithTag(writer, 2, value.type);
      if (value.tools != null) Tool.ADAPTER.asRepeated().encodeWithTag(writer, 3, value.tools);
      if (value.update != null) Message.ADAPTER.asRepeated().encodeWithTag(writer, 20, value.update);
      writer.writeBytes(value.unknownFields());
    }

    @Override
    public Platform decode(ProtoReader reader) throws IOException {
      Builder builder = new Builder();
      long token = reader.beginMessage();
      for (int tag; (tag = reader.nextTag()) != -1;) {
        switch (tag) {
          case 1: {
            try {
              builder.name(PlatformName.ADAPTER.decode(reader));
            } catch (EnumConstantNotFoundException e) {
              builder.addUnknownField(tag, FieldEncoding.VARINT, (long) e.value);
            }
            break;
          }
          case 2: {
            try {
              builder.type(PlatformType.ADAPTER.decode(reader));
            } catch (EnumConstantNotFoundException e) {
              builder.addUnknownField(tag, FieldEncoding.VARINT, (long) e.value);
            }
            break;
          }
          case 3: builder.tools.add(Tool.ADAPTER.decode(reader)); break;
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
    public Platform redact(Platform value) {
      Builder builder = value.newBuilder();
      Internal.redactElements(builder.tools, Tool.ADAPTER);
      Internal.redactElements(builder.update, Message.ADAPTER);
      builder.clearUnknownFields();
      return builder.build();
    }
  }
}
