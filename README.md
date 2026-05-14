# Elemental Quest
SFU, Spring 2026, Group 12.

## Authors

Bhav, Kanika, Kannan, Xavier

## Description

Players control Ember (fire) and Aqua (water), collect crystals, avoid hazards, and reach gates. Controls and rules are in the main menu **How to Play**.

## Requirements

- JDK **17**
- **Maven** 3.6+

## Build

From the project root (where `pom.xml` is):

```bash
mvn clean compile
```

## Run

`mvn exec:java` **does not compile**. After `mvn clean`, you must compile before `exec:java` or you will get `ClassNotFoundException: game.core.GameManager`.

**Recommended (one command — compiles then runs):**

```bash
mvn clean compile exec:java
```

**Or two steps** (same result):

```bash
mvn clean compile
mvn exec:java
```

The main class is set in `pom.xml`; you do not need `-Dexec.mainClass=...` (on PowerShell, that flag is easy to break because of the `.` in `exec.mainClass`).

Or package and run the JAR:

```bash
mvn clean package
java -jar target/elemental-quest-1.0.0.jar
```

Optional: add `audio/menu.mp3`, `audio/battle.mp3`, `audio/attack.mp3` next to the JAR or project root.

## Test

Tests are **JUnit 5** and run with **Maven**:

| Location | Purpose |
|----------|---------|
| `src/test/java` | Unit and integration tests |
| `src/test/resources` | Test-only files (if any) |

Run all tests:

```bash
mvn clean test
```

Run one class:

```bash
mvn test -Dtest=YourTestClass
```

**Coverage:** JaCoCo runs with the tests. Open `target/site/jacoco/index.html` for line and branch coverage. Details are in `report.md`.

## Phase 3 deliverables

- Implementation: `src/main/java`
- Tests: `src/test/java` (and `src/test/resources` if used)
- Written report: **`Documents/Phase3Report.pdf`**
- This **README**: build, run, and test instructions
