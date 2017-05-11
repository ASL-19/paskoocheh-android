package org.asl19.paskoocheh.proto;

import com.squareup.wire.ProtoAdapter;
import com.squareup.wire.WireEnum;

/**
 * Different supported platform types
 */
public enum PlatformType implements WireEnum {
  DESKTOP(0),

  MOBILE(1),

  BROWSER(2);

  public static final ProtoAdapter<PlatformType> ADAPTER = ProtoAdapter.newEnumAdapter(PlatformType.class);

  private final int value;

  PlatformType(int value) {
    this.value = value;
  }

  /**
   * Return the constant for {@code value} or null.
   */
  public static PlatformType fromValue(int value) {
    switch (value) {
      case 0: return DESKTOP;
      case 1: return MOBILE;
      case 2: return BROWSER;
      default: return null;
    }
  }

  @Override
  public int getValue() {
    return value;
  }
}
