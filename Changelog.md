# Changelog

## Unreleased

### Netzwerk

Direkt nach dem Beitreten ist noch nicht klar, ob du auf einem Citybuild (1.8) oder auf der Cloud spielst. Funktionen, die nur auf einem der beiden Netze laufen, warten deshalb kurz. Dazu gehören zum Beispiel Zahlungen, die Bank, Booster, der Nickname und die automatische Chatfarbe. Sobald das Netz erkannt ist, verhalten sie sich wie bisher. Beim Verlassen des Servers setzt das Addon die Erkennung zurück.

### Chat

Auf dem Citybuild (1.8) steht vor Spielernachrichten der Kopf aus der Tabliste. Das gilt für den öffentlichen Chat, Privatnachrichten und den Plot-Chat. Zahlungen und Systemzeilen bleiben ohne Kopf. Ist die Chat-Zeit an, steht sie vor dem Kopf. Auf der Cloud und solange das Netz noch unbekannt ist, fügt das Addon keinen Kopf ein.

Lange Nachrichten auf dem Citybuild lassen sich an Leerzeichen aufteilen. Die Funktion ist standardmäßig aus. Es werden höchstens drei Teile gesendet, mit mindestens drei Sekunden Abstand. Ein einzelnes Wort, das nicht in einen Teil passt, wird gar nicht gesendet. Global-Chat mit `@` bleibt ungeteilt. Wechselt der Server oder lehnt er einen Teil ab, fallen die restlichen Teile weg. Das Chat-Eingabefeld von Minecraft 1.8.9 fasst selbst nur 100 Zeichen; längere Texte tippst du mit einem neueren Client.

## 1.2.0

Das Addon läuft mit dem aktuellen LabyMod auf Minecraft 1.8.9 bis 26.3. Deine bisherigen Einstellungen bleiben erhalten und werden beim ersten Start in die neue Übersicht übernommen.

### Einstellungen

Die Einstellungen sind nach Funktionen sortiert: Chat, Bezahlungen, Remover, Automatisierungen, AFK, Booster und Freunde. Jede Funktion hat einen eigenen Schalter.

ItemRemover und MobRemover sind ein Eintrag namens Remover. War einer der beiden alten Schalter an, bleibt Remover an. Im zweiten Chat gibt es dafür nur noch eine Kategorie.

### Remover

Auf Citybuild gelten weiter die bekannten Chat-Nachrichten, inklusive Zeitstempel beim Drüberfahren und Benachrichtigung.

Auf der Cloud kommt die Restzeit direkt vom Server, für Items und für Entities. Eine Benachrichtigung erscheint einmal, sobald noch höchstens eine Minute übrig ist.

Im HUD-Editor unter GrieferGames gibt es zwei neue Anzeigen: ItemRemover und MobRemover. Sie zählen die Restzeit herunter, zum Beispiel `1:30`. Solange der Server keine Zeit schickt, bleiben sie aus.

### Booster

Aktive Booster kommen beim Joinen direkt vom Server. 
Das Booster-Menü öffnet sich nur, wenn diese Meldung ausbleibt. 
Die Restzeiten werden dann auf Citybuild aus dem Menü gelesen. 
Das Menü lässt sich wie bisher ausblenden.

### Block des Tages

Im HUD-Editor unter GrieferGames gibt es die Anzeige Block des Tages. Sie zeigt den heutigen Block und daneben, wie oft du ihn in dieser Sitzung gesammelt hast, zum Beispiel `Diamond Ore (3)`. Wechselt der Block, beginnt der Zähler bei null.

### Sonstiges

- Die Redstone-Anzeige im HUD funktioniert wieder.
- Untertitel über Spielern funktionieren wieder.
- Der aktuelle Citybuild wird wieder im LabyChat angezeigt, wenn du das in den Freunde-Einstellungen an hast.
- Discord zeigt den Citybuild weiter an.
- Klick-zum-Antworten im Global-Chat ist entfernt. Das kann der Server selbst.
