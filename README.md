# thesis-poc

Dieses Repository ist ein **Proof of Concept** zu dem in meiner Bachelorarbeit entwickelten theoretischen Ansatz, Steamworks in Kotlin über die Java Foreign Function & Memory API (FFM) anzubinden.

Der Schwerpunkt liegt auf dem Modul `ffmlibrary`: einem KSP-basierten Generator, der aus annotierten Spezifikationen konkrete FFM-Bindings erzeugt.

Die Bachelorarbeit ist hier zu finden: [Repository](https://github.com/Zweistein2/thesis) / [PDF](https://github.com/Zweistein2/thesis/blob/master/out/thesis.pdf).

## Ziel des Projekts

Ziel war es, die Generierung von FFM-Bindings aus deklarativen Spezifikationen zu demonstrieren und die resultierenden Bindings in einem realen Anwendungsfall (Steamworks) zu testen.
Ebenso sollte erprobt werden, ob sich mit diesem Ansatz die bisherige Notwendigkeit von manuell geschriebenem C-Code umgehen lässt.

Dieser PoC soll zudem aufzeigen, dass sich der in der Bachelorarbeit beschriebene Ansatz nicht nur praktisch umsetzen lässt, sondern auch im Vergleich zu einem etablierten Wrapper (steamworks4j) in Bezug auf Ergonomie und Performance konkurrenzfähig ist.

Der PoC vergleicht zwei Zugriffsstrategien auf Steamworks:

- **FFM + Codegenerierung** über `poc/steamworksFFM`
- **steamworks4j Wrapper** über `poc/steamworks4j`

Damit werden Umsetzbarkeit, Ergonomie und Laufzeitverhalten des in der Arbeit beschriebenen Ansatzes praktisch überprüft und mit bisherigen Lösungen verglichen.

## Projektstruktur

- `ffmlibrary`  
  KSP-Processor für die Generierung von FFM-Implementierungen aus Annotationen.
- `poc/steamworksFFM`  
  PoC-Modul, das den `ffmlibrary`-Processor für die Generierung der Bindings verwendet.
- `poc/steamworks4j`  
  Vergleichsmodul auf Basis von `steamworks4j`.

Die Build-Logik befindet sich in der zentralen `build.gradle.kts`.

## `ffmlibrary`

`ffmlibrary` ist kein Anwendungsmodul, sondern ein Symbol Processor:

- Einstieg: `ffmlibrary/src/main/kotlin/de/potionlabs/ffmlibrary/FFMProcessor.kt`
- Logik: `ffmlibrary/src/main/kotlin/de/potionlabs/ffmlibrary/FFMVisitor.kt`

Generierungsfluss:

1. Annotierte Specs/Structs/Tuples im FFM-PoC (z. B. `poc/steamworksFFM/src/main/kotlin/de/potionlabs/specs`).
2. KSP liest `@NativeLibrary`, `@NativeFunction*`, `@NativeStruct`, `@NativeTuple`, etc. aus
3. Aus einem internen Modell (Siehe `FFMLibrary`) werden Implementierungen erzeugt.
4. Generierter Code landet unter `build/generated/ksp/main/kotlin` und wird vom Consumer-Modul mitkompiliert.

> Wichtig: Generierte Dateien nicht manuell bearbeiten; stattdessen Annotationen/Spezifikationen ändern.

## Voraussetzungen

- Windows 64-Bit
- JDK 22
- Steam/Steamworks-Umgebung für Integrationsläufe (Hierfür muss auf Steam ein Spiel eingereicht worden sein, die dazugehörige App-ID muss in `steam_appid.txt` hinterlegt sein).

Die Module laden native Steam-DLLs aus:

- `poc/steamworksFFM/src/main/resources/sdks/steamworks/redistributable_bin/win64`
- `poc/steamworks4j/src/main/resources/sdks/steamworks/redistributable_bin/win64`

`steam_appid.txt` muss mit der App-ID des eigenen Spiels im Repository hinterlegt werden. Siehe hierfür [Steamworks-Dokumentation](https://partner.steamgames.com/doc/sdk/api#SteamAPI_Init).

## Build und Ausführung

### FFM-Bindings neu generieren (inkl. Build)

```bat
.\gradlew.bat clean build --rerun-tasks --no-build-cache --exclude-task :poc:steamworksFFM:test --info thesis-poc:poc:steamworksFFM
```

### Benchmarks starten

```bat
.\gradlew.bat thesis-poc:poc:steamworksFFM:benchmark
.\gradlew.bat thesis-poc:poc:steamworks4j:benchmark
```

Danach können die Ergebnisse in den jeweiligen `BenchmarkReport.ipynb` Notebooks ausgewertet werden.

## Tests

Die Tests in `poc/steamworksFFM` und `poc/steamworks4j` sind **umgebungsabhängige Integrations-/API-Tests** (echte Steam-Aufrufe), z. B.:

- `poc/steamworksFFM/src/test/kotlin/de/potionlabs/specs/SteamAPITest.kt`
- `poc/steamworks4j/src/test/kotlin/de/potionlabs/derp/SteamworksAPITest.kt`

Je nach lokaler Steam-Umgebung können diese Tests fehlschlagen, obwohl der Build selbst korrekt ist.
Evtl. müssen die Prüfbedingungen angepasst oder einzelne Tests temporär deaktiviert werden.

## Projektspezifische Konventionen

- Parameterreihenfolge in nativen Calls wird über `priority` in `@NativeFunctionParam` und Pointer-Annotationen gesteuert.
- Pointer-/Mehrfachrückgaben werden über `ReturnTuple` plus konkrete Tuple-Datenklassen modelliert.
- Callback-Strukturen werden über Struct-Mapping und Callback-IDs aufgelöst (`FFMStructFactory` -> generiert `CallbackStructFactory`).

## Einordnung zur Bachelorarbeit

Dieses Repository dient als technische Validierung des theoretischen Ansatzes der Bachelorarbeit.  
Insbesondere `ffmlibrary` zeigt, wie sich deklarative Spezifikationen per KSP in nutzbare FFM-Bindings übersetzen lassen und wie sich dieser Ansatz im Vergleich zu einem etablierten Wrapper (`steamworks4j`) im Benchmark-Setup verhält.
