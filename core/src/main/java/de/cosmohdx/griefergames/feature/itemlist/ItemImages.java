package de.cosmohdx.griefergames.feature.itemlist;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.feature.itemlist.ItemCatalog.Category;
import de.cosmohdx.griefergames.feature.itemlist.ItemCatalog.Entry;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import net.labymod.api.Laby;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.ResourceLocation;

/**
 * Lädt Item-Bilder und die Katalog-JSON von derselben öffentlichen Quelle.
 */
public final class ItemImages {

  public static final String SOURCE = "https://raw.githubusercontent.com/Syntax-Official/GGItems/main/";

  private static final ItemImages INSTANCE = new ItemImages();
  private static final String ITEMS_FILE = "Items.json";
  private static final String CATEGORIES_FILE = "categories.json";

  private final ItemCatalog catalog = new ItemCatalog();
  private final AtomicBoolean refreshRunning = new AtomicBoolean();
  private final HttpClient httpClient = HttpClient.newBuilder()
      .connectTimeout(Duration.ofSeconds(8))
      .followRedirects(HttpClient.Redirect.NORMAL)
      .build();

  public static ItemImages get() {
    return INSTANCE;
  }

  public ItemCatalog catalog() {
    return this.catalog;
  }

  public Icon icon(Entry entry) {
    this.ensureLoaded();
    String url = this.url(entry == null ? null : entry.image());
    if (url == null) {
      return Icon.texture(ResourceLocation.create("griefergames", "textures/icon.png"));
    }
    return Icon.url(url);
  }

  public String url(String image) {
    if (image == null || image.isBlank()) {
      return null;
    }
    if (image.startsWith("http://") || image.startsWith("https://")) {
      return image;
    }
    if (image.startsWith("/")) {
      return SOURCE + image.substring(1);
    }
    return SOURCE + image;
  }

  public void prepare(Runnable onUpdate) {
    if (this.catalog.isEmpty()) {
      this.loadBundled();
    }
    onUpdate.run();
    if (!this.refreshRunning.compareAndSet(false, true)) {
      return;
    }
    GrieferGames.get().schedule(() -> {
      try {
        String items = this.fetch(SOURCE + ITEMS_FILE);
        String categories = this.fetch(SOURCE + CATEGORIES_FILE);
        this.catalog.replace(this.parseItems(items), this.parseCategories(categories));
        Laby.labyAPI().minecraft().executeOnRenderThread(onUpdate);
      } catch (Exception exception) {
        GrieferGames.get().logger().warn(GrieferGames.LOG_PREFIX + "Itemliste konnte nicht aktualisiert werden: " + exception.getMessage());
      } finally {
        this.refreshRunning.set(false);
      }
    }, 0, TimeUnit.MILLISECONDS);
  }

  private void ensureLoaded() {
    if (!this.catalog.isEmpty()) {
      return;
    }
    this.loadBundled();
  }

  private void loadBundled() {
    String items = this.readResource("items.json");
    String categories = this.readResource(CATEGORIES_FILE);
    this.catalog.replace(
        items == null ? List.of() : this.parseItems(items),
        categories == null ? List.of() : this.parseCategories(categories)
    );
  }

  private String readResource(String fileName) {
    String path = "/assets/griefergames/catalog/" + fileName;
    try (InputStream input = ItemImages.class.getResourceAsStream(path)) {
      if (input == null) {
        return null;
      }
      return new String(input.readAllBytes(), StandardCharsets.UTF_8);
    } catch (Exception exception) {
      return null;
    }
  }

  private String fetch(String url) throws Exception {
    HttpRequest request = HttpRequest.newBuilder(URI.create(url))
        .timeout(Duration.ofSeconds(20))
        .header("User-Agent", "GrieferGames-Addon")
        .GET()
        .build();
    HttpResponse<String> response = this.httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    if (response.statusCode() != 200) {
      throw new IllegalStateException(url + " -> " + response.statusCode());
    }
    return response.body();
  }

  private List<Entry> parseItems(String json) {
    List<Entry> entries = new ArrayList<>();
    JsonObject root = JsonParser.parseString(json).getAsJsonObject();
    for (Map.Entry<String, JsonElement> network : root.entrySet()) {
      if (!network.getValue().isJsonArray()) {
        continue;
      }
      for (JsonElement element : network.getValue().getAsJsonArray()) {
        if (!element.isJsonObject()) {
          continue;
        }
        JsonObject item = element.getAsJsonObject();
        entries.add(new Entry(
            network.getKey(),
            text(item, "title"),
            text(item, "image"),
            text(item, "description"),
            text(item, "label"),
            text(item, "credits")
        ));
      }
    }
    return entries;
  }

  private List<Category> parseCategories(String json) {
    List<Category> categories = new ArrayList<>();
    JsonObject root = JsonParser.parseString(json).getAsJsonObject();
    for (Map.Entry<String, JsonElement> category : root.entrySet()) {
      String filter = "";
      if (category.getValue().isJsonObject()) {
        filter = text(category.getValue().getAsJsonObject(), "filter");
      }
      categories.add(new Category(category.getKey(), filter));
    }
    return categories;
  }

  private static String text(JsonObject object, String key) {
    JsonElement element = object.get(key);
    if (element == null || element.isJsonNull()) {
      return "";
    }
    return element.getAsString();
  }
}
