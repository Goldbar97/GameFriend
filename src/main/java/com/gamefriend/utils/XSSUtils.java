package com.gamefriend.utils;

public class XSSUtils {

  public static String sanitize(String unsafe) {

    if (unsafe != null) {
      StringBuilder sb = new StringBuilder();
      for (char c : unsafe.toCharArray()) {
        switch (c) {
          case '<':
            sb.append("&lt;");
            break;
          case '>':
            sb.append("&gt;");
            break;
          case '&':
            sb.append("&amp;");
            break;
          case '"':
            sb.append("&quot;");
            break;
          case '\'':
            sb.append("&#x27;");
            break;
          case '/':
            sb.append("&#x2F;");
            break;
          default:
            sb.append(c);
            break;
        }
      }

      return sb.toString();
    }

    return null;
  }
}