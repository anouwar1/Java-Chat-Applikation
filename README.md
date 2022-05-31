# Experimentieranwendung **_Datenkommunikation_**
Dieses Projekt dient als Lenrprojekt für die Entwicklung einer verteilten Anwendung auf Basis von Java Sockets. Als Kommunikationsanwendung wird eine einfache Chat-Anwendung verwendet, bei der mehrere Clients miteinander über eine Chat-Gruppe chatten können.

Es handelt sich um eine Client-/Server-Anwendung mit Chat-Clients und einem Chat-Server.

Die Chat-Anwendung nutzt ein einfaches Chat-Protokoll (Anwendungsprotokoll) zur Kommunikation, das folgende Nachrichtentypen verwendet:

- Login-Request
- Login-Response
- Login-Event
- Logout-Request
- Logout-Response
- Logout-Event
- ChatMessage-Request
- ChatMessage-Response
- ChatMessage-Events

Die Requests werden vom Chat-Client gesendet, die Events dienen der Verteilung an alle angemeldeten Clients, die Responses sind die Antworten vom Chat_Server an den initiierenden Chat-Client. Login-Requests dienen der Login-Anfrage eines Teilnehmers (Clients), Logout-Requests der Logout-Anfrage, ChatMessage-Requests zum Senden einer Chat-Nachricht an alle angemeldeten Teilnehmer.
Die Verteilung an alle Teilnehmer erfolgt über Event-Nachrichten (Login-Events, Logout-Event, ChatMessage-Event).

## Projekt einrichten

- Gradle 7.x muss lauffähig sein
- OpenJDK 17 installieren
- Im Projekt SDK 17 einstellen: 
  - settings->build->gradle->SDK 17 (bzw. Preferences bei Mac OS)
  - library-settings->SDK 17
- Docker installieren

## Start der Anwendung

Alle Ports, die für die Kommunikation über die verschiedenen Techniken benötigt werden, sind standardmäßig eingestellt.

Standardmäßig werden alle Komponenten auf einem Rechner (localhost) gestartet.

### Server starten

Der Chat-Server wird Clients werden über den Aufruf von ChatServerStarter gestartet.

### Client starten
Clients werden über den Aufruf von ChatClientStarter gestartet. In der Client-GUI kann dann die Kommunikationsart durch Angabe des Servertyps angegeben werden.

#### Erweiterte Client-Konfiguration
Zum Start des Clients wird eine Konfiguration aus der Datei ./resources/configuration.properties im Modul 'client' geladen. Folgendes ist hierbei zu beachten:

- rmi.server.hostname - Die IP-Adresse des Clients ist hier zu konfigurieren. Für lokale Tests reicht eine Localhost-Adresse. Bei Remote-Verbindungen ist das die IPv4-Adresse des Hosts, auf dem der Client laufen wird.

### Benchmark-Client starten
Ein Benchmark zur Lasterzeugung und Leistungsmessung wird über den Aufruf von BenchmarkingClientStarter initiiert. In der dann erscheinenden GUI kann ebenfalls die Kommunikationsart durch Angabe des Servertyps angegeben werden.

## Projektinhalt
Das Projekt ist als Gradle-Multiprojekt organisiert (siehe settings.gradle)
Die gesamte Anwendung befindet sich im Gradle-Projekt chat. Einige Beispielprogramme im Gradle-Projekt beispielprogramme sind ergänzend beigefügt.

Im Folgenden werden die einzelnen Ordner kurz erläutert:

### examples
Einige Programme zum Test von Java-Klassen, nicht relevant für die Chat-Anwendung, nur zum Test.

### benchmark
Simulation von Chat-Clients zur Leistungsmessung und zum Test der Anwendung.

### client
In diesem Ordner befindet sich der Chat-Client mit User Interface als eigenes Gradle-Unterprojekt.

### communication
In diesem Ordner befinden sich die Implementierungen der verschiedenen Kommunikationstechniken als eigenes Gradle-Unterprojekt.

### shared
Gemeinsam benutzte Klassen und Interfaces.

### server
In diesem Ordner befindet sich der Chat-Server mit User Interface als eigenes Gradle-Unterprojekt.
 
### chat.userservice
Nicht implementiert!
Idee: Auf der Serverseite ist in der Advanced-Lösung mit JMS und Spring Boot eine Erweiterung vorgesehen, die eine Überprüfung der Benutzer anhand eines Token-basierten Verfahrens ermöglicht. Ein USer-Service ist zu ergänzen. Auch die Clientseite und der Benchmarking-Client sind noch um die Authentifizierung über den User-Service zu erweitern.
Auch die Clientseite und der Benchmarking-Client sind noch um die Authentifizierung über den User-Service zu erweitern.
Für die Implementierung der Benutzerliste ist die IN-Memory-Datenbank Redis vorgesehen.

chat.auditlogserver

Beispielsprogramm für einen Auditlog-Server, die die Studierenden in einer Studiearbeit anfertigen sollen.

### docker
In diesem Ordner befindet sich die Konfiguration für die Ausführung der abhängigen Dienste in Docker.

### config
In diesem Ordner liegen die CheckStyle-Konfiguration und die Log4j-Konfiguration.

Für das Logging wird durchgängig Log4j2 genutzt. Die Konfiguration der Logs liegt in den Dateien
- log4j2.benchmarkingClient.xml
- log4j2chatClient.xml
- log4j2.chatServer.xml

Die Logs werden in den Ordner logs geschrieben.

### documentation
In diesem Ordner liegen die Javadoc-Dateien sowie eine Dokumentation des Aufbaus der Benchmarking-Protokolldatei.

### logs
In diesen Ordner werden die Logdateien abgelegt.

### uml
Vorgesehen für UML-Diagramme.


## Sonstiges

### Benchmarking
Protokollierung der Benchmarking-Ergebnisse erfolgt nach jedem Lauf in der Datei Benchmarking-ChatApp-Protokolldatei.

### Unit-Tests
Tests sind noch nicht implementiert.