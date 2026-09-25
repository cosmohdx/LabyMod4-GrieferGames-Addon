package de.cosmohdx.griefergames.feature.wiki;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Minimal HTML tree for the public server-rendered wiki pages. */
final class WikiHtml {
  private static final Pattern TOKEN = Pattern.compile("(?s)<!--.*?-->|<![^>]*>|<(/?)([A-Za-z][A-Za-z0-9:-]*)([^>]*)>|([^<]+)");
  private static final Pattern ATTRIBUTE = Pattern.compile("([A-Za-z_:][A-Za-z0-9_:.-]*)\\s*=\\s*(?:\"([^\"]*)\"|'([^']*)'|([^\\s>]+))");
  private static final Pattern ENTITY = Pattern.compile("&(#x[0-9a-fA-F]+|#[0-9]+|[A-Za-z]+);");

  private WikiHtml() {}

  static Node parse(String html) {
    String stripped = html.replaceAll("(?is)<script\\b[^>]*>.*?</script>", "")
        .replaceAll("(?is)<style\\b[^>]*>.*?</style>", "");
    Node root = new Node("root", Map.of());
    Deque<Node> stack = new ArrayDeque<>();
    stack.push(root);
    Matcher tokens = TOKEN.matcher(stripped);
    while (tokens.find()) {
      if (tokens.group(4) != null) {
        stack.peek().parts.add(decode(tokens.group(4)));
        continue;
      }
      if (tokens.group(2) == null) continue;
      String tag = tokens.group(2).toLowerCase();
      if (!tokens.group(1).isEmpty()) {
        while (stack.size() > 1 && !stack.peek().tag.equals(tag)) stack.pop();
        if (stack.size() > 1) stack.pop();
        continue;
      }
      Map<String, String> attributes = new LinkedHashMap<>();
      Matcher pairs = ATTRIBUTE.matcher(tokens.group(3));
      while (pairs.find()) {
        String value = pairs.group(2) != null ? pairs.group(2) : pairs.group(3) != null ? pairs.group(3) : pairs.group(4);
        attributes.put(pairs.group(1).toLowerCase(), decode(value));
      }
      Node node = new Node(tag, attributes);
      stack.peek().parts.add(node);
      if (!tokens.group(3).trim().endsWith("/") && !isVoid(tag)) stack.push(node);
    }
    return root;
  }

  private static boolean isVoid(String tag) {
    return switch (tag) {
      case "area", "base", "br", "col", "embed", "hr", "img", "input", "link", "meta", "source", "wbr" -> true;
      default -> false;
    };
  }

  static String decode(String text) {
    Matcher matcher = ENTITY.matcher(text);
    StringBuffer result = new StringBuffer();
    while (matcher.find()) {
      String key = matcher.group(1);
      String replacement;
      try {
        replacement = key.startsWith("#x") ? new String(Character.toChars(Integer.parseInt(key.substring(2), 16)))
            : key.startsWith("#") ? new String(Character.toChars(Integer.parseInt(key.substring(1))))
            : switch (key) {
              case "amp" -> "&"; case "lt" -> "<"; case "gt" -> ">"; case "quot" -> "\"";
              case "apos", "rsquo" -> "'"; case "nbsp" -> " "; case "ndash" -> "–";
              case "mdash" -> "—"; case "hellip" -> "…"; default -> "&" + key + ";";
            };
      } catch (RuntimeException ignored) {
        replacement = "&" + key + ";";
      }
      matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
    }
    matcher.appendTail(result);
    return result.toString();
  }

  static final class Node {
    final String tag;
    final Map<String, String> attributes;
    final List<Object> parts = new ArrayList<>();

    Node(String tag, Map<String, String> attributes) {
      this.tag = tag;
      this.attributes = attributes;
    }

    String attr(String name) { return this.attributes.getOrDefault(name, ""); }
    boolean hasClass(String name) { return List.of(this.attr("class").split("\\s+")).contains(name); }

    List<Node> children() {
      List<Node> nodes = new ArrayList<>();
      for (Object part : this.parts) if (part instanceof Node node) nodes.add(node);
      return nodes;
    }

    Node first(String tag) {
      for (Node child : this.children()) if (child.tag.equals(tag)) return child;
      return null;
    }

    Node firstClass(String name) {
      if (this.hasClass(name)) return this;
      for (Node child : this.children()) {
        Node found = child.firstClass(name);
        if (found != null) return found;
      }
      return null;
    }

    List<Node> descendants(String tag) {
      List<Node> nodes = new ArrayList<>();
      for (Node child : this.children()) {
        if (child.tag.equals(tag)) nodes.add(child);
        nodes.addAll(child.descendants(tag));
      }
      return nodes;
    }

    String textWithoutNestedLists() {
      StringBuilder result = new StringBuilder();
      for (Object part : this.parts) {
        if (part instanceof String value) result.append(value);
        else if (part instanceof Node node && !node.tag.equals("ul") && !node.tag.equals("ol")) {
          result.append(node.textWithoutNestedLists());
          if (node.tag.equals("p") || node.tag.equals("br")) result.append(' ');
        }
      }
      return result.toString().replaceAll("\\s+", " ").trim();
    }

    String text() {
      StringBuilder result = new StringBuilder();
      for (Object part : this.parts) {
        if (part instanceof String value) result.append(value);
        else if (part instanceof Node node) result.append(node.text());
        if (part instanceof Node node && (node.tag.equals("p") || node.tag.equals("li") || node.tag.equals("br"))) result.append(' ');
      }
      return result.toString().replaceAll("\\s+", " ").trim();
    }
  }
}
