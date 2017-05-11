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
 * FAQ for tools
 */
public final class Faq extends Message<Faq, Faq.Builder> {
  public static final ProtoAdapter<Faq> ADAPTER = new ProtoAdapter_Faq();

  private static final long serialVersionUID = 0L;

  public static final String DEFAULT_LANG = "";

  public static final String DEFAULT_QUESTION = "";

  public static final String DEFAULT_ANSWER = "";

  /**
   * Language of the Q and A
   */
  @WireField(
      tag = 1,
      adapter = "com.squareup.wire.ProtoAdapter#STRING",
      label = WireField.Label.REQUIRED
  )
  public final String lang;

  /**
   * Question!
   */
  @WireField(
      tag = 2,
      adapter = "com.squareup.wire.ProtoAdapter#STRING",
      label = WireField.Label.REQUIRED
  )
  public final String question;

  /**
   * Answer!
   */
  @WireField(
      tag = 3,
      adapter = "com.squareup.wire.ProtoAdapter#STRING",
      label = WireField.Label.REQUIRED
  )
  public final String answer;

  public Faq(String lang, String question, String answer) {
    this(lang, question, answer, ByteString.EMPTY);
  }

  public Faq(String lang, String question, String answer, ByteString unknownFields) {
    super(ADAPTER, unknownFields);
    this.lang = lang;
    this.question = question;
    this.answer = answer;
  }

  @Override
  public Builder newBuilder() {
    Builder builder = new Builder();
    builder.lang = lang;
    builder.question = question;
    builder.answer = answer;
    builder.addUnknownFields(unknownFields());
    return builder;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof Faq)) return false;
    Faq o = (Faq) other;
    return Internal.equals(unknownFields(), o.unknownFields())
        && Internal.equals(lang, o.lang)
        && Internal.equals(question, o.question)
        && Internal.equals(answer, o.answer);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode;
    if (result == 0) {
      result = unknownFields().hashCode();
      result = result * 37 + (lang != null ? lang.hashCode() : 0);
      result = result * 37 + (question != null ? question.hashCode() : 0);
      result = result * 37 + (answer != null ? answer.hashCode() : 0);
      super.hashCode = result;
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    if (lang != null) builder.append(", lang=").append(lang);
    if (question != null) builder.append(", question=").append(question);
    if (answer != null) builder.append(", answer=").append(answer);
    return builder.replace(0, 2, "Faq{").append('}').toString();
  }

  public static final class Builder extends Message.Builder<Faq, Builder> {
    public String lang;

    public String question;

    public String answer;

    public Builder() {
    }

    /**
     * Language of the Q and A
     */
    public Builder lang(String lang) {
      this.lang = lang;
      return this;
    }

    /**
     * Question!
     */
    public Builder question(String question) {
      this.question = question;
      return this;
    }

    /**
     * Answer!
     */
    public Builder answer(String answer) {
      this.answer = answer;
      return this;
    }

    @Override
    public Faq build() {
      if (lang == null
          || question == null
          || answer == null) {
        throw Internal.missingRequiredFields(lang, "lang",
            question, "question",
            answer, "answer");
      }
      return new Faq(lang, question, answer, buildUnknownFields());
    }
  }

  private static final class ProtoAdapter_Faq extends ProtoAdapter<Faq> {
    ProtoAdapter_Faq() {
      super(FieldEncoding.LENGTH_DELIMITED, Faq.class);
    }

    @Override
    public int encodedSize(Faq value) {
      return ProtoAdapter.STRING.encodedSizeWithTag(1, value.lang)
          + ProtoAdapter.STRING.encodedSizeWithTag(2, value.question)
          + ProtoAdapter.STRING.encodedSizeWithTag(3, value.answer)
          + value.unknownFields().size();
    }

    @Override
    public void encode(ProtoWriter writer, Faq value) throws IOException {
      ProtoAdapter.STRING.encodeWithTag(writer, 1, value.lang);
      ProtoAdapter.STRING.encodeWithTag(writer, 2, value.question);
      ProtoAdapter.STRING.encodeWithTag(writer, 3, value.answer);
      writer.writeBytes(value.unknownFields());
    }

    @Override
    public Faq decode(ProtoReader reader) throws IOException {
      Builder builder = new Builder();
      long token = reader.beginMessage();
      for (int tag; (tag = reader.nextTag()) != -1;) {
        switch (tag) {
          case 1: builder.lang(ProtoAdapter.STRING.decode(reader)); break;
          case 2: builder.question(ProtoAdapter.STRING.decode(reader)); break;
          case 3: builder.answer(ProtoAdapter.STRING.decode(reader)); break;
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
    public Faq redact(Faq value) {
      Builder builder = value.newBuilder();
      builder.clearUnknownFields();
      return builder.build();
    }
  }
}
