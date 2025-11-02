package com.cburch.logisim.std.pld;

/**
 * PLD Fuse map.
 */
public class FuseMap {
  public static final FuseMap EMPTY = new FuseMap();

  /**
   * Deserializes a fuse map from the circuit file format.
   *
   * @param str The circuit file representation of the fuse map.
   * @return A parsed fuse map.
   */
  public static FuseMap parse(String str) {
    return new FuseMap();
  }

  /**
   * Serializes a fuse map to the circuit file format.
   *
   * @return The serialized fuse map.
   */
  public String toStandardString() {
    return "";
  }
}