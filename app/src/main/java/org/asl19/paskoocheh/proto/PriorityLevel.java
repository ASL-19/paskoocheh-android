package org.asl19.paskoocheh.proto;

import com.squareup.wire.ProtoAdapter;
import com.squareup.wire.WireEnum;

/**
 * Priority level for update messages
 */
public enum PriorityLevel implements WireEnum {
  HIGH(0),

  MEDIUM(1),

  LOW(2);

  public static final ProtoAdapter<PriorityLevel> ADAPTER = ProtoAdapter.newEnumAdapter(PriorityLevel.class);

  private final int value;

  PriorityLevel(int value) {
    this.value = value;
  }

  /**
   * Return the constant for {@code value} or null.
   */
  public static PriorityLevel fromValue(int value) {
    switch (value) {
      case 0: return HIGH;
      case 1: return MEDIUM;
      case 2: return LOW;
      default: return null;
    }
  }

  @Override
  public int getValue() {
    return value;
  }
}
