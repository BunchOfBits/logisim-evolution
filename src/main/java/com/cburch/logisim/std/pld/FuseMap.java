package com.cburch.logisim.std.pld;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * PLD Fuse map.
 */
public abstract class FuseMap implements Cloneable {
  protected final boolean[] map;

  protected FuseMap(int mapSize) {
    map = new boolean[mapSize];

    Arrays.fill(map, true);
  }

  protected FuseMap(boolean[] other) {
    this.map = other.clone();
  }

  /**
   * Serializes a fuse map to the circuit file format.
   *
   * @return The serialized fuse map.
   */
  public String toStandardString() {
    final var chunk = 80;
    var sb = new StringBuilder();

    IntStream.range(0, map.length)
        .forEach(i -> {
          sb.append(map[i++] ? '1' : '0');
          if (i > 0 && i % chunk == 0) {
            sb.append('\n');
          }
        });

    return sb.toString();
  }

  /**
   * Deserializes a fuse map from the circuit file format.
   *
   * @param str The circuit file representation of the fuse map.
   * @return A parsed fuse map.
   */
  protected static boolean[] parseFrom(String str) {
    var map = new ArrayList<Boolean>();
    var stream = str.chars();
    var it = stream.iterator();

    while (it.hasNext()) {
      switch(it.next()) {
        case (int) '0':
          map.add(false);
          break;
        case (int) '1':
          map.add(true);
          break;
        default:
          break;
      }
    }

    var arr = new boolean[map.size()];

    for (var i = 0; i < arr.length; i++) {
      arr[i] = map.get(i);
    }

    return arr;
  }

  protected static boolean[] parseFrom(File file) {
    var jedec = JedecFile.load(file);

    return jedec.getFuseMap();
  }

  public void copyFrom(boolean[] other) {
    System.arraycopy(other, 0, map, 0, map.length);
  }

  protected abstract Object clone() throws CloneNotSupportedException;
}