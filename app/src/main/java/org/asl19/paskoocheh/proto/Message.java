package org.asl19.paskoocheh.proto;

import com.squareup.wire.FieldEncoding;
import com.squareup.wire.ProtoAdapter;
import com.squareup.wire.ProtoReader;
import com.squareup.wire.ProtoWriter;
import com.squareup.wire.WireField;
import com.squareup.wire.internal.Internal;

import java.io.IOException;

import okio.ByteString;

/**
 * Update messages to be sent to the user
 */
public final class Message extends com.squareup.wire.Message<Message, Message.Builder> {
  public static final ProtoAdapter<Message> ADAPTER = new ProtoAdapter_Message();

  private static final long serialVersionUID = 0L;

  public static final String DEFAULT_TEXT = "";

  public static final Long DEFAULT_DATE_PUBLISHED = 0L;

  public static final PriorityLevel DEFAULT_PRIORITY = PriorityLevel.HIGH;

  /**
   * Text of the message
   */
  @WireField(
      tag = 1,
      adapter = "com.squareup.wire.ProtoAdapter#STRING",
      label = WireField.Label.REQUIRED
  )
  public final String text;

  /**
   * Date the message published
   */
  @WireField(
      tag = 2,
      adapter = "com.squareup.wire.ProtoAdapter#INT64",
      label = WireField.Label.REQUIRED
  )
  public final Long date_published;

  /**
   * Priority of the message
   */
  @WireField(
      tag = 3,
      adapter = "org.asl19.paskoocheh.PriorityLevel#ADAPTER",
      label = WireField.Label.REQUIRED
  )
  public final PriorityLevel priority;

  public Message(String text, Long date_published, PriorityLevel priority) {
    this(text, date_published, priority, ByteString.EMPTY);
  }

  public Message(String text, Long date_published, PriorityLevel priority, ByteString unknownFields) {
    super(ADAPTER, unknownFields);
    this.text = text;
    this.date_published = date_published;
    this.priority = priority;
  }

  @Override
  public Builder newBuilder() {
    Builder builder = new Builder();
    builder.text = text;
    builder.date_published = date_published;
    builder.priority = priority;
    builder.addUnknownFields(unknownFields());
    return builder;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof Message)) return false;
    Message o = (Message) other;
    return Internal.equals(unknownFields(), o.unknownFields())
        && Internal.equals(text, o.text)
        && Internal.equals(date_published, o.date_published)
        && Internal.equals(priority, o.priority);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode;
    if (result == 0) {
      result = unknownFields().hashCode();
      result = result * 37 + (text != null ? text.hashCode() : 0);
      result = result * 37 + (date_published != null ? date_published.hashCode() : 0);
      result = result * 37 + (priority != null ? priority.hashCode() : 0);
      super.hashCode = result;
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    if (text != null) builder.append(", text=").append(text);
    if (date_published != null) builder.append(", date_published=").append(date_published);
    if (priority != null) builder.append(", priority=").append(priority);
    return builder.replace(0, 2, "Message{").append('}').toString();
  }

  public static final class Builder extends com.squareup.wire.Message.Builder<Message, Builder> {
    public String text;

    public Long date_published;

    public PriorityLevel priority;

    public Builder() {
    }

    /**
     * Text of the message
     */
    public Builder text(String text) {
      this.text = text;
      return this;
    }

    /**
     * Date the message published
     */
    public Builder date_published(Long date_published) {
      this.date_published = date_published;
      return this;
    }

    /**
     * Priority of the message
     */
    public Builder priority(PriorityLevel priority) {
      this.priority = priority;
      return this;
    }

    @Override
    public Message build() {
      if (text == null
          || date_published == null
          || priority == null) {
        throw Internal.missingRequiredFields(text, "text",
            date_published, "date_published",
            priority, "priority");
      }
      return new Message(text, date_published, priority, buildUnknownFields());
    }
  }

  private static final class ProtoAdapter_Message extends ProtoAdapter<Message> {
    ProtoAdapter_Message() {
      super(FieldEncoding.LENGTH_DELIMITED, Message.class);
    }

    @Override
    public int encodedSize(Message value) {
      return ProtoAdapter.STRING.encodedSizeWithTag(1, value.text)
          + ProtoAdapter.INT64.encodedSizeWithTag(2, value.date_published)
          + PriorityLevel.ADAPTER.encodedSizeWithTag(3, value.priority)
          + value.unknownFields().size();
    }

    @Override
    public void encode(ProtoWriter writer, Message value) throws IOException {
      ProtoAdapter.STRING.encodeWithTag(writer, 1, value.text);
      ProtoAdapter.INT64.encodeWithTag(writer, 2, value.date_published);
      PriorityLevel.ADAPTER.encodeWithTag(writer, 3, value.priority);
      writer.writeBytes(value.unknownFields());
    }

    @Override
    public Message decode(ProtoReader reader) throws IOException {
      Builder builder = new Builder();
      long token = reader.beginMessage();
      for (int tag; (tag = reader.nextTag()) != -1;) {
        switch (tag) {
          case 1: builder.text(ProtoAdapter.STRING.decode(reader)); break;
          case 2: builder.date_published(ProtoAdapter.INT64.decode(reader)); break;
          case 3: {
            try {
              builder.priority(PriorityLevel.ADAPTER.decode(reader));
            } catch (EnumConstantNotFoundException e) {
              builder.addUnknownField(tag, FieldEncoding.VARINT, (long) e.value);
            }
            break;
          }
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
    public Message redact(Message value) {
      Builder builder = value.newBuilder();
      builder.clearUnknownFields();
      return builder.build();
    }
  }
}
