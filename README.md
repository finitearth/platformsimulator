# Digitale Plattformen - Simulation

## Versionen

### V1.0 ![Static Badge](https://img.shields.io/badge/Status-Umgesetzt-green)


- Design Überarbeitung und Responsivität
- Code-Cleaning und Restrukturierung
- Anpassung der HTML mit Input-Feldern und Buttons
- JS zur Generierung von Code und Export in Email
- Input-Validierung
- Umsetzung von Events u. Tech-Tree im Code (statt Bild)
- JS mit aktuellem Spielstand (Variablen, erforschte Technologien, ...)

### V2.0 ![Static Badge](https://img.shields.io/badge/Status-Entwurf-yellow)

- [Architecture](https://github.com/finitearth/platformsimulator/blob/main/Architecture.pdf)

## Angebot

Variante 1: Automatisierung der Java Eingabe

- Vortreffen & Review (6)
- HTML anpassen (Buttons usw.) (20)
- JS zum export coden (10)

→ Gesamtaufwand 36h

Variante 2: Server-Client Architektur ohne Änderung am Spielmechanismus

- Vortreffen & Review (6)
- Genaue Absprache zur verfügbaren Architektur (4)
- Java-Backend: Springboot für das Empfangen von HTTP-Requests (Entwurf einer Architektur) (30)
- Testen der Harware anhand einer leichtgewichtigen Testplatform (30)
- Anpassung der Weboberfläche wie in Variante 1 (30)
- Code verstehen & umstrukturieren (Klassenkonzept umsetzen) (60)
- WebApp Schema: Verbindungen zur Webapp (Umsetzung der Architektur) (40), JS für das Senden von HTTP-Requests, SQL-Datenbank (Spielstandspeicherung, verschlüsselte Anmeldedaten), Jedes Team hat einen Login mit personalisierter Ansicht
- Admin-Seite (20): Erstellen von Simulationen, Anlegen von Teams, Einstellen von Simulationsparametern, Eventplatzierungen
- Unit- und Integrationstesting (20)

→ Gesamtaufwand: 240h

Variante 3: Server-Client Architektur mit verbesserter Spielerfahrung
- Enthält alle Funktionen von Variante 2 (240)
- Echtzeitsimulation (statt rundenbasiert, wird kontinuierlich nach Ticks aktualisiert) (20)
- Teams können live interagieren (mit evtl. Sperrzeit) (10)
- Veränderung der Spiellogik (50)
- ermöglicht mehrere Admins (Nutzung der Webapp durch anderen Dozenten) (10)

→ Gesamtaufwand: 330h
