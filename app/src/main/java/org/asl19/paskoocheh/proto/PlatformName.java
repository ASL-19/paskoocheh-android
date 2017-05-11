package org.asl19.paskoocheh.proto;

import com.squareup.wire.ProtoAdapter;
import com.squareup.wire.WireEnum;

/**
 * Various platforms for each platform type
 */
public enum PlatformName implements WireEnum {
  /**
   * OS
   */
  WINDOWS(0),

  MACOS(1),

  LINUX_64(2),

  LINUX_32(3),

  /**
   * Platform
   */
  ANDROID(5),

  IOS(6),

  WINDOWS_MOBILE(7),

  /**
   * Browser
   */
  CHROME(10),

  FIREFOX(11),

  IE(12),

  OPERA(13);

  public static final ProtoAdapter<PlatformName> ADAPTER = ProtoAdapter.newEnumAdapter(PlatformName.class);

  private final int value;

  PlatformName(int value) {
    this.value = value;
  }

  /**
   * Return the constant for {@code value} or null.
   */
  public static PlatformName fromValue(int value) {
    switch (value) {
      case 0: return WINDOWS;
      case 1: return MACOS;
      case 2: return LINUX_64;
      case 3: return LINUX_32;
      case 5: return ANDROID;
      case 6: return IOS;
      case 7: return WINDOWS_MOBILE;
      case 10: return CHROME;
      case 11: return FIREFOX;
      case 12: return IE;
      case 13: return OPERA;
      default: return null;
    }
  }

  @Override
  public int getValue() {
    return value;
  }
}
