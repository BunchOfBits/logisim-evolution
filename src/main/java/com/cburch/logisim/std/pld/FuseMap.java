package com.cburch.logisim.std.pld;

import java.util.stream.IntStream;

/**
 * PLD Fuse map.
 */
public class FuseMap implements Cloneable {
  protected int[] andPlane;
  protected int[][] orPlane;
  protected final boolean[] map;

  protected FuseMap(boolean[] map) {
    this.map = map;
  }

  protected int toPattern(int begin, int size) {
    return IntStream
        .range(begin, begin + size)
        .filter(fuse -> map[fuse])
        .map(fuse -> 1 << (fuse - begin))
        .sum();
  }
}