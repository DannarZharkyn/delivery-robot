# Architecture and restoration notes

## Recovered data flow

The React website writes root Firebase keys `flat`, `flong`, `code`, and `email`. Compass listens to these keys and writes `/direction` with `goal`, `heading`, and `status`. ESP8266 reads that object and streams the three numbers as comma-delimited serial values. The Mega attempts to parse them and control the robot.

## Problems visible in the saved source

These findings come from static inspection. No physical robot, Android device, or original Firebase deployment was available for validation.

- Mega: `сonst byte pin_rows` begins with a Cyrillic character. `sonarR` and `sonarL` are used but the declared sensors are `sonaron` and `sonarsol`. `turn_right()` / `turn_left()` are called while the defined functions are `go_right()` / `go_left()`. The closing brace structure places function definitions inside `loop()`.
- Serial: ESP8266 transmits at 9600 on software serial pins 4/5 with trailing commas and no newline. The Mega reads `Serial` at 9600 using a newline delimiter, while also initializing a separate software serial interface at 115200 on pins 0/1. Wiring and framing need verification. The receiver does not bound-check the three-value array.
- Mega uses recursive `loop()` calls and blocking delays. Its three fixed unlock codes do not implement the Firebase `code` value.
- Compass uses the deprecated orientation sensor and AsyncTask. Location subscription is removed in `onPause()` but not restored in `onResume()`. It does not implement a location permission result callback.
- Compass rounds GPS coordinates to four decimal places and tests exact equality for arrival. This is a rough prototype approach, not robust waypoint navigation.
- Compass writes return destination keys `flat:` / `flong:` with colons, whereas the listeners and website use `flat` / `flong`.
- The website initializes Firebase from two modules using the same default app and mixes compatibility and modular APIs. Initialization should be consolidated and verified during restoration.
- Authentication protects the React route, but deployed database rules are absent. Client routing alone does not establish database authorization.
- SMTP credentials originally lived in the Android client. For a rebuilt system, move email sending to a backend and provision new service credentials.

## Suggested restoration order

1. Restore the website against a new Firebase test project and restricted Maps key.
2. Build Compass, test permission handling and sensor output, and verify its Firebase payloads.
3. Repair Mega compile errors and define one serial baud rate, wiring plan, and record delimiter.
4. Bench-test motor, obstacle, and lock behavior separately before integrating navigation.
5. Integrate delivery code handling, arrival tolerance, and return destination behavior.

## Archive changes

Folder names were organized for browsing. Source behavior was preserved apart from replacing embedded service configuration and credentials with placeholders. No firmware logic repairs or dependency upgrades were made. The thesis XML had matching recovered service values redacted; visual pagination has not been verified. The presentation and MOV are preserved as release assets. Original files in the Desktop diploma folder were not modified.
