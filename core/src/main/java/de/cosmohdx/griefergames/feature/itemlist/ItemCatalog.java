package de.cosmohdx.griefergames.feature.itemlist;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

public final class ItemCatalog {

  public record Entry(String network, String title, String image, String description, String label, String credits) {

  }

  public record Category(String name, String filter) {

  }

  private volatile List<Entry> entries = List.of();
  private volatile List<Category> categories = List.of();

  public void replace(List<Entry> entries, List<Category> categories) {
    this.entries = List.copyOf(entries);
    this.categories = List.copyOf(categories);
  }

  public boolean isEmpty() {
    return this.entries.isEmpty();
  }

  public int size() {
    return this.entries.size();
  }

  public List<String> categoryNames() {
    List<String> names = new ArrayList<>();
    for (Category category : this.categories) {
      names.add(category.name());
    }
    return names;
  }

  public List<String> networks() {
    LinkedHashSet<String> names = new LinkedHashSet<>();
    for (Entry entry : this.entries) {
      names.add(entry.network());
    }
    return List.copyOf(names);
  }

  public List<Entry> filter(String network, String categoryName, String query) {
    String categoryFilter = this.filterOf(categoryName);
    String needle = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
    List<Entry> matches = new ArrayList<>();
    for (Entry entry : this.entries) {
      if (network != null && !network.equals(entry.network())) {
        continue;
      }
      if (!categoryFilter.isEmpty() && !contains(entry.title(), categoryFilter) && !contains(entry.label(), categoryFilter)) {
        continue;
      }
      if (!needle.isEmpty() && !contains(entry.title(), needle) && !contains(entry.description(), needle)
          && !contains(entry.label(), needle)) {
        continue;
      }
      matches.add(entry);
    }
    return matches;
  }

  private String filterOf(String categoryName) {
    if (categoryName == null) {
      return "";
    }
    for (Category category : this.categories) {
      if (category.name().equals(categoryName)) {
        return category.filter() == null ? "" : category.filter().toLowerCase(Locale.ROOT);
      }
    }
    return "";
  }

  private static boolean contains(String value, String needle) {
    return value != null && value.toLowerCase(Locale.ROOT).contains(needle);
  }
}
