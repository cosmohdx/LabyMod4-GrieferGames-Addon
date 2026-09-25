#!/usr/bin/env python3
"""Historical one-shot from the first remaster commit.

Do not re-run. A later pass split item remover, mob remover, AFK, nickname,
delay, fly and subserver into their own packages. Re-running this script
would move those classes back.
"""
from pathlib import Path
import shutil
import re

ROOT = Path(__file__).resolve().parents[1]
CORE = ROOT / "core/src/main/java"
GAME = ROOT / "game-runner/src"
OLD = CORE / "de/cosmohdx/griefergames"

# old relative path (from cosmohdx/griefergames) -> (new package, new filename)
MOVES = {
    "GrieferGames.java": ("de.cosmohdx.griefergames", "GrieferGames.java"),
    "Constants.java": ("de.cosmohdx.griefergames", "Constants.java"),

    "utils/Helper.java": ("de.cosmohdx.griefergames.core", "Helper.java"),
    "utils/GrieferGamesController.java": ("de.cosmohdx.griefergames.core", "GrieferGamesController.java"),
    "enums/SubServerType.java": ("de.cosmohdx.griefergames.core", "SubServerType.java"),
    "enums/CloudRegionType.java": ("de.cosmohdx.griefergames.core", "CloudRegionType.java"),
    "settings/GrieferGamesConfig.java": ("de.cosmohdx.griefergames.core", "GrieferGamesConfig.java"),
    "commands/GGMessageCommand.java": ("de.cosmohdx.griefergames.core", "GGMessageCommand.java"),

    "chat/modules/ChatModule.java": ("de.cosmohdx.griefergames.feature.chat", "ChatModule.java"),
    "chat/events/GGChatProcessEvent.java": ("de.cosmohdx.griefergames.feature.chat", "GGChatProcessEvent.java"),
    "chat/modules/Blanks.java": ("de.cosmohdx.griefergames.feature.chat", "Blanks.java"),
    "chat/modules/News.java": ("de.cosmohdx.griefergames.feature.chat", "News.java"),
    "chat/modules/ChatTime.java": ("de.cosmohdx.griefergames.feature.chat", "ChatTime.java"),
    "chat/modules/PlotChat.java": ("de.cosmohdx.griefergames.feature.chat", "PlotChat.java"),
    "chat/modules/PrivateMessage.java": ("de.cosmohdx.griefergames.feature.chat", "PrivateMessage.java"),
    "chat/modules/Mention.java": ("de.cosmohdx.griefergames.feature.chat", "Mention.java"),
    "chat/modules/AntiMagicPrefix.java": ("de.cosmohdx.griefergames.feature.chat", "AntiMagicPrefix.java"),
    "chat/modules/AntiMagicClanTag.java": ("de.cosmohdx.griefergames.feature.chat", "AntiMagicClanTag.java"),
    "chat/modules/BetterIgnoreList.java": ("de.cosmohdx.griefergames.feature.chat", "BetterIgnoreList.java"),
    "chat/modules/Vote.java": ("de.cosmohdx.griefergames.feature.chat", "Vote.java"),
    "chat/modules/Realname.java": ("de.cosmohdx.griefergames.feature.chat", "Realname.java"),
    "chat/modules/Teleport.java": ("de.cosmohdx.griefergames.feature.chat", "Teleport.java"),
    "chat/modules/ItemRemover.java": ("de.cosmohdx.griefergames.feature.chat", "ItemRemover.java"),
    "chat/modules/MobRemover.java": ("de.cosmohdx.griefergames.feature.chat", "MobRemover.java"),
    "chat/modules/Nickname.java": ("de.cosmohdx.griefergames.feature.chat", "Nickname.java"),
    "listener/GGMessageReceiveListener.java": ("de.cosmohdx.griefergames.feature.chat", "GGMessageReceiveListener.java"),
    "listener/GGMessageSendListener.java": ("de.cosmohdx.griefergames.feature.chat", "GGMessageSendListener.java"),
    "listener/GGNameTagListener.java": ("de.cosmohdx.griefergames.feature.chat", "GGNameTagListener.java"),
    "listener/GGKeyListener.java": ("de.cosmohdx.griefergames.feature.chat", "GGKeyListener.java"),
    "settings/GrieferGamesChatConfig.java": ("de.cosmohdx.griefergames.feature.chat", "GrieferGamesChatConfig.java"),
    "settings/GrieferGamesChatTabConfig.java": ("de.cosmohdx.griefergames.feature.chat", "GrieferGamesChatTabConfig.java"),
    "settings/GrieferGamesNameHighlightConfig.java": ("de.cosmohdx.griefergames.feature.chat", "GrieferGamesNameHighlightConfig.java"),
    "enums/Sounds.java": ("de.cosmohdx.griefergames.feature.chat", "Sounds.java"),
    "enums/RealnamePosition.java": ("de.cosmohdx.griefergames.feature.chat", "RealnamePosition.java"),
    "hud/NicknameHudWidget.java": ("de.cosmohdx.griefergames.feature.chat", "NicknameHudWidget.java"),

    "booster/Booster.java": ("de.cosmohdx.griefergames.feature.booster", "Booster.java"),
    "booster/BoosterController.java": ("de.cosmohdx.griefergames.feature.booster", "BoosterController.java"),
    "booster/BreakBooster.java": ("de.cosmohdx.griefergames.feature.booster", "BreakBooster.java"),
    "booster/DropBooster.java": ("de.cosmohdx.griefergames.feature.booster", "DropBooster.java"),
    "booster/ExperienceBooster.java": ("de.cosmohdx.griefergames.feature.booster", "ExperienceBooster.java"),
    "booster/FlyBooster.java": ("de.cosmohdx.griefergames.feature.booster", "FlyBooster.java"),
    "booster/MobBooster.java": ("de.cosmohdx.griefergames.feature.booster", "MobBooster.java"),
    "chat/modules/Booster.java": ("de.cosmohdx.griefergames.feature.booster", "BoosterChatModule.java"),
    "hud/BoosterHudWidget.java": ("de.cosmohdx.griefergames.feature.booster", "BoosterHudWidget.java"),
    "settings/GrieferGamesBoosterToolsConfig.java": ("de.cosmohdx.griefergames.feature.booster", "GrieferGamesBoosterToolsConfig.java"),

    "chat/modules/Payment.java": ("de.cosmohdx.griefergames.feature.payment", "Payment.java"),
    "chat/modules/Bank.java": ("de.cosmohdx.griefergames.feature.payment", "Bank.java"),
    "settings/GrieferGamesPaymentsConfig.java": ("de.cosmohdx.griefergames.feature.payment", "GrieferGamesPaymentsConfig.java"),
    "utils/FileManager.java": ("de.cosmohdx.griefergames.feature.payment", "FileManager.java"),
    "hud/IncomeHudWidget.java": ("de.cosmohdx.griefergames.feature.payment", "IncomeHudWidget.java"),
    "enums/TransactionType.java": ("de.cosmohdx.griefergames.feature.payment", "TransactionType.java"),

    "settings/GrieferGamesAutomationsConfig.java": ("de.cosmohdx.griefergames.feature.automation", "GrieferGamesAutomationsConfig.java"),
    "settings/GrieferGamesAFKConfig.java": ("de.cosmohdx.griefergames.feature.automation", "GrieferGamesAFKConfig.java"),
    "enums/ChatColor.java": ("de.cosmohdx.griefergames.feature.automation", "ChatColor.java"),
    "chat/modules/WaitTime.java": ("de.cosmohdx.griefergames.feature.automation", "WaitTime.java"),
    "hud/DelayHudWidget.java": ("de.cosmohdx.griefergames.feature.automation", "DelayHudWidget.java"),
    "listener/GGTickListener.java": ("de.cosmohdx.griefergames.feature.automation", "GGTickListener.java"),

    "settings/GrieferGamesFriendsConfig.java": ("de.cosmohdx.griefergames.feature.friends", "GrieferGamesFriendsConfig.java"),

    "listener/GGServerJoinListener.java": ("de.cosmohdx.griefergames.feature.server", "GGServerJoinListener.java"),
    "listener/GGServerQuitListener.java": ("de.cosmohdx.griefergames.feature.server", "GGServerQuitListener.java"),
    "listener/GGScoreboardListener.java": ("de.cosmohdx.griefergames.feature.server", "GGScoreboardListener.java"),
    "listener/GGSubServerChangeListener.java": ("de.cosmohdx.griefergames.feature.server", "GGSubServerChangeListener.java"),
    "listener/GGServerMessageListener.java": ("de.cosmohdx.griefergames.feature.server", "GGServerMessageListener.java"),
    "chat/events/GGSubServerChangeEvent.java": ("de.cosmohdx.griefergames.feature.server", "GGSubServerChangeEvent.java"),
    "hud/SubServerHUDWidget.java": ("de.cosmohdx.griefergames.feature.server", "SubServerHUDWidget.java"),
    "hud/FlyHudWidget.java": ("de.cosmohdx.griefergames.feature.server", "FlyHudWidget.java"),
    "hud/RedstoneHudWidget.java": ("de.cosmohdx.griefergames.feature.server", "RedstoneHudWidget.java"),
}

RENAMES = {
    "de.cosmohdx.griefergames.chat.modules.Booster": "de.cosmohdx.griefergames.feature.booster.BoosterChatModule",
}

# Build simple old-package prefix replacements after exact class moves.
PACKAGE_REPLACEMENTS = [
    ("de.cosmohdx.griefergames.chat.modules", "de.cosmohdx.griefergames.feature.chat"),
    ("de.cosmohdx.griefergames.chat.events", "de.cosmohdx.griefergames.feature.chat"),
    ("de.cosmohdx.griefergames.booster", "de.cosmohdx.griefergames.feature.booster"),
    ("de.cosmohdx.griefergames.settings", "PLACEHOLDER_SETTINGS"),
    ("de.cosmohdx.griefergames.listener", "PLACEHOLDER_LISTENER"),
    ("de.cosmohdx.griefergames.hud", "PLACEHOLDER_HUD"),
    ("de.cosmohdx.griefergames.utils", "PLACEHOLDER_UTILS"),
    ("de.cosmohdx.griefergames.enums", "PLACEHOLDER_ENUMS"),
    ("de.cosmohdx.griefergames.commands", "de.cosmohdx.griefergames.core"),
]


def pkg_to_dir(pkg: str) -> Path:
    return CORE / pkg.replace(".", "/")


def rewrite(text: str, new_pkg: str, new_class_name: str | None) -> str:
    text = re.sub(r"^package .+;", f"package {new_pkg};", text, count=1, flags=re.M)
    if new_class_name == "BoosterChatModule":
        text = text.replace("public class Booster extends ChatModule", "public class BoosterChatModule extends ChatModule")
        text = text.replace("public Booster(GrieferGames", "public BoosterChatModule(GrieferGames")
    for old, new in RENAMES.items():
        text = text.replace(old, new)
    # class-specific imports that would be wrong if we only swap package prefixes
    specific = {
        "de.cosmohdx.griefergames.chat.modules.Payment": "de.cosmohdx.griefergames.feature.payment.Payment",
        "de.cosmohdx.griefergames.chat.modules.Bank": "de.cosmohdx.griefergames.feature.payment.Bank",
        "de.cosmohdx.griefergames.chat.modules.WaitTime": "de.cosmohdx.griefergames.feature.automation.WaitTime",
        "de.cosmohdx.griefergames.chat.events.GGSubServerChangeEvent": "de.cosmohdx.griefergames.feature.server.GGSubServerChangeEvent",
        "de.cosmohdx.griefergames.utils.FileManager": "de.cosmohdx.griefergames.feature.payment.FileManager",
        "de.cosmohdx.griefergames.utils.Helper": "de.cosmohdx.griefergames.core.Helper",
        "de.cosmohdx.griefergames.utils.GrieferGamesController": "de.cosmohdx.griefergames.core.GrieferGamesController",
        "de.cosmohdx.griefergames.enums.SubServerType": "de.cosmohdx.griefergames.core.SubServerType",
        "de.cosmohdx.griefergames.enums.CloudRegionType": "de.cosmohdx.griefergames.core.CloudRegionType",
        "de.cosmohdx.griefergames.enums.TransactionType": "de.cosmohdx.griefergames.feature.payment.TransactionType",
        "de.cosmohdx.griefergames.enums.ChatColor": "de.cosmohdx.griefergames.feature.automation.ChatColor",
        "de.cosmohdx.griefergames.enums.Sounds": "de.cosmohdx.griefergames.feature.chat.Sounds",
        "de.cosmohdx.griefergames.enums.RealnamePosition": "de.cosmohdx.griefergames.feature.chat.RealnamePosition",
        "de.cosmohdx.griefergames.settings.GrieferGamesConfig": "de.cosmohdx.griefergames.core.GrieferGamesConfig",
        "de.cosmohdx.griefergames.settings.GrieferGamesChatConfig": "de.cosmohdx.griefergames.feature.chat.GrieferGamesChatConfig",
        "de.cosmohdx.griefergames.settings.GrieferGamesChatTabConfig": "de.cosmohdx.griefergames.feature.chat.GrieferGamesChatTabConfig",
        "de.cosmohdx.griefergames.settings.GrieferGamesNameHighlightConfig": "de.cosmohdx.griefergames.feature.chat.GrieferGamesNameHighlightConfig",
        "de.cosmohdx.griefergames.settings.GrieferGamesPaymentsConfig": "de.cosmohdx.griefergames.feature.payment.GrieferGamesPaymentsConfig",
        "de.cosmohdx.griefergames.settings.GrieferGamesAutomationsConfig": "de.cosmohdx.griefergames.feature.automation.GrieferGamesAutomationsConfig",
        "de.cosmohdx.griefergames.settings.GrieferGamesAFKConfig": "de.cosmohdx.griefergames.feature.automation.GrieferGamesAFKConfig",
        "de.cosmohdx.griefergames.settings.GrieferGamesFriendsConfig": "de.cosmohdx.griefergames.feature.friends.GrieferGamesFriendsConfig",
        "de.cosmohdx.griefergames.settings.GrieferGamesBoosterToolsConfig": "de.cosmohdx.griefergames.feature.booster.GrieferGamesBoosterToolsConfig",
        "de.cosmohdx.griefergames.listener.GGMessageReceiveListener": "de.cosmohdx.griefergames.feature.chat.GGMessageReceiveListener",
        "de.cosmohdx.griefergames.listener.GGMessageSendListener": "de.cosmohdx.griefergames.feature.chat.GGMessageSendListener",
        "de.cosmohdx.griefergames.listener.GGNameTagListener": "de.cosmohdx.griefergames.feature.chat.GGNameTagListener",
        "de.cosmohdx.griefergames.listener.GGKeyListener": "de.cosmohdx.griefergames.feature.chat.GGKeyListener",
        "de.cosmohdx.griefergames.listener.GGTickListener": "de.cosmohdx.griefergames.feature.automation.GGTickListener",
        "de.cosmohdx.griefergames.listener.GGServerJoinListener": "de.cosmohdx.griefergames.feature.server.GGServerJoinListener",
        "de.cosmohdx.griefergames.listener.GGServerQuitListener": "de.cosmohdx.griefergames.feature.server.GGServerQuitListener",
        "de.cosmohdx.griefergames.listener.GGScoreboardListener": "de.cosmohdx.griefergames.feature.server.GGScoreboardListener",
        "de.cosmohdx.griefergames.listener.GGSubServerChangeListener": "de.cosmohdx.griefergames.feature.server.GGSubServerChangeListener",
        "de.cosmohdx.griefergames.listener.GGServerMessageListener": "de.cosmohdx.griefergames.feature.server.GGServerMessageListener",
        "de.cosmohdx.griefergames.hud.IncomeHudWidget": "de.cosmohdx.griefergames.feature.payment.IncomeHudWidget",
        "de.cosmohdx.griefergames.hud.NicknameHudWidget": "de.cosmohdx.griefergames.feature.chat.NicknameHudWidget",
        "de.cosmohdx.griefergames.hud.RedstoneHudWidget": "de.cosmohdx.griefergames.feature.server.RedstoneHudWidget",
        "de.cosmohdx.griefergames.hud.DelayHudWidget": "de.cosmohdx.griefergames.feature.automation.DelayHudWidget",
        "de.cosmohdx.griefergames.hud.FlyHudWidget": "de.cosmohdx.griefergames.feature.server.FlyHudWidget",
        "de.cosmohdx.griefergames.hud.BoosterHudWidget": "de.cosmohdx.griefergames.feature.booster.BoosterHudWidget",
        "de.cosmohdx.griefergames.hud.SubServerHUDWidget": "de.cosmohdx.griefergames.feature.server.SubServerHUDWidget",
        "de.cosmohdx.griefergames.commands.GGMessageCommand": "de.cosmohdx.griefergames.core.GGMessageCommand",
        "de.cosmohdx.griefergames.chat.modules.ChatModule": "de.cosmohdx.griefergames.feature.chat.ChatModule",
    }
    # longer keys first
    for old, new in sorted(specific.items(), key=lambda kv: len(kv[0]), reverse=True):
        text = text.replace(old, new)
    text = text.replace("de.cosmohdx.griefergames.chat.modules", "de.cosmohdx.griefergames.feature.chat")
    text = text.replace("de.cosmohdx.griefergames.booster", "de.cosmohdx.griefergames.feature.booster")
    return text


def main():
    staged = []
    for rel, (pkg, filename) in MOVES.items():
        src = OLD / rel
        if not src.exists():
            raise SystemExit(f"missing {src}")
        dest = pkg_to_dir(pkg) / filename
        staged.append((src, dest, pkg, filename.replace(".java", "")))

    contents = []
    for src, dest, pkg, cls in staged:
        text = rewrite(src.read_text(encoding="utf-8"), pkg, cls)
        contents.append((dest, text))

    # write to temp-ish new files first; may overlap dirs with old files
    for dest, text in contents:
        dest.parent.mkdir(parents=True, exist_ok=True)
        dest.write_text(text, encoding="utf-8")
        print("wrote", dest.relative_to(CORE))

    # delete old trees except newly written files
    keep = {dest.resolve() for dest, _ in contents}
    for path in (CORE / "de/cosmohdx/griefergames").rglob("*.java"):
        if path.resolve() not in keep:
            path.unlink()
            print("deleted leftover", path.relative_to(CORE))

    # remove empty dirs under cosmohdx
    for path in sorted((CORE / "de/cosmohdx/griefergames").rglob("*"), reverse=True):
        if path.is_dir() and not any(path.iterdir()):
            path.rmdir()

    neo = CORE / "de/neocraftr"
    if neo.exists():
        shutil.rmtree(neo)
        print("removed de.neocraftr core")

    if GAME.exists():
        for neo_game in GAME.glob("*/java/de/neocraftr"):
            shutil.rmtree(neo_game)
            print("removed", neo_game)

    # update game-runner controller imports
    for java in GAME.rglob("*.java"):
        text = java.read_text(encoding="utf-8")
        new = text.replace(
            "de.cosmohdx.griefergames.utils.GrieferGamesController",
            "de.cosmohdx.griefergames.core.GrieferGamesController",
        )
        if new != text:
            java.write_text(new, encoding="utf-8")
            print("updated import", java.relative_to(ROOT))


if __name__ == "__main__":
    main()
