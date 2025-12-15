package com.cburch.logisim.std.pld;

import java.io.File;
import java.util.Arrays;
import java.util.stream.IntStream;

public class Gal22V10FuseMap extends FuseMap {
  private final int[] oe;
  private final int ar;
  private final int sp;
  private final int[] olmc;

  public Gal22V10FuseMap(boolean[] map) {
    super(map);

    final var nrOfFusesInAndPlane = 5808;
    final var andPlaneWidth = 44;
    final var andPlaneLength = nrOfFusesInAndPlane / andPlaneWidth;

    andPlane = IntStream
        .range(0, andPlaneLength)
        .map(i -> toPattern(i * andPlaneWidth, andPlaneWidth))
        .limit(andPlaneLength)
        .toArray();

    orPlane = new int[][] {
        IntStream.range(2, 10).toArray(),
        IntStream.range(11, 21).toArray(),
        IntStream.range(22, 34).toArray(),
        IntStream.range(35, 49).toArray(),
        IntStream.range(50, 66).toArray(),
        IntStream.range(67, 83).toArray(),
        IntStream.range(84, 98).toArray(),
        IntStream.range(99, 111).toArray(),
        IntStream.range(112, 122).toArray(),
        IntStream.range(123, 131).toArray(),
    };

    oe = new int[] { 1, 10, 21, 34, 49, 66, 83, 98, 111, 122 };

    ar = 0;
    sp = 131;

    olmc = new int[] {
        toPattern(5808, 2),
        toPattern(5810, 2),
        toPattern(5812, 2),
        toPattern(5814, 2),
        toPattern(5816, 2),
        toPattern(5818, 2),
        toPattern(5820, 2),
        toPattern(5822, 2),
        toPattern(5824, 2),
        toPattern(5826, 2),
    };
  }

  Gal22V10FuseMap() {
    this(emptyMap());
  }

  Gal22V10FuseMap(Gal22V10FuseMap map) {
    this(map.map);
  }

  private static boolean[] emptyMap() {
    var map = new boolean[5892];

    Arrays.fill(map, true);

    return map;
  }

  /**
   * Deserializes a fuse map from the circuit file format.
   *
   * @param str The circuit file representation of the fuse map.
   * @return A parsed fuse map.
   */
  public static Gal22V10FuseMap parse(String str) {
    var map = emptyMap();
    var stream = str.chars();
    var it = stream.iterator();
    var i = 0;

    while (it.hasNext()) {
      switch(it.next()) {
        case (int) '0':
          map[i++] = false;
          break;
        case (int) '1':
          map[i++] = true;
          break;
        default:
          break;
      }
    }

    return new Gal22V10FuseMap(map);
  }

  public static Gal22V10FuseMap parse(File file) {
    return new Gal22V10FuseMap();
  }

  public void copyFrom(Gal22V10FuseMap other) {
    System.arraycopy(other.map, 0, map, 0, map.length);
  }

  /**
   * Serializes a fuse map to the circuit file format.
   *
   * @return The serialized fuse map.
   */
  public String toStandardString() {
    final var chunk = 44;
    StringBuilder sb = new StringBuilder();

    IntStream.range(0, map.length)
        .forEach(i -> {
          sb.append(map[i++] ? '1' : '0');
          if (i > 0 && i % chunk == 0) {
            sb.append('\n');
          }
        });

    return sb.toString();
  }

  @Override
  protected Object clone() {
    return new Gal22V10FuseMap(this.map);
  }
}
