# Changelog

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

### Kontostand und Bankguthaben

Im HUD-Editor unter GrieferGames gibt es zwei neue Anzeigen: Kontostand und Bankguthaben. Sie zeigen den Betrag, den der Server schickt, zum Beispiel `$12,345.67`. Solange noch kein Betrag angekommen ist, bleiben sie aus. In den Einstellungen der Anzeige kannst du Cent ausblenden, große Beträge kürzen (zum Beispiel `1,25 Mio.`) und die Anzeige bei 0 verstecken.

Der erste Betrag nach dem Joinen setzt nur den aktuellen Stand. Erst spätere Änderungen sind eine Differenz. Nach dem Verlassen des Servers beginnt das wieder von vorn.

Unter Bezahlungen gibt es den Schalter „Zeitmessung loggen“. Er ist aus. Wenn du ihn einschaltest, schreibt das Addon ins Log, wann Kontostand oder Bankguthaben ankommen und wann eine Zahlungszeile im Chat ankommt. Damit lässt sich vergleichen, was zuerst da ist.

Das Icon der Einnahmen-Anzeige wird wieder angezeigt.

### Block des Tages

Im HUD-Editor unter GrieferGames gibt es die Anzeige Block des Tages. Sie zeigt den heutigen Block und daneben, wie oft du ihn in dieser Sitzung gesammelt hast, zum Beispiel `Diamond Ore (3)`. Wechselt der Block, beginnt der Zähler bei null.

### Sonstiges

- Die Redstone-Anzeige im HUD funktioniert wieder.
- Untertitel über Spielern funktionieren wieder.
- Der aktuelle Citybuild wird wieder im LabyChat angezeigt, wenn du das in den Freunde-Einstellungen an hast.
- Discord zeigt den Citybuild weiter an.
- Klick-zum-Antworten im Global-Chat ist entfernt. Das kann der Server selbst.
