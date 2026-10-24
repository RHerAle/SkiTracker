# Contributing to SkiTracker

SkiTracker is the telemetry, activity classification, and calorie calculation
engine for ski tracking: GPS trajectory analysis, hysteresis-based activity
recognition (skiing, lift rides, stationary), and MET physiological calorie
expenditure calculation. It is published under AGPL-3.0-only. Bug reports,
questions and verification results are welcome as issues, in English or
Spanish.

## Before you send code

External contributions that constitute a copyrightable work are accepted only
under a signed Fiduciary Licence Agreement ([FLA.md](FLA.md)). In short:

- You assign your exploitation rights in the contribution to the project owner,
  exclusively and free of charge.
- The owner undertakes to keep your contribution available under a free software
  licence (today AGPL-3.0-only) and to credit you under the name or pseudonym you
  choose, in [AUTHORS](AUTHORS).
- The owner may also license the engine, your contribution included, under other
  terms, including commercial ones.
- You keep a full licence to use your own contribution for any purpose.
- The agreement is governed by Spanish law; the Spanish text is binding and an
  English translation is provided.

How to sign: open an issue or contact the project maintainer. You will receive
the agreement with the owner's identification filled in. Return Annex A signed
(electronic or scanned handwritten signature) from the e-mail address you state
in it. If you are employed and the contribution relates to your job, include
Annex B signed by your employer. One signature covers all your future pull
requests.

Pull requests that need the FLA are not merged without it.

## What does not need the FLA

Typo and formatting fixes, identifier renames, trivial changes of one or a few
lines dictated by their function, and factual data without creative selection.
These are not copyrightable works and are merged under the project licence.
When in doubt, the maintainer will ask for the signature.

## Rules for code

- Keep the telemetry calculation free of Android framework, UI, and network
  dependencies. Mathematical recognition and calorie modeling operate purely
  on in-memory GPS points.
- Every algorithm change comes with a unit test in `tests/ActivityRecognizerTest.kt`
  or `tests/CalorieCalculatorTest.kt` that would fail without it.
- No personal data, no coordinates of private places, in code, tests or history.
- British English in code and documentation; some comments or test messages may
  be in Spanish.
