### Voraussetzungen
* Das Projekt setzt aufgrund der DB-Komponente eine Datenbankverbindung voraus.
Hier wurde MariaDB implementiert. 
Aufgrund der Kompatibilität von Flyway wird eine MariaDB Version vor 10.11 erwartet.
Der MariaDB-Server sollte auf Port 3306 laufen, mit einem User 'root' mit Passwort 'Password',
und eine Datenbank mit dem Namen 'superdupermarkt' haben (kann alles in application.properties geändert werden).

* Java 21

### Start

* Die SuperDuperMarktApplication starten, danach erscheint eine Konsolenanwendung, wo ihr
'help' eingeben könnt, um eine Zusammenfassung aller Befehle zu sehen. Die relevanten
befinden sich ganz unten. Diese sollten selbsterklärend sein, ansonsten kann ich
bei Fragen gerne helfen

* Unit-Tests können gestartet werden, indem AllUnitTest.java gestartet wird
* Unter src/main/resources kann eine .csv Datei für den Import abgelegt werden