# DSA & Algorithms — From-Scratch Java

A personal repository of data structures and algorithms implemented from scratch in Java, for interview and research-engineering preparation. Every structure is written by hand (no `java.util` collections backing them), documented in prose notes, and validated by a self-contained test runner. The work is paced and deliberate: the goal is durable understanding, not just passing tests.

## What's here

Two Java packages plus prose notes and a build harness:

- **`Data_structures/`** — generic structures, each in its own file with a `<Name>_Main` test runner.
- **`Algorithms/`** — algorithms that build on those structures (currently suffix-array based), same per-file test-runner pattern.
- **`Data_structures_notes.md`** — combined notes for every structure (Positives / Negatives / Algorithm thought-process).
- **`Algorithms_notes.md`** — combined notes for the algorithms (Idea / Complexity / Notes).
- **`Makefile`** — root build/run harness for both packages.

## Conventions

Every class follows the same rules so the whole codebase reads consistently:

- **Generic backing** via `Object[]` with `@SuppressWarnings("unchecked")` casts on read (Java erasure rules out `new E[n]`). Numeric-only structures (e.g. `Fenwick_tree`) use a primitive backing like `long[]` instead.
- **Contract-only comments** above each method — what it does, what it returns, what it throws — with no implementation hints, so the method body is the exercise.
- **Self-contained test runner**: each file declares a `<Name>_Main` class with `checkEquals`, `checkTrue`, and `checkThrows` helpers that print PASS/FAIL inline, tally results, print a summary, and `System.exit(1)` on any failure. No `-ea` needed; tests cover edge cases (empty, single element, bounds, null-safety) and, where useful, cross-check against a brute-force reference.
- **Null-safety** with `java.util.Objects.equals` for value comparisons.
- Data-structure-specific touches, e.g. doubly linked lists carry a `toStringReverse` that walks `prev` pointers to verify back-pointer maintenance.

## Build & run

Requires a JDK (8+; developed and tested on JDK 21). From the repo root:

```sh
make run F=Stack              # compile everything, run Data_structures.Stack_Main, then clean
make run F=Unique_substrings  # run Algorithms.Unique_substrings_Main
make run-all                  # run every *_Main, log to test-results.log, print only failures
make clean                    # delete all .class files
```

`make run` picks the package automatically based on which folder contains `<F>.java`, then auto-cleans the generated `.class` files. `make run-all` writes full output to `test-results.log` and prints only the classes that failed.

## Notes format

Each structure's notes follow a three-part prose template — **Positives**, **Negatives**, **Algorithm / thought process** (reasoning and edge cases, no code) — collected into `Data_structures_notes.md` with a table of contents. Algorithms use a parallel template — **Idea**, **Complexity**, **Notes** — in `Algorithms_notes.md`.

## How Claude is used in this project

Claude (Anthropic) is used as a coding tutor and scaffolding tool, not as a code generator that does the work:

- **Skeletons.** Claude produces the class scaffold — fields, contract-only method stubs, and a complete `<Name>_Main` test runner (known-answer cases plus brute-force cross-checks) — matching the conventions above. The method bodies are left empty for me to implement.
- **Socratic debugging.** When I share buggy code, Claude points at the bug and walks the failing trace, edge cases, and complexity trade-offs rather than handing over a fix. It only writes the fix when I explicitly ask.
- **Notes.** Claude converts my handwritten notes into the Positives / Negatives / Algorithm prose format, flagging and correcting any errors it finds rather than copying them verbatim.

The intent is that the learning happens while filling in the skeletons and chasing down the test failures; the scaffolding just removes boilerplate and keeps the structure uniform.

## Study resources & curriculum

### Coding / DSA spine
- **NeetCode 150** — https://neetcode.io/practice
- **Grokking the Coding Interview** (pattern-based prep) — https://www.designgurus.io/course/grokking-the-coding-interview (also on Educative: https://www.educative.io/courses/grokking-coding-interview)
- **William Fiset — Data Structures** (YouTube) — https://www.youtube.com/playlist?list=PLDV1Zeh2NRsB6SWUrDFW2RmDotAfPbeHu
- **William Fiset — Graph Theory** (YouTube) — https://www.youtube.com/playlist?list=PLDV1Zeh2NRsDGO4--qE8yH72HFL1Km93P
- **William Fiset — Algorithms** (companion Java repo) — https://github.com/williamfiset/Algorithms
- **Abdul Bari — Algorithms** (YouTube) — https://www.youtube.com/playlist?list=PLAPEtbmG9XgTQqVYWAgAR6MilRB93OeMQ

<!-- ### ML / math track
- **Introduction to Statistical Learning (ISL)** — https://www.statlearning.com
- **Harvard Stat 110 — Probability** (Joe Blitzstein) — https://stat110.hsites.harvard.edu/ · lectures: https://www.youtube.com/playlist?list=PL2SOU6wwxB0uwwH80KTQ6ht66KWxbzTIo · free book: https://probabilitybook.net
- **Andrej Karpathy — Neural Networks: Zero to Hero** — https://karpathy.ai/zero-to-hero.html · playlist: https://www.youtube.com/playlist?list=PLAqhIrjkxbuWI23v9cThsA9GvCAUhRvKZ
- **Aurélien Géron — Hands-On Machine Learning** (ch. 10–16; code repo) — https://github.com/ageron/handson-ml3
- **Chip Huyen — Designing Machine Learning Systems** — https://huyenchip.com/books/ · **Stanford CS329S** — https://stanford-cs329s.github.io/
- **Khang Pham — Machine Learning Interviews** (late-stage drilling) — https://mlengineer.io/ -->



---

*Environment: macOS, zsh. Links above were checked against their sources; the ML-track entries should still be re-verified periodically as course pages move.*