# Contributing to Kotlin-LLVM

Thanks for considering contributing to Kotlin-LLVM. This guide explains how to contribute code, tests, and documentation.

## Project overview

See the main project overview in [`README.md`](README.md:1).

## Before you start

- Read the license: [`LICENSE`](LICENSE:1).
- Ensure you understand the project's goals and structure (see [`README.md`](README.md:1) and the [`src/`](src/:1) layout).

## Development setup

1. Fork the repository and clone your fork:

```bash
git clone https://github.com/your-username/LLVM.git
cd LLVM
```

2. Install prerequisites:

- JDK 21 or later
- Kotlin 2.2.0

3. Build and run tests locally:

```bash
./gradlew build
./gradlew test
```

## Coding guidelines

- Follow existing style in Kotlin sources under [`src/main/kotlin/`](src/main/kotlin/:1).
- Keep public APIs stable and add deprecation notices for breaking changes.
- Write clear, small commits with descriptive messages.
- Use the existing naming and builder patterns shown in examples such as [`src/main/kotlin/space/norb/llvm/examples/HelloWorldExample.kt`](src/main/kotlin/space/norb/llvm/examples/HelloWorldExample.kt:1).

## Running tests

- Add unit and integration tests under [`src/test/kotlin/`](src/test/kotlin/:1).
- E2E test resources are in [`src/test/resources/`](src/test/resources/:1).
- Run tests with:

```bash
./gradlew test
```

## Submitting changes

1. Create a feature branch from `main`.
2. Make changes and add tests.
3. Run the full test suite locally.
4. Push your branch and open a pull request.

### Pull request checklist

- [ ] Is the change limited in scope and well documented?
- [ ] Are there tests covering new behavior?
- [ ] Does the build pass: `./gradlew build`?
- [ ] Have code style and naming conventions been followed?

### Issues and feature requests

- Open issues on GitHub and include a clear description, steps to reproduce, and expected behavior.
- For design-level discussions, prefer opening an issue before a large implementation.

### Documentation

- Update docs in [`docs/`](docs/:1) for design or API changes.
- Add usage examples to [`src/main/kotlin/space/norb/llvm/examples`](src/main/kotlin/space/norb/llvm/examples:1) when relevant.

## Contact

- For questions, use GitHub Discussions or open an issue.

Thank you for helping improve Kotlin-LLVM!
