package org.asl19.paskoocheh.proto;

import com.squareup.wire.FieldEncoding;
import com.squareup.wire.Message;
import com.squareup.wire.ProtoAdapter;
import com.squareup.wire.ProtoReader;
import com.squareup.wire.ProtoWriter;
import com.squareup.wire.WireField;
import com.squareup.wire.internal.Internal;

import java.io.IOException;
import java.util.List;

import okio.ByteString;

/**
 * A message to hold info for an online Entity
 * Used for tool author and tool vendor
 */
public final class Contact extends Message<Contact, Contact.Builder> {
  public static final ProtoAdapter<Contact> ADAPTER = new ProtoAdapter_Contact();

  private static final long serialVersionUID = 0L;

  public static final String DEFAULT_NAME = "";

  public static final String DEFAULT_WEBSITE_URL = "";

  public static final String DEFAULT_USER_SUPPORT_URL = "";

  public static final String DEFAULT_SUPPORT_EMAIL = "";

  public static final String DEFAULT_BLOG_URL = "";

  public static final String DEFAULT_FACEBOOK_URL = "";

  public static final String DEFAULT_TWITTER_HANDLE = "";

  public static final String DEFAULT_FEED_URL = "";

  public static final String DEFAULT_MAIL_RESPONDER_EMAIL = "";

  public static final String DEFAULT_SOURCE_URL = "";

  public static final String DEFAULT_DESCRIPTION = "";

  /**
   * Name of the entity
   */
  @WireField(
      tag = 1,
      adapter = "com.squareup.wire.ProtoAdapter#STRING",
      label = WireField.Label.REQUIRED
  )
  public final String name;

  /**
   * Website URL
   */
  @WireField(
      tag = 2,
      adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  public final String website_url;

  /**
   * URL for user support
   */
  @WireField(
      tag = 3,
      adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  public final String user_support_url;

  /**
   * Email for user support
   */
  @WireField(
      tag = 4,
      adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  public final String support_email;

  /**
   * Blog URL
   */
  @WireField(
      tag = 5,
      adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  public final String blog_url;

  /**
   * Facebook page URL
   */
  @WireField(
      tag = 6,
      adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  public final String facebook_url;

  /**
   * Twitter URL
   */
  @WireField(
      tag = 7,
      adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  public final String twitter_handle;

  /**
   * RSS Feed
   */
  @WireField(
      tag = 8,
      adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  public final String feed_url;

  /**
   * Email Address for tool delivery
   */
  @WireField(
      tag = 9,
      adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  public final String mail_responder_email;

  /**
   * URL of the open source repo
   */
  @WireField(
      tag = 10,
      adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  public final String source_url;

  /**
   * Description text for the entity
   */
  @WireField(
      tag = 11,
      adapter = "com.squareup.wire.ProtoAdapter#STRING"
  )
  public final String description;

  /**
   * Language specific information
   */
  @WireField(
      tag = 12,
      adapter = "org.asl19.paskoocheh.proto.Translation#ADAPTER",
      label = WireField.Label.REPEATED
  )
  public final List<Translation> translation;

  public Contact(String name, String website_url, String user_support_url, String support_email, String blog_url, String facebook_url, String twitter_handle, String feed_url, String mail_responder_email, String source_url, String description, List<Translation> translation) {
    this(name, website_url, user_support_url, support_email, blog_url, facebook_url, twitter_handle, feed_url, mail_responder_email, source_url, description, translation, ByteString.EMPTY);
  }

  public Contact(String name, String website_url, String user_support_url, String support_email, String blog_url, String facebook_url, String twitter_handle, String feed_url, String mail_responder_email, String source_url, String description, List<Translation> translation, ByteString unknownFields) {
    super(ADAPTER, unknownFields);
    this.name = name;
    this.website_url = website_url;
    this.user_support_url = user_support_url;
    this.support_email = support_email;
    this.blog_url = blog_url;
    this.facebook_url = facebook_url;
    this.twitter_handle = twitter_handle;
    this.feed_url = feed_url;
    this.mail_responder_email = mail_responder_email;
    this.source_url = source_url;
    this.description = description;
    this.translation = Internal.immutableCopyOf("translation", translation);
  }

  @Override
  public Builder newBuilder() {
    Builder builder = new Builder();
    builder.name = name;
    builder.website_url = website_url;
    builder.user_support_url = user_support_url;
    builder.support_email = support_email;
    builder.blog_url = blog_url;
    builder.facebook_url = facebook_url;
    builder.twitter_handle = twitter_handle;
    builder.feed_url = feed_url;
    builder.mail_responder_email = mail_responder_email;
    builder.source_url = source_url;
    builder.description = description;
    builder.translation = Internal.copyOf("translation", translation);
    builder.addUnknownFields(unknownFields());
    return builder;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof Contact)) return false;
    Contact o = (Contact) other;
    return Internal.equals(unknownFields(), o.unknownFields())
        && Internal.equals(name, o.name)
        && Internal.equals(website_url, o.website_url)
        && Internal.equals(user_support_url, o.user_support_url)
        && Internal.equals(support_email, o.support_email)
        && Internal.equals(blog_url, o.blog_url)
        && Internal.equals(facebook_url, o.facebook_url)
        && Internal.equals(twitter_handle, o.twitter_handle)
        && Internal.equals(feed_url, o.feed_url)
        && Internal.equals(mail_responder_email, o.mail_responder_email)
        && Internal.equals(source_url, o.source_url)
        && Internal.equals(description, o.description)
        && Internal.equals(translation, o.translation);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode;
    if (result == 0) {
      result = unknownFields().hashCode();
      result = result * 37 + (name != null ? name.hashCode() : 0);
      result = result * 37 + (website_url != null ? website_url.hashCode() : 0);
      result = result * 37 + (user_support_url != null ? user_support_url.hashCode() : 0);
      result = result * 37 + (support_email != null ? support_email.hashCode() : 0);
      result = result * 37 + (blog_url != null ? blog_url.hashCode() : 0);
      result = result * 37 + (facebook_url != null ? facebook_url.hashCode() : 0);
      result = result * 37 + (twitter_handle != null ? twitter_handle.hashCode() : 0);
      result = result * 37 + (feed_url != null ? feed_url.hashCode() : 0);
      result = result * 37 + (mail_responder_email != null ? mail_responder_email.hashCode() : 0);
      result = result * 37 + (source_url != null ? source_url.hashCode() : 0);
      result = result * 37 + (description != null ? description.hashCode() : 0);
      result = result * 37 + (translation != null ? translation.hashCode() : 1);
      super.hashCode = result;
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    if (name != null) builder.append(", name=").append(name);
    if (website_url != null) builder.append(", website_url=").append(website_url);
    if (user_support_url != null) builder.append(", user_support_url=").append(user_support_url);
    if (support_email != null) builder.append(", support_email=").append(support_email);
    if (blog_url != null) builder.append(", blog_url=").append(blog_url);
    if (facebook_url != null) builder.append(", facebook_url=").append(facebook_url);
    if (twitter_handle != null) builder.append(", twitter_handle=").append(twitter_handle);
    if (feed_url != null) builder.append(", feed_url=").append(feed_url);
    if (mail_responder_email != null) builder.append(", mail_responder_email=").append(mail_responder_email);
    if (source_url != null) builder.append(", source_url=").append(source_url);
    if (description != null) builder.append(", description=").append(description);
    if (translation != null) builder.append(", translation=").append(translation);
    return builder.replace(0, 2, "Contact{").append('}').toString();
  }

  public static final class Builder extends Message.Builder<Contact, Builder> {
    public String name;

    public String website_url;

    public String user_support_url;

    public String support_email;

    public String blog_url;

    public String facebook_url;

    public String twitter_handle;

    public String feed_url;

    public String mail_responder_email;

    public String source_url;

    public String description;

    public List<Translation> translation;

    public Builder() {
      translation = Internal.newMutableList();
    }

    /**
     * Name of the entity
     */
    public Builder name(String name) {
      this.name = name;
      return this;
    }

    /**
     * Website URL
     */
    public Builder website_url(String website_url) {
      this.website_url = website_url;
      return this;
    }

    /**
     * URL for user support
     */
    public Builder user_support_url(String user_support_url) {
      this.user_support_url = user_support_url;
      return this;
    }

    /**
     * Email for user support
     */
    public Builder support_email(String support_email) {
      this.support_email = support_email;
      return this;
    }

    /**
     * Blog URL
     */
    public Builder blog_url(String blog_url) {
      this.blog_url = blog_url;
      return this;
    }

    /**
     * Facebook page URL
     */
    public Builder facebook_url(String facebook_url) {
      this.facebook_url = facebook_url;
      return this;
    }

    /**
     * Twitter URL
     */
    public Builder twitter_handle(String twitter_handle) {
      this.twitter_handle = twitter_handle;
      return this;
    }

    /**
     * RSS Feed
     */
    public Builder feed_url(String feed_url) {
      this.feed_url = feed_url;
      return this;
    }

    /**
     * Email Address for tool delivery
     */
    public Builder mail_responder_email(String mail_responder_email) {
      this.mail_responder_email = mail_responder_email;
      return this;
    }

    /**
     * URL of the open source repo
     */
    public Builder source_url(String source_url) {
      this.source_url = source_url;
      return this;
    }

    /**
     * Description text for the entity
     */
    public Builder description(String description) {
      this.description = description;
      return this;
    }

    /**
     * Language specific information
     */
    public Builder translation(List<Translation> translation) {
      Internal.checkElementsNotNull(translation);
      this.translation = translation;
      return this;
    }

    @Override
    public Contact build() {
      if (name == null) {
        throw Internal.missingRequiredFields(name, "name");
      }
      return new Contact(name, website_url, user_support_url, support_email, blog_url, facebook_url, twitter_handle, feed_url, mail_responder_email, source_url, description, translation, buildUnknownFields());
    }
  }

  private static final class ProtoAdapter_Contact extends ProtoAdapter<Contact> {
    ProtoAdapter_Contact() {
      super(FieldEncoding.LENGTH_DELIMITED, Contact.class);
    }

    @Override
    public int encodedSize(Contact value) {
      return ProtoAdapter.STRING.encodedSizeWithTag(1, value.name)
          + (value.website_url != null ? ProtoAdapter.STRING.encodedSizeWithTag(2, value.website_url) : 0)
          + (value.user_support_url != null ? ProtoAdapter.STRING.encodedSizeWithTag(3, value.user_support_url) : 0)
          + (value.support_email != null ? ProtoAdapter.STRING.encodedSizeWithTag(4, value.support_email) : 0)
          + (value.blog_url != null ? ProtoAdapter.STRING.encodedSizeWithTag(5, value.blog_url) : 0)
          + (value.facebook_url != null ? ProtoAdapter.STRING.encodedSizeWithTag(6, value.facebook_url) : 0)
          + (value.twitter_handle != null ? ProtoAdapter.STRING.encodedSizeWithTag(7, value.twitter_handle) : 0)
          + (value.feed_url != null ? ProtoAdapter.STRING.encodedSizeWithTag(8, value.feed_url) : 0)
          + (value.mail_responder_email != null ? ProtoAdapter.STRING.encodedSizeWithTag(9, value.mail_responder_email) : 0)
          + (value.source_url != null ? ProtoAdapter.STRING.encodedSizeWithTag(10, value.source_url) : 0)
          + (value.description != null ? ProtoAdapter.STRING.encodedSizeWithTag(11, value.description) : 0)
          + Translation.ADAPTER.asRepeated().encodedSizeWithTag(12, value.translation)
          + value.unknownFields().size();
    }

    @Override
    public void encode(ProtoWriter writer, Contact value) throws IOException {
      ProtoAdapter.STRING.encodeWithTag(writer, 1, value.name);
      if (value.website_url != null) ProtoAdapter.STRING.encodeWithTag(writer, 2, value.website_url);
      if (value.user_support_url != null) ProtoAdapter.STRING.encodeWithTag(writer, 3, value.user_support_url);
      if (value.support_email != null) ProtoAdapter.STRING.encodeWithTag(writer, 4, value.support_email);
      if (value.blog_url != null) ProtoAdapter.STRING.encodeWithTag(writer, 5, value.blog_url);
      if (value.facebook_url != null) ProtoAdapter.STRING.encodeWithTag(writer, 6, value.facebook_url);
      if (value.twitter_handle != null) ProtoAdapter.STRING.encodeWithTag(writer, 7, value.twitter_handle);
      if (value.feed_url != null) ProtoAdapter.STRING.encodeWithTag(writer, 8, value.feed_url);
      if (value.mail_responder_email != null) ProtoAdapter.STRING.encodeWithTag(writer, 9, value.mail_responder_email);
      if (value.source_url != null) ProtoAdapter.STRING.encodeWithTag(writer, 10, value.source_url);
      if (value.description != null) ProtoAdapter.STRING.encodeWithTag(writer, 11, value.description);
      if (value.translation != null) Translation.ADAPTER.asRepeated().encodeWithTag(writer, 12, value.translation);
      writer.writeBytes(value.unknownFields());
    }

    @Override
    public Contact decode(ProtoReader reader) throws IOException {
      Builder builder = new Builder();
      long token = reader.beginMessage();
      for (int tag; (tag = reader.nextTag()) != -1;) {
        switch (tag) {
          case 1: builder.name(ProtoAdapter.STRING.decode(reader)); break;
          case 2: builder.website_url(ProtoAdapter.STRING.decode(reader)); break;
          case 3: builder.user_support_url(ProtoAdapter.STRING.decode(reader)); break;
          case 4: builder.support_email(ProtoAdapter.STRING.decode(reader)); break;
          case 5: builder.blog_url(ProtoAdapter.STRING.decode(reader)); break;
          case 6: builder.facebook_url(ProtoAdapter.STRING.decode(reader)); break;
          case 7: builder.twitter_handle(ProtoAdapter.STRING.decode(reader)); break;
          case 8: builder.feed_url(ProtoAdapter.STRING.decode(reader)); break;
          case 9: builder.mail_responder_email(ProtoAdapter.STRING.decode(reader)); break;
          case 10: builder.source_url(ProtoAdapter.STRING.decode(reader)); break;
          case 11: builder.description(ProtoAdapter.STRING.decode(reader)); break;
          case 12: builder.translation.add(Translation.ADAPTER.decode(reader)); break;
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
    public Contact redact(Contact value) {
      Builder builder = value.newBuilder();
      Internal.redactElements(builder.translation, Translation.ADAPTER);
      builder.clearUnknownFields();
      return builder.build();
    }
  }
}
