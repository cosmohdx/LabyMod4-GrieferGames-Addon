package de.cosmohdx.griefergames.feature.itempreview;

import java.util.UUID;
import net.labymod.api.client.world.item.ItemStack;
import net.labymod.api.component.data.BuiltinDataComponents;
import net.labymod.api.component.data.DataComponentContainer;
import net.labymod.api.mojang.GameProfile;
import net.labymod.api.mojang.Property;
import net.labymod.api.nbt.NBTTagType;
import net.labymod.api.nbt.tags.NBTTagCompound;
import net.labymod.api.nbt.tags.NBTTagList;
import org.jetbrains.annotations.Nullable;

public final class HeadTextures {

  private HeadTextures() {
  }

  public static @Nullable HeadTexture fromStack(ItemStack stack) {
    if (!ItemPreviewKinds.isPlayerHead(stack)) {
      return null;
    }
    HeadTexture fromComponent = fromProfileComponent(stack);
    HeadTexture fromNbt = fromSkullOwner(stack);
    return merge(fromComponent, fromNbt);
  }

  private static @Nullable HeadTexture fromProfileComponent(ItemStack stack) {
    if (!stack.hasDataComponentContainer()) {
      return null;
    }
    DataComponentContainer container = stack.getDataComponentContainer();
    if (container == null || !container.has(BuiltinDataComponents.PROFILE)) {
      return null;
    }
    Object profile = container.get(BuiltinDataComponents.PROFILE);
    if (profile instanceof GameProfile gameProfile) {
      return fromGameProfile(gameProfile);
    }
    return null;
  }

  private static @Nullable HeadTexture fromGameProfile(GameProfile profile) {
    String encoded = null;
    if (profile.getProperties() != null) {
      var textures = profile.getProperties().get("textures");
      if (textures != null) {
        for (Property property : textures) {
          if (property != null && property.getValue() != null && !property.getValue().isEmpty()) {
            encoded = property.getValue();
            break;
          }
        }
      }
    }
    HeadTexture texture = encoded == null ? null : HeadProfileTextures.fromTexturesValue(encoded);
    String name = profile.getUsername();
    UUID uuid = profile.getUniqueId() != null ? profile.getUniqueId() : profile.getProfileId();
    if (texture == null) {
      if ((name == null || name.isEmpty()) && uuid == null) {
        return null;
      }
      return new HeadTexture(name, uuid, null, false);
    }
    return new HeadTexture(name, uuid, texture.url(), texture.slim());
  }

  private static @Nullable HeadTexture fromSkullOwner(ItemStack stack) {
    if (!stack.hasNBTTag()) {
      return null;
    }
    NBTTagCompound tag = stack.getNBTTag();
    if (tag == null || !tag.contains("SkullOwner")) {
      return null;
    }
    if (tag.contains("SkullOwner", NBTTagType.STRING)) {
      String name = tag.getString("SkullOwner");
      if (name == null || name.isEmpty()) {
        return null;
      }
      return new HeadTexture(name, null, null, false);
    }
    if (!tag.contains("SkullOwner", NBTTagType.COMPOUND)) {
      return null;
    }
    NBTTagCompound owner = tag.getCompound("SkullOwner");
    if (owner == null) {
      return null;
    }
    String name = owner.contains("Name", NBTTagType.STRING) ? owner.getString("Name") : null;
    UUID uuid = null;
    if (owner.contains("Id", NBTTagType.STRING)) {
      uuid = HeadProfileTextures.parseUuid(owner.getString("Id"));
    } else if (owner.contains("Id", NBTTagType.INT_ARRAY)) {
      uuid = HeadProfileTextures.uuidFromIntArray(owner.getIntArray("Id"));
    }
    String encoded = textureValue(owner);
    HeadTexture texture = encoded == null ? null : HeadProfileTextures.fromTexturesValue(encoded);
    if (texture == null && (name == null || name.isEmpty()) && uuid == null) {
      return null;
    }
    if (texture == null) {
      return new HeadTexture(name, uuid, null, false);
    }
    return new HeadTexture(
        name == null || name.isEmpty() ? null : name,
        uuid,
        texture.url(),
        texture.slim()
    );
  }

  private static @Nullable String textureValue(NBTTagCompound owner) {
    if (!owner.contains("Properties", NBTTagType.COMPOUND)) {
      return null;
    }
    NBTTagCompound properties = owner.getCompound("Properties");
    if (properties == null || !properties.contains("textures", NBTTagType.LIST)) {
      return null;
    }
    NBTTagList<?, ?> textures = properties.getList("textures", NBTTagType.COMPOUND);
    if (textures == null || textures.isEmpty()) {
      return null;
    }
    if (!(textures.get(0) instanceof NBTTagCompound entry)) {
      return null;
    }
    if (!entry.contains("Value", NBTTagType.STRING)) {
      return null;
    }
    return entry.getString("Value");
  }

  private static @Nullable HeadTexture merge(HeadTexture primary, HeadTexture fallback) {
    if (primary == null) {
      return fallback;
    }
    if (fallback == null) {
      return primary;
    }
    String url = primary.url() != null ? primary.url() : fallback.url();
    String name = primary.name() != null ? primary.name() : fallback.name();
    UUID uuid = primary.uuid() != null ? primary.uuid() : fallback.uuid();
    boolean slim = primary.url() != null ? primary.slim() : fallback.slim();
    if (url == null && name == null && uuid == null) {
      return null;
    }
    return new HeadTexture(name, uuid, url, slim);
  }
}
