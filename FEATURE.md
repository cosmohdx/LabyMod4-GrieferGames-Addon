# Features

Übersicht aller Addon-Funktionen. Haken bedeutet: im Code für diese Plattform aktiv.

**1.8** = Citybuild / `SubServerType.REGULAR`  
**Cloud** = Cloud-Netz / `SubServerType.CLOUD`

Solange das Netz noch nicht erkannt ist (`SubServerType.UNKNOWN`), bleiben Funktionen aus, die ein bestimmtes Netz voraussetzen. Nach dem Verlassen des Servers gilt das Netz wieder als unbekannt.

| Feature | 1.8 | Cloud |
|---|---|---|
| Addon-Master-Switch | ✓ | ✓ |
| Chat-Tab (2. Chat) | ✓ | ✓ |
| Plot-Chat in den 2. Chat | ✓ | ✓ |
| Private Nachrichten in den 2. Chat | ✓ | ✓ |
| Private-Nachricht-Sound | ✓ | ✓ |
| Click to Reply | ✓ | |
| Ignore-Liste lesbarer | ✓ | |
| Falsche Commands blocken (`7p` …) | ✓ | ✓ |
| Command-Großschreibung korrigieren | | ✓ |
| Prefix-Farben über Spielern | ✓ | ✓ |
| Mentions highlighten | ✓ | ✓ |
| TPA/TPAHERE highlighten | ✓ | |
| Realname-Anzeige | ✓ | ✓ |
| Remover (Chat / Hover / Notify) | ✓ | ✓ |
| Vote-Nachrichten verstecken | ✓ | |
| News verstecken | ✓ | ✓ |
| Leere Zeilen entfernen | ✓ | ✓ |
| Supreme-Leerzeilen entfernen | ✓ | ✓ |
| Magic-Prefix ersetzen | ✓ | ✓ |
| Magic-Clantags ersetzen | ✓ | ✓ |
| Chat-Zeit | ✓ | ✓ |
| Zahlungen loggen / highlighten / warnen | ✓ | |
| Bank-Nachrichten / Notify | ✓ | |
| Income-HUD | ✓ | |
| Booster laden / Menü schließen / HUD | ✓ | |
| Auto-Portal | ✓ | |
| Subserver-Wechsel-Nachricht | ✓ | ✓ |
| Auto-Chatfarbe (`&`) | ✓ | |
| Auto-Chatfarbe (Hex) | | ✓ |
| Farbverlauf (`##hex`) | | ✓ |
| AFK-Erkennung | ✓ | ✓ |
| AFK-Nick | ✓ | ✓ |
| AFK-Antwort auf PMs | ✓ | ✓ |
| CityBuild in LabyChat | ✓ | ✓ |
| CityBuild in Discord | ✓ | ✓ |
| Nickname-HUD | ✓ | |
| Redstone-HUD (MysteryMod) | ✓ | ✓ |
| Fly-HUD | ✓ | ✓ |
| Delay-HUD | ✓ | ✓ |
| Subserver-HUD | ✓ | ✓ |
| Spieler in der Nähe | ✓ | ✓ |

## Spieler in der Nähe

Grenzwertig gegenüber §2 Abs. 5 und 8 (kein Radar, keine visuellen Vorteile). Standard **aus**. Mit den Voreinstellungen nur Spieler in direkter Sichtlinie, Radius 16 (höchstens 32), nur Citybuilds, Distanz nur grob (`<5 m`, `<10 m`, `<20 m`, `<32 m`). Keine Richtung, keine Koordinaten, kein Ton, keine Auflösung von Nicks.

Auf 1.8 zählen Citybuilds (`nature`, `extreme`, `evil`, `cb…`). Farmwelten (`lava`, `wasser`), der Event-Server und auf der Cloud Farm, Event und Minigame bleiben aus, solange „Nur auf Citybuilds“ an ist. Solange das Netz unbekannt ist, bleibt die Liste leer.

Die Sichtlinie geht vom Auge zum Kopf oder zur Körpermitte. Blöcke mit Kollisionsform blockieren sie, so wie die Vanilla-Prüfung. Glas, Zäune und Laub blockieren deshalb je nach Version, sobald ihre Kollisionsform den Strahl trifft. Im Zweifel wird der Spieler nicht angezeigt. Spieler auf einem Reittier zählen mit, der eigene Spieler und die Spectator-Kamera nicht.

Nicht veröffentlichen, bevor der GrieferGames-Support die Funktion freigegeben hat. Wird die Option „Sichtlinie aus“ abgelehnt, muss sie entfernt und fest auf an gesetzt werden.

## Nicht in diesem Branch

| Feature | Status |
|---|---|
| Auto-Update | nur i18n, Store-Version ohne eigenen Updater |
| Website-Button | nur i18n |
| Addon-loaded / Update-Toasts | nur i18n |
| Global-Chat Click-to-MSG | entfernt (Server-seitig vorhanden) |
