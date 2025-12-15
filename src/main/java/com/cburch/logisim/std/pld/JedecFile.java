package com.cburch.logisim.std.pld;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class JedecFile {
  private static final JedecFile empty = new JedecFile();

  @SuppressWarnings("unchecked")
  private static final Vector<Boolean>[] lut =
      new Vector[] {
        new Vector<>(Arrays.asList(false, false, false, false)),
        new Vector<>(Arrays.asList(false, false, false, true)),
        new Vector<>(Arrays.asList(false, false, true, false)),
        new Vector<>(Arrays.asList(false, false, true, true)),
        new Vector<>(Arrays.asList(false, true, false, false)),
        new Vector<>(Arrays.asList(false, true, false, true)),
        new Vector<>(Arrays.asList(false, true, true, false)),
        new Vector<>(Arrays.asList(false, true, true, true)),
        new Vector<>(Arrays.asList(true, false, false, false)),
        new Vector<>(Arrays.asList(true, false, false, true)),
        new Vector<>(Arrays.asList(true, false, true, false)),
        new Vector<>(Arrays.asList(true, false, true, true)),
        new Vector<>(Arrays.asList(true, true, false, false)),
        new Vector<>(Arrays.asList(true, true, false, true)),
        new Vector<>(Arrays.asList(true, true, true, false)),
        new Vector<>(Arrays.asList(true, true, true, true)),
      };

  private final Map<Character, Function<String, Boolean>> map = new HashMap<>();

  private int accessTime;
  private int fuseCheckSum;
  private final Vector<Boolean> electricalFuseData = new Vector<>();
  private boolean defaultFuseState;
  private boolean securityFuse;
  private ArrayList<Boolean> fuseMap;

  private JedecFile() {
    map.put('A', this::handleAccessTime);
    map.put('C', this::handleFuseChecksum);
    map.put('D', this::handleDeviceType);
    map.put('E', this::handleElectricalFuseData);
    map.put('F', this::handleDefaultFuseState);
    map.put('G', this::handleSecurityFuse);
    map.put('J', this::handleDeviceIdentification);
  }

  public int getAccessTime() {
    return accessTime;
  }

  public int getFuseCheckSum() {
    return fuseCheckSum;
  }

  public List<Boolean> getElectricalFuseData() {
    return Collections.unmodifiableList(electricalFuseData);
  }

  public boolean getDefaultFuseState() {
    return defaultFuseState;
  }

  public boolean getSecurityFuse() {
    return securityFuse;
  }

  public ArrayList<Boolean> getFuseMap() {
    return fuseMap;
  }

  public static JedecFile load(File file) {
    final int stx = 2;
    final int etx = 3;

    try (var stream = new FileInputStream(file)) {
      var stack = new Stack<Character>();
      var jedec = new JedecFile();

      if (get(stream, stack) != stx) {
        return empty;
      }

      var designSpecification = readField(stream, stack);

      do {
        final var field = readField(stream, stack);

        if (field.isEmpty()) {
          break;
        }

        var fieldIdentifier = field.charAt(0);
        var function = jedec.map.get(fieldIdentifier);

        if (function != null) {
          var success = function.apply(field);

          if (!success) {
            return empty;
          }
        }
      }
      while(true);

      if (get(stream, stack) != etx) {
        return empty;
      }

      return jedec;
    }
    catch (IOException e) {
      return empty;
    }
  }

  private boolean handleAccessTime(String field) {
    accessTime = Integer.parseInt(field.substring(1));

    return true;
  }

  private boolean handleFuseChecksum(String field) {
    fuseCheckSum = Integer.parseInt(field.substring(1), 16);

    return true;
  }

  private boolean handleDeviceType(String field) {
    // Obsolete
    return true;
  }

  private boolean handleElectricalFuseData(String field) {
    var hexFormat = field.length() > 1 && field.charAt(1) == 'H';

    if (hexFormat) {
      for (var c : field.substring(2).toCharArray())
      {
        var i = Character.digit(c, 16);

        if (i == -1) {
          electricalFuseData.clear();

          return false;
        }

        electricalFuseData.addAll(lut[i]);
      }

      return true;
    }

    for (var c : field.substring(2).toCharArray()) {
      var b = Character.digit(c, 2);

      if (b == -1) {
        electricalFuseData.clear();

        return false;
      }

      electricalFuseData.add(b != 0);
    }

    return true;
  }

  private boolean handleDefaultFuseState(String field) {
    if (field.length() <= 1) {
      return false;
    }

    var b = Character.digit(field.charAt(1), 2);

    if (b == -1) {
      return false;
    }

    defaultFuseState = b != 0;

    return true;
  }

  private boolean handleSecurityFuse(String field) {
    if (field.length() <= 1) {
      return false;
    }

    var b = Character.digit(field.charAt(1), 2);

    if (b == -1) {
      return false;
    }

    securityFuse = b != 0;

    return true;
  }

  private boolean handleDeviceIdentification(String field) {

  }

  private static String readField(FileInputStream stream, Stack<Character> stack) throws IOException {
    var builder = new StringBuilder();

    do {
      var c = get(stream, stack);

      if (c == '*') { return builder.toString(); }
      if (c == -1 || c == 3) { builder.chars().forEach(i -> unget((char)i, stack)); return ""; }

      builder.append((char)c);
    }
    while(true);
  }

  private static int get(FileInputStream stream, Stack<Character> stack) throws IOException {
    if (stack.isEmpty())
    {
      return stream.read();
    }

    return stack.pop();
  }

  private static void unget(char c, Stack<Character> stack) {
    stack.push(c);
  }
}
