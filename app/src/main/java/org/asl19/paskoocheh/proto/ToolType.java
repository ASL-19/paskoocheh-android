package org.asl19.paskoocheh.proto;

import com.squareup.wire.ProtoAdapter;
import com.squareup.wire.WireEnum;

/**
 * Different Tool Types
 */
public enum ToolType implements WireEnum {
  VPN(0),

  PROXY(1),

  MESSENGER(2),

  ANONIMITY(3);

  public static final ProtoAdapter<ToolType> ADAPTER = ProtoAdapter.newEnumAdapter(ToolType.class);

  private final int value;

  ToolType(int value) {
    this.value = value;
  }

  /**
   * Return the constant for {@code value} or null.
   */
  public static ToolType fromValue(int value) {
    switch (value) {
      case 0: return VPN;
      case 1: return PROXY;
      case 2: return MESSENGER;
      case 3: return ANONIMITY;
      default: return null;
    }
  }

  @Override
  public int getValue() {
    return value;
  }
}
