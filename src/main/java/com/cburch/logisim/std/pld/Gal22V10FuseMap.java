package com.cburch.logisim.std.pld;

import java.io.File;
import java.util.stream.IntStream;

public class Gal22V10FuseMap extends FuseMap {
  private static final int[][] orPlane = new int[][] {
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
  private static final int[] oe = new int[] { 1, 10, 21, 34, 49, 66, 83, 98, 111, 122 };

  Gal22V10FuseMap() {
    super(5892);
  }

  Gal22V10FuseMap(Gal22V10FuseMap other) {
    super(other.map);
  }

  Gal22V10FuseMap(boolean map[]) {
    super(map);
  }

  public boolean getAndPlane(int row, int col) {
    if (0 <= row && row < 132 && 0 <= col && col < 44) {
      return map[row * 44 + col];
    }

    return false;
  }

  public int[] getOrPlane(int row) {
    return orPlane[row];
  }

  public int getOe(int row) { return oe[row]; }

  public int getAr() { return 0; }

  public int getSp() { return 131; }

  public boolean getS0(int row) {return map[5808 + row * 2]; }

  public boolean getS1(int row) {return map[5809 + row * 2]; }

  public static Gal22V10FuseMap parse(String str) {
    return new Gal22V10FuseMap(FuseMap.parseFrom(str));
  }

  public static Gal22V10FuseMap parse(File file) {
    return new Gal22V10FuseMap(FuseMap.parseFrom(file));
  }

  @Override
  protected Object clone() {
    return new Gal22V10FuseMap(this);
  }
}
