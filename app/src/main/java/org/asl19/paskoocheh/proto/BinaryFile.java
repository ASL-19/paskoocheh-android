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
 * Message to hold an uploaded binary file for tools
 */
public final class BinaryFile extends Message<BinaryFile, BinaryFile.Builder> {
  public static final ProtoAdapter<BinaryFile> ADAPTER = new ProtoAdapter_BinaryFile();

  private static final long serialVersionUID = 0L;

  public static final String DEFAULT_CHECKSUM = "";

  public static final Long DEFAULT_SIZE = 0L;

  public static final String DEFAULT_PATH = "";

  /**
   * Checksum of the binary
   */
  @WireField(
      tag = 1,
      adapter = "com.squareup.wire.ProtoAdapter#STRING",
      label = WireField.Label.REQUIRED
  )
  public final String checksum;

  /**
   * Size of the binary
   */
  @WireField(
      tag = 2,
      adapter = "com.squareup.wire.ProtoAdapter#INT64",
      label = WireField.Label.REQUIRED
  )
  public final Long size;

  /**
   * key in S3
   */
  @WireField(
      tag = 3,
      adapter = "com.squareup.wire.ProtoAdapter#STRING",
      label = WireField.Label.REQUIRED
  )
  public final String path;

  public BinaryFile(String checksum, Long size, String path) {
    this(checksum, size, path, ByteString.EMPTY);
  }

  public BinaryFile(String checksum, Long size, String path, ByteString unknownFields) {
    super(ADAPTER, unknownFields);
    this.checksum = checksum;
    this.size = size;
    this.path = path;
  }

  @Override
  public Builder newBuilder() {
    Builder builder = new Builder();
    builder.checksum = checksum;
    builder.size = size;
    builder.path = path;
    builder.addUnknownFields(unknownFields());
    return builder;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof BinaryFile)) return false;
    BinaryFile o = (BinaryFile) other;
    return Internal.equals(unknownFields(), o.unknownFields())
        && Internal.equals(checksum, o.checksum)
        && Internal.equals(size, o.size)
        && Internal.equals(path, o.path);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode;
    if (result == 0) {
      result = unknownFields().hashCode();
      result = result * 37 + (checksum != null ? checksum.hashCode() : 0);
      result = result * 37 + (size != null ? size.hashCode() : 0);
      result = result * 37 + (path != null ? path.hashCode() : 0);
      super.hashCode = result;
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    if (checksum != null) builder.append(", checksum=").append(checksum);
    if (size != null) builder.append(", size=").append(size);
    if (path != null) builder.append(", path=").append(path);
    return builder.replace(0, 2, "BinaryFile{").append('}').toString();
  }

  public static final class Builder extends Message.Builder<BinaryFile, Builder> {
    public String checksum;

    public Long size;

    public String path;

    public Builder() {
    }

    /**
     * Checksum of the binary
     */
    public Builder checksum(String checksum) {
      this.checksum = checksum;
      return this;
    }

    /**
     * Size of the binary
     */
    public Builder size(Long size) {
      this.size = size;
      return this;
    }

    /**
     * key in S3
     */
    public Builder path(String path) {
      this.path = path;
      return this;
    }

    @Override
    public BinaryFile build() {
      if (checksum == null
          || size == null
          || path == null) {
        throw Internal.missingRequiredFields(checksum, "checksum",
            size, "size",
            path, "path");
      }

      return new BinaryFile(checksum, size, path, buildUnknownFields());
    }
  }

  private static final class ProtoAdapter_BinaryFile extends ProtoAdapter<BinaryFile> {
    ProtoAdapter_BinaryFile() {
      super(FieldEncoding.LENGTH_DELIMITED, BinaryFile.class);
    }

    @Override
    public int encodedSize(BinaryFile value) {
      return ProtoAdapter.STRING.encodedSizeWithTag(1, value.checksum)
          + ProtoAdapter.INT64.encodedSizeWithTag(2, value.size)
          + ProtoAdapter.STRING.encodedSizeWithTag(3, value.path)
          + value.unknownFields().size();
    }

    @Override
    public void encode(ProtoWriter writer, BinaryFile value) throws IOException {
      ProtoAdapter.STRING.encodeWithTag(writer, 1, value.checksum);
      ProtoAdapter.INT64.encodeWithTag(writer, 2, value.size);
      ProtoAdapter.STRING.encodeWithTag(writer, 3, value.path);
      writer.writeBytes(value.unknownFields());
    }

    @Override
    public BinaryFile decode(ProtoReader reader) throws IOException {
      Builder builder = new Builder();
      long token = reader.beginMessage();
      for (int tag; (tag = reader.nextTag()) != -1;) {
        switch (tag) {
          case 1: builder.checksum(ProtoAdapter.STRING.decode(reader)); break;
          case 2: builder.size(ProtoAdapter.INT64.decode(reader)); break;
          case 3: builder.path(ProtoAdapter.STRING.decode(reader)); break;
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
    public BinaryFile redact(BinaryFile value) {
      Builder builder = value.newBuilder();
      builder.clearUnknownFields();
      return builder.build();
    }
  }
}
