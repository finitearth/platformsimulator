# Digitale Plattformen - Simulation 

<img src="https://github.com/finitearth/platformsimulator/blob/main/htmls/template/gfx/MainLogo.png" alt="Logo" width="150"/>



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

- Vortreffen & Review
- HTML anpassen (Buttons usw.)
- JS zum export coden

Variante 2: Server-Client Architektur ohne Änderung am Spielmechanismus

- Vortreffen & Review
- Genaue Absprache zur verfügbaren Architektur
- Java-Backend: Springboot für das Empfangen von HTTP-Requests (Entwurf einer Architektur)
- Testen der Harware anhand einer leichtgewichtigen Testplatform
- Anpassung der Weboberfläche wie in Variante 1
- Code verstehen & umstrukturieren (Klassenkonzept umsetzen)
- WebApp Schema: Verbindungen zur Webapp (Umsetzung der Architektur), JS für das Senden von HTTP-Requests, SQL-Datenbank (Spielstandspeicherung, verschlüsselte Anmeldedaten), Jedes Team hat einen Login mit personalisierter Ansicht
- Admin-Seite: Erstellen von Simulationen, Anlegen von Teams, Einstellen von Simulationsparametern, Eventplatzierungen
- Unit- und Integrationstesting

Variante 3: Server-Client Architektur mit verbesserter Spielerfahrung
- Enthält alle Funktionen von Variante 2
- Echtzeitsimulation (statt rundenbasiert, wird kontinuierlich nach Ticks aktualisiert)
- Teams können live interagieren (mit evtl. Sperrzeit)
- Veränderung der Spiellogik
- ermöglicht mehrere Admins (Nutzung der Webapp durch anderen Dozenten)
