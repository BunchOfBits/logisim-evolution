package com.cburch.logisim.std.pld;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;

public class JedecFile {
  private static final int stx = 2;
  private static final int etx = 3;
  private static final int lf = 10;
  private static final int cr = 13;
  private static final int eof = -1;

  private static final JedecFile empty = new JedecFile();

  private final Stack<Character> stack = new Stack<>();
  private final Map<Character, Function<String, Boolean>> map = new HashMap<>();

  private String designSpecification;
  private int accessTime;
  private int fuseCheckSum;
  private boolean[] electricalFuseData;
  private boolean defaultFuseState;
  private boolean securityFuse;
  private int architecture;
  private int pinout;
  private boolean[] fuseMap;
  private String note;
  private int[] pinSequence;
  private int fuseLimit;
  private int numberOfPins;
  private int vectorLimit;
  private long signatureAnalysisResultVector;
  private boolean[] signatureAnalysisStartVector;
  private int signatureAnalysisTestCycles;
  private boolean[] userData;
  private String[] testVectors;
  private boolean defaultTestCondition;

  private final static Pattern accessTimePattern = Pattern.compile("^A(?<accesTime>[1-9][0-9]*)$");
  private final static Pattern fuseChecksumPattern = Pattern.compile("^C(?<fuseChecksum>[0-9a-fA-F]{4})$");
  private final static Pattern deviceTypePattern = Pattern.compile("^D.*");
  private final static Pattern electricalFuseDataPattern = Pattern.compile("^E(?<binData>[01]+)$|^EH(?<hexData>[0-9a-fA-F]+)$");
  private final static Pattern defaultFuseStatePattern = Pattern.compile("^F(?<defaultFuseState>[01])$");
  private final static Pattern securityFusePattern = Pattern.compile("^G(?<securityFuse>[01])$");
  private final static Pattern deviceIdentificationPattern = Pattern.compile("^J(?<architecture>[1-9][0-9]*)(?<pinout>0|[1-9][0-9]*)$");
  private final static Pattern fuseListPattern = Pattern.compile("^L(?<fuseNumber>[0-9]+)[ \r](?<fuseStates>[01\r]+)$");
  private final static Pattern notePattern = Pattern.compile("^N(?<note>.*)$");
  private final static Pattern pinSequencePattern = Pattern.compile("^P(?<pinSequence>( [1-9][0-9]?)+)$");
  private final static Pattern valuePattern = Pattern.compile("^QF(?<fuseLimit>[1-9][0-9]*)|QP(?<numberOfPins>[1-9][0-9]?)|QV(?<vectorLimit>[1-9][0-9]*)$");
  private final static Pattern signatureAnalysisResultVectorPattern = Pattern.compile("^R(?<resultVector>[0-9a-fA-F]{8})$");
  private final static Pattern signatureAnalysisStartVectorPattern = Pattern.compile("^S(?<startVector>[01]+)$");
  private final static Pattern signatureAnalysisTestCyclesPattern = Pattern.compile("^T(?<testCycles>[1-9][0-9]*)$");
  private final static Pattern userDataPattern = Pattern.compile("^U(?<binData>[01]+)|UA(?<txtData>[ -~&&[^*]]+)|UH(?<hexData>[0-9a-fA-F]+)$");
  private final static Pattern testVectorsPattern = Pattern.compile("^V(?<vectorNumber>[0-9]+) (?<testCondition>[0-9BCDFHKLNPRTUXZ]+)$");
  private final static Pattern defaultTestConditionPattern = Pattern.compile("^X(?<defaultTestCondition>[01])$");

  private JedecFile() {
    map.put('A', this::handleAccessTime);
    map.put('C', this::handleFuseChecksum);
    map.put('D', this::handleDeviceType);
    map.put('E', this::handleElectricalFuseData);
    map.put('F', this::handleDefaultFuseState);
    map.put('G', this::handleSecurityFuse);
    map.put('J', this::handleDeviceIdentification);
    map.put('L', this::handleFuseList);
    map.put('N', this::handleNote);
    map.put('P', this::handlePinSequence);
    map.put('Q', this::handleValue);
    map.put('R', this::handleSignatureAnalysisResultVector);
    map.put('S', this::handleSignatureAnalysisStartVector);
    map.put('T', this::handleSignatureAnalysisTestCycles);
    map.put('U', this::handleUserData);
    map.put('V', this::handleTestVectors);
    map.put('X', this::handleDefaultTestCondition);
  }

  public String getDesignSpecification() { return designSpecification; }

  public int getAccessTime() {
    return accessTime;
  }

  public int getFuseCheckSum() {
    return fuseCheckSum;
  }

  public boolean[] getElectricalFuseData() { return electricalFuseData.clone(); }

  public boolean getDefaultFuseState() {
    return defaultFuseState;
  }

  public boolean getSecurityFuse() {
    return securityFuse;
  }

  public int getArchitecture() { return architecture; }

  public int getPinout() { return pinout; }

  public boolean[] getFuseMap() {
    return fuseMap.clone();
  }

  public String getNote() { return note; }

  public int[] getPinSequence() { return pinSequence.clone(); }

  public int getFuseLimit() { return fuseLimit; }

  public int getNumberOfPins() { return numberOfPins; }

  public int getVectorLimit() { return vectorLimit; }

  public long getSignatureAnalysisResultVector() { return signatureAnalysisResultVector; }

  public boolean[] getSignatureAnalysisStartVector() { return signatureAnalysisStartVector; }

  public int getSignatureAnalysisTestCycles() { return signatureAnalysisTestCycles; }

  public boolean[] getUserData() { return userData; }

  public String[] getTestVectors() { return testVectors; }

  public boolean getDefaultTestCondition() { return defaultTestCondition; }

  public static JedecFile load(File file) {
    try (var stream = new FileInputStream(file)) {
      var jedec = new JedecFile();

      if (!jedec.readMarker(stream, (char)stx)) {
        return empty;
      }

      jedec.designSpecification = jedec.readField(stream);

      do {
        var field = jedec.readField(stream);

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

      if (!jedec.readMarker(stream, (char)etx)) {
        return empty;
      }

      return jedec;
    }
    catch (IOException e) {
      return empty;
    }
  }

  private boolean handleAccessTime(String field) {
    var matcher = accessTimePattern.matcher(field);

    if (!matcher.matches()) {
      return false;
    }

    accessTime = Integer.parseInt(matcher.group("accessTime"));

    return true;
  }

  private boolean handleFuseChecksum(String field) {
    var matcher = fuseChecksumPattern.matcher(field);

    if (!matcher.matches()) {
      return false;
    }

    fuseCheckSum = Integer.parseInt(matcher.group("fuseChecksum"), 16);

    return true;
  }

  private boolean handleDeviceType(String field) {
    var matcher = deviceTypePattern.matcher(field);

    return matcher.find();
  }

  private boolean handleElectricalFuseData(String field) {
    var matcher = electricalFuseDataPattern.matcher(field);

    if (!matcher.matches()) {
      return false;
    }

    var binaryData = matcher.group("binData");

    if (binaryData != null) {
      electricalFuseData = toBooleanArray(binaryData);

      return true;
    }

    var hexData = matcher.group("hexData");

    electricalFuseData = toBooleanArray(toBinaryString(hexData));

    return true;
  }

  private boolean handleDefaultFuseState(String field) {
    var matcher = defaultFuseStatePattern.matcher(field);

    if (!matcher.matches()) {
      return false;
    }

    defaultFuseState = Integer.parseInt(matcher.group("defaultFuseState"), 2) != 0;

    return true;
  }

  private boolean handleSecurityFuse(String field) {
    var matcher = securityFusePattern.matcher(field);

    if (!matcher.matches()) {
      return false;
    }

    securityFuse = Integer.parseInt(matcher.group("securityFuse"), 2) != 0;

    return true;
  }

  private boolean handleDeviceIdentification(String field) {
    var matcher = deviceIdentificationPattern.matcher(field);

    if (!matcher.matches()) {
      return false;
    }

    architecture = Integer.parseInt(matcher.group("architecture"), 10);
    pinout = Integer.parseInt(matcher.group("pinout"), 10);

    return true;
  }

  private boolean handleFuseList(String field) {
    var matcher = fuseListPattern.matcher(field);

    if (!matcher.matches()) {
      return false;
    }

    var fuseNumber = Integer.parseInt(matcher.group("fuseNumber"), 10);
    var fuseStates = toBooleanArray(matcher.group("fuseStates").replace("\r", ""));

    System.arraycopy(fuseStates, 0, fuseMap, fuseNumber, fuseStates.length);

    return true;
  }

  private boolean handleNote(String field) {
    var matcher = notePattern.matcher(field);

    if (!matcher.matches()) {
      return false;
    }

    note = matcher.group("note");

    return true;
  }

  private boolean handlePinSequence(String field) {
    var matcher = pinSequencePattern.matcher(field);

    if (!matcher.matches()) {
      return false;
    }

    pinSequence = Arrays
        .stream(matcher.group("pinSequence").trim().split(" "))
        .mapToInt(Integer::parseInt)
        .toArray();

    return true;
  }

  private boolean handleValue(String field) {
    var matcher = valuePattern.matcher(field);

    if (!matcher.matches()) {
      return false;
    }

    var fuseLimitGroup = matcher.group("fuseLimit");

    if (fuseLimitGroup != null) {
      fuseLimit = Integer.parseInt(fuseLimitGroup);
      fuseMap = new boolean[fuseLimit];

      Arrays.fill(fuseMap, defaultFuseState);

      return true;
    }

    var numberOfPinsGroup = matcher.group("numberOfPins");

    if (numberOfPinsGroup != null) {
      numberOfPins = Integer.parseInt(numberOfPinsGroup);

      return true;
    }

    var vectorLimitGroup = matcher.group("vectorLimit");

    vectorLimit = Integer.parseInt(vectorLimitGroup);
    testVectors = new String[vectorLimit];

    return true;
  }

  private boolean handleSignatureAnalysisResultVector(String field) {
    var matcher = signatureAnalysisResultVectorPattern.matcher(field);

    if (!matcher.matches()) {
      return false;
    }

    var resultVectorGroup = matcher.group("resultVector");

    if (resultVectorGroup.length() != 8) {
      return false;
    }

    signatureAnalysisResultVector = Long.parseLong(resultVectorGroup, 16);

    return true;
  }

  private boolean handleSignatureAnalysisStartVector(String field) {
    var matcher = signatureAnalysisStartVectorPattern.matcher(field);

    if (!matcher.matches()) {
      return false;
    }

    signatureAnalysisStartVector = toBooleanArray(matcher.group("startVector"));

    return true;
  }

  private boolean handleSignatureAnalysisTestCycles(String field) {
    var matcher = signatureAnalysisTestCyclesPattern.matcher(field);

    if (!matcher.matches()) {
      return false;
    }

    signatureAnalysisTestCycles = Integer.parseInt(matcher.group("testCycles"), 10);

    return true;
  }

  private boolean handleUserData(String field) {
    var matcher = userDataPattern.matcher(field);

    if (!matcher.matches()) {
      return false;
    }

    var txtData = matcher.group("txtData");

    if (txtData != null) {
      userData = toBooleanArray(toBinaryString(toHexString(txtData)));

      return true;
    }

    var hexData = matcher.group("hexData");

    if (hexData != null) {
      userData = toBooleanArray(toBinaryString(hexData));

      return true;
    }

    var binData = matcher.group("binData");

    userData = toBooleanArray(binData);

    return true;
  }

  private boolean handleTestVectors(String field) {
    var matcher = testVectorsPattern.matcher(field);

    if (!matcher.matches()) {
      return false;
    }

    var vectorNumber = Integer.parseInt(matcher.group("vectorNumber"));

    testVectors[vectorNumber] = matcher.group("testCondition");

    return true;
  }

  private boolean handleDefaultTestCondition(String field) {
    var matcher = defaultTestConditionPattern.matcher(field);

    if (!matcher.matches()) {
      return false;
    }

    defaultTestCondition = Integer.parseInt(matcher.group("defaultTestCondition"), 2) != 0;

    return true;
  }

  private boolean readMarker(FileInputStream stream, char marker) throws IOException {
    int c;

    while ((c = get(stream)) != eof) {
      if (c == cr || c == lf) {
        continue;
      }

      return c == marker;
    }

    return false;
  }

  private String readField(FileInputStream stream) throws IOException {
    var field = readField_(stream);

    if (field == null) {
      return "";
    }

    return field.trim();
  }

  private String readField_(FileInputStream stream) throws IOException {
    var c = get(stream);

    switch(c) {
      case eof:
        return null;
      case '*':
        return "";
      case etx:
        unget((char)c);
        return null;
      default:
        var s = readField_(stream);

        if (s == null) {
          unget((char)c);
          return null;
        }

        return (char)c + s;
    }
  }

  private int get(FileInputStream stream) throws IOException {
    if (stack.isEmpty())
    {
      return stream.read();
    }

    return stack.pop();
  }

  private void unget(char c) {
    stack.push(c);
  }

  private static boolean[] toBooleanArray(String s) {
    var result = new boolean[s.length()];

    for (var i = 0; i < s.length(); i++) {
      result[i] = s.charAt(i) == '1';
    }

    return result;
  }

  private static String toBinaryString(String s) {
    var builder = new StringBuilder();

    for(var c : s.toCharArray())
    {
      var hex = Character.digit(c, 16);
      var bin = right("000" + Integer.toBinaryString(hex), 4);

      builder.append(bin);
    }

    return builder.toString();
  }

  private static String toHexString(String s) {
    var builder = new StringBuilder();

    for(var c : s.toCharArray())
    {
      builder.append(String.format("%02X", (int)c));
    }

    return builder.toString();
  }

  private static String right(String s, int n) {
    return s.substring(Math.max(0, s.length() - n));
  }
}
