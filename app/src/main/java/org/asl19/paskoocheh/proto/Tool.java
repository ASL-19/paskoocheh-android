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

public final class Tool extends com.squareup.wire.Message<Tool, Tool.Builder> {
  public static final ProtoAdapter<Tool> ADAPTER = new ProtoAdapter_Tool();

  private static final long serialVersionUID = 0L;

  public static final Boolean DEFAULT_IS_OPENSOURCE = false;

  public static final Boolean DEFAULT_IS_RECOMMENDED = false;

  public static final Boolean DEFAULT_IS_FEATURED = false;

  public static final Long DEFAULT_TOOL_ID = 0L;

  /**
   * Author of the tool
   */
  @WireField(
      tag = 1,
      adapter = "org.asl19.paskoocheh.Contact#ADAPTER",
      label = WireField.Label.REQUIRED
  )
  public final Contact contact;

  /**
   * Different tool types for a tool
   */
  @WireField(
      tag = 2,
      adapter = "org.asl19.paskoocheh.ToolType#ADAPTER",
      label = WireField.Label.REPEATED
  )
  public final List<ToolType> types;

  /**
   * Different tag for the tool
   */
  @WireField(
      tag = 3,
      adapter = "com.squareup.wire.ProtoAdapter#STRING",
      label = WireField.Label.REPEATED
  )
  public final List<String> tags;

  /**
   * Is tool open source?
   */
  @WireField(
      tag = 4,
      adapter = "com.squareup.wire.ProtoAdapter#BOOL",
      label = WireField.Label.REQUIRED
  )
  public final Boolean is_opensource;

  /**
   * Is tool recommended by ASL19
   */
  @WireField(
      tag = 5,
      adapter = "com.squareup.wire.ProtoAdapter#BOOL",
      label = WireField.Label.REQUIRED
  )
  public final Boolean is_recommended;

  /**
   * Is tool featured on the app?
   */
  @WireField(
      tag = 6,
      adapter = "com.squareup.wire.ProtoAdapter#BOOL",
      label = WireField.Label.REQUIRED
  )
  public final Boolean is_featured;

  @WireField(
      tag = 7,
      adapter = "com.squareup.wire.ProtoAdapter#INT64",
      label = WireField.Label.REQUIRED
  )
  public final Long tool_id;

  /**
   * Multiple FAQ related to this tool
   */
  @WireField(
      tag = 8,
      adapter = "org.asl19.paskoocheh.Faq#ADAPTER",
      label = WireField.Label.REPEATED
  )
  public final List<Faq> faqs;

  /**
   * Vendor of the tool
   */
  @WireField(
      tag = 9,
      adapter = "org.asl19.paskoocheh.Contact#ADAPTER",
      label = WireField.Label.REQUIRED
  )
  public final Contact vendor;

  /**
   * Release/Version information for the tool
   */
  @WireField(
      tag = 10,
      adapter = "org.asl19.paskoocheh.Release#ADAPTER",
      label = WireField.Label.REPEATED
  )
  public final List<Release> releases;

  /**
   * Tool specific updateAvailable for the user
   */
  @WireField(
      tag = 20,
      adapter = "org.asl19.paskoocheh.Message#ADAPTER",
      label = WireField.Label.REPEATED
  )
  public final List<Message> update;

  public Tool(Contact contact, List<ToolType> types, List<String> tags, Boolean is_opensource, Boolean is_recommended, Boolean is_featured, Long tool_id, List<Faq> faqs, Contact vendor, List<Release> releases, List<Message> update) {
    this(contact, types, tags, is_opensource, is_recommended, is_featured, tool_id, faqs, vendor, releases, update, ByteString.EMPTY);
  }

  public Tool(Contact contact, List<ToolType> types, List<String> tags, Boolean is_opensource, Boolean is_recommended, Boolean is_featured, Long tool_id, List<Faq> faqs, Contact vendor, List<Release> releases, List<Message> update, ByteString unknownFields) {
    super(ADAPTER, unknownFields);
    this.contact = contact;
    this.types = Internal.immutableCopyOf("types", types);
    this.tags = Internal.immutableCopyOf("tags", tags);
    this.is_opensource = is_opensource;
    this.is_recommended = is_recommended;
    this.is_featured = is_featured;
    this.tool_id = tool_id;
    this.faqs = Internal.immutableCopyOf("faqs", faqs);
    this.vendor = vendor;
    this.releases = Internal.immutableCopyOf("releases", releases);
    this.update = Internal.immutableCopyOf("update", update);
  }

  @Override
  public Builder newBuilder() {
    Builder builder = new Builder();
    builder.contact = contact;
    builder.types = Internal.copyOf("types", types);
    builder.tags = Internal.copyOf("tags", tags);
    builder.is_opensource = is_opensource;
    builder.is_recommended = is_recommended;
    builder.is_featured = is_featured;
    builder.tool_id = tool_id;
    builder.faqs = Internal.copyOf("faqs", faqs);
    builder.vendor = vendor;
    builder.releases = Internal.copyOf("releases", releases);
    builder.update = Internal.copyOf("update", update);
    builder.addUnknownFields(unknownFields());
    return builder;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof Tool)) return false;
    Tool o = (Tool) other;
    return Internal.equals(unknownFields(), o.unknownFields())
        && Internal.equals(contact, o.contact)
        && Internal.equals(types, o.types)
        && Internal.equals(tags, o.tags)
        && Internal.equals(is_opensource, o.is_opensource)
        && Internal.equals(is_recommended, o.is_recommended)
        && Internal.equals(is_featured, o.is_featured)
        && Internal.equals(tool_id, o.tool_id)
        && Internal.equals(faqs, o.faqs)
        && Internal.equals(vendor, o.vendor)
        && Internal.equals(releases, o.releases)
        && Internal.equals(update, o.update);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode;
    if (result == 0) {
      result = unknownFields().hashCode();
      result = result * 37 + (contact != null ? contact.hashCode() : 0);
      result = result * 37 + (types != null ? types.hashCode() : 1);
      result = result * 37 + (tags != null ? tags.hashCode() : 1);
      result = result * 37 + (is_opensource != null ? is_opensource.hashCode() : 0);
      result = result * 37 + (is_recommended != null ? is_recommended.hashCode() : 0);
      result = result * 37 + (is_featured != null ? is_featured.hashCode() : 0);
      result = result * 37 + (tool_id != null ? tool_id.hashCode() : 0);
      result = result * 37 + (faqs != null ? faqs.hashCode() : 1);
      result = result * 37 + (vendor != null ? vendor.hashCode() : 0);
      result = result * 37 + (releases != null ? releases.hashCode() : 1);
      result = result * 37 + (update != null ? update.hashCode() : 1);
      super.hashCode = result;
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    if (contact != null) builder.append(", contact=").append(contact);
    if (types != null) builder.append(", types=").append(types);
    if (tags != null) builder.append(", tags=").append(tags);
    if (is_opensource != null) builder.append(", is_opensource=").append(is_opensource);
    if (is_recommended != null) builder.append(", is_recommended=").append(is_recommended);
    if (is_featured != null) builder.append(", is_featured=").append(is_featured);
    if (tool_id != null) builder.append(", tool_id=").append(tool_id);
    if (faqs != null) builder.append(", faqs=").append(faqs);
    if (vendor != null) builder.append(", vendor=").append(vendor);
    if (releases != null) builder.append(", releases=").append(releases);
    if (update != null) builder.append(", update=").append(update);
    return builder.replace(0, 2, "Tool{").append('}').toString();
  }

  public static final class Builder extends com.squareup.wire.Message.Builder<Tool, Builder> {
    public Contact contact;

    public List<ToolType> types;

    public List<String> tags;

    public Boolean is_opensource;

    public Boolean is_recommended;

    public Boolean is_featured;

    public Long tool_id;

    public List<Faq> faqs;

    public Contact vendor;

    public List<Release> releases;

    public List<Message> update;

    public Builder() {
      types = Internal.newMutableList();
      tags = Internal.newMutableList();
      faqs = Internal.newMutableList();
      releases = Internal.newMutableList();
      update = Internal.newMutableList();
    }

    /**
     * Author of the tool
     */
    public Builder contact(Contact contact) {
      this.contact = contact;
      return this;
    }

    /**
     * Different tool types for a tool
     */
    public Builder types(List<ToolType> types) {
      Internal.checkElementsNotNull(types);
      this.types = types;
      return this;
    }

    /**
     * Different tag for the tool
     */
    public Builder tags(List<String> tags) {
      Internal.checkElementsNotNull(tags);
      this.tags = tags;
      return this;
    }

    /**
     * Is tool open source?
     */
    public Builder is_opensource(Boolean is_opensource) {
      this.is_opensource = is_opensource;
      return this;
    }

    /**
     * Is tool recommended by ASL19
     */
    public Builder is_recommended(Boolean is_recommended) {
      this.is_recommended = is_recommended;
      return this;
    }

    /**
     * Is tool featured on the app?
     */
    public Builder is_featured(Boolean is_featured) {
      this.is_featured = is_featured;
      return this;
    }

    public Builder tool_id(Long tool_id) {
      this.tool_id = tool_id;
      return this;
    }

    /**
     * Multiple FAQ related to this tool
     */
    public Builder faqs(List<Faq> faqs) {
      Internal.checkElementsNotNull(faqs);
      this.faqs = faqs;
      return this;
    }

    /**
     * Vendor of the tool
     */
    public Builder vendor(Contact vendor) {
      this.vendor = vendor;
      return this;
    }

    /**
     * Release/Version information for the tool
     */
    public Builder releases(List<Release> releases) {
      Internal.checkElementsNotNull(releases);
      this.releases = releases;
      return this;
    }

    /**
     * Tool specific updateAvailable for the user
     */
    public Builder update(List<Message> update) {
      Internal.checkElementsNotNull(update);
      this.update = update;
      return this;
    }

    @Override
    public Tool build() {
      if (contact == null
          || is_opensource == null
          || is_recommended == null
          || is_featured == null
          || tool_id == null
          || vendor == null) {
        throw Internal.missingRequiredFields(contact, "contact",
            is_opensource, "is_opensource",
            is_recommended, "is_recommended",
            is_featured, "is_featured",
            tool_id, "tool_id",
            vendor, "vendor");
      }
      return new Tool(contact, types, tags, is_opensource, is_recommended, is_featured, tool_id, faqs, vendor, releases, update, buildUnknownFields());
    }
  }

  private static final class ProtoAdapter_Tool extends ProtoAdapter<Tool> {
    ProtoAdapter_Tool() {
      super(FieldEncoding.LENGTH_DELIMITED, Tool.class);
    }

    @Override
    public int encodedSize(Tool value) {
      return Contact.ADAPTER.encodedSizeWithTag(1, value.contact)
          + ToolType.ADAPTER.asRepeated().encodedSizeWithTag(2, value.types)
          + ProtoAdapter.STRING.asRepeated().encodedSizeWithTag(3, value.tags)
          + ProtoAdapter.BOOL.encodedSizeWithTag(4, value.is_opensource)
          + ProtoAdapter.BOOL.encodedSizeWithTag(5, value.is_recommended)
          + ProtoAdapter.BOOL.encodedSizeWithTag(6, value.is_featured)
          + ProtoAdapter.INT64.encodedSizeWithTag(7, value.tool_id)
          + Faq.ADAPTER.asRepeated().encodedSizeWithTag(8, value.faqs)
          + Contact.ADAPTER.encodedSizeWithTag(9, value.vendor)
          + Release.ADAPTER.asRepeated().encodedSizeWithTag(10, value.releases)
          + Message.ADAPTER.asRepeated().encodedSizeWithTag(20, value.update)
          + value.unknownFields().size();
    }

    @Override
    public void encode(ProtoWriter writer, Tool value) throws IOException {
      Contact.ADAPTER.encodeWithTag(writer, 1, value.contact);
      if (value.types != null) ToolType.ADAPTER.asRepeated().encodeWithTag(writer, 2, value.types);
      if (value.tags != null) ProtoAdapter.STRING.asRepeated().encodeWithTag(writer, 3, value.tags);
      ProtoAdapter.BOOL.encodeWithTag(writer, 4, value.is_opensource);
      ProtoAdapter.BOOL.encodeWithTag(writer, 5, value.is_recommended);
      ProtoAdapter.BOOL.encodeWithTag(writer, 6, value.is_featured);
      ProtoAdapter.INT64.encodeWithTag(writer, 7, value.tool_id);
      if (value.faqs != null) Faq.ADAPTER.asRepeated().encodeWithTag(writer, 8, value.faqs);
      Contact.ADAPTER.encodeWithTag(writer, 9, value.vendor);
      if (value.releases != null) Release.ADAPTER.asRepeated().encodeWithTag(writer, 10, value.releases);
      if (value.update != null) Message.ADAPTER.asRepeated().encodeWithTag(writer, 20, value.update);
      writer.writeBytes(value.unknownFields());
    }

    @Override
    public Tool decode(ProtoReader reader) throws IOException {
      Builder builder = new Builder();
      long token = reader.beginMessage();
      for (int tag; (tag = reader.nextTag()) != -1;) {
        switch (tag) {
          case 1: builder.contact(Contact.ADAPTER.decode(reader)); break;
          case 2: {
            try {
              builder.types.add(ToolType.ADAPTER.decode(reader));
            } catch (EnumConstantNotFoundException e) {
              builder.addUnknownField(tag, FieldEncoding.VARINT, (long) e.value);
            }
            break;
          }
          case 3: builder.tags.add(ProtoAdapter.STRING.decode(reader)); break;
          case 4: builder.is_opensource(ProtoAdapter.BOOL.decode(reader)); break;
          case 5: builder.is_recommended(ProtoAdapter.BOOL.decode(reader)); break;
          case 6: builder.is_featured(ProtoAdapter.BOOL.decode(reader)); break;
          case 7: builder.tool_id(ProtoAdapter.INT64.decode(reader)); break;
          case 8: builder.faqs.add(Faq.ADAPTER.decode(reader)); break;
          case 9: builder.vendor(Contact.ADAPTER.decode(reader)); break;
          case 10: builder.releases.add(Release.ADAPTER.decode(reader)); break;
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
    public Tool redact(Tool value) {
      Builder builder = value.newBuilder();
      if (builder.contact != null) builder.contact = Contact.ADAPTER.redact(builder.contact);
      Internal.redactElements(builder.faqs, Faq.ADAPTER);
      if (builder.vendor != null) builder.vendor = Contact.ADAPTER.redact(builder.vendor);
      Internal.redactElements(builder.releases, Release.ADAPTER);
      Internal.redactElements(builder.update, Message.ADAPTER);
      builder.clearUnknownFields();
      return builder.build();
    }
  }
}
