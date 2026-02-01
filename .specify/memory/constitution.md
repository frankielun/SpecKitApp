<!--
Sync Impact Report:
- Version change: Initial → 1.0.0
- Principles established: 7 core principles
- Sections added: Core Principles, Platform Integration Requirements, Quality Standards, Development Workflow, Governance
- Templates status: ✅ All templates updated
- Follow-up TODOs: None
-->

# SpecKitApp Constitution

## Core Principles

### I. Cross-Platform Compatibility First (NON-NEGOTIABLE)

The KMP SDK MUST function identically across iOS and Android platforms. All features implemented in `commonMain` MUST have corresponding platform-specific implementations in `iosMain` (HealthKit) and `androidMain` (Health Connect). Integration MUST be validated across all four demo applications (Native iOS, Native Android, React Native, Flutter) before any feature is considered complete.

**Rationale**: This project's purpose is proving KMP viability for health data across diverse frameworks. Platform parity is the success criterion.

### II. Test-First Development (NON-NEGOTIABLE)

All code MUST achieve minimum 80% test coverage (85%+ for KMP SDK). Tests MUST be written before implementation following TDD principles: Red (failing test) → Green (minimal implementation) → Refactor. Unit tests are required for all business logic. Platform-specific code MUST use mocks/stubs to isolate from native APIs.

**Rationale**: Health data is sensitive and critical. Bugs are unacceptable. Comprehensive testing ensures reliability and facilitates refactoring.

### III. Clean Code & SOLID Principles

Code MUST be self-documenting with meaningful names. Apply SOLID principles: Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation, Dependency Inversion. Use dependency injection for all platform-specific implementations. Prefer composition over inheritance. Follow language-specific style guides (Kotlin Conventions, Swift API Guidelines, Airbnb/TypeScript, Effective Dart).

**Rationale**: Multiple frameworks and languages require consistent, maintainable patterns. Clean architecture enables team scalability.

### IV. expect/actual Pattern for Platform Abstraction

Platform-specific functionality (HealthKit, Health Connect) MUST use Kotlin's `expect/actual` mechanism. Maximize shared code in `commonMain`. Platform implementations in `iosMain`/`androidMain` MUST be thin wrappers around native APIs. Use sealed classes or `Result` types for error handling across platform boundaries.

**Rationale**: This pattern ensures type safety, testability, and clear separation between shared logic and platform APIs.

### V. Comprehensive Error Handling & Observability

All asynchronous operations MUST use proper error handling (try/catch, Result types). Errors MUST be logged with sufficient context for debugging. User-facing errors MUST provide actionable messages (e.g., "Enable HealthKit permissions in Settings"). Platform-specific errors MUST be translated to common error types in the KMP layer.

**Rationale**: Health APIs fail unpredictably (permissions, OS versions, data unavailability). Robust error handling prevents app crashes and improves user experience.

### VI. Code Quality Enforcement via Linting & Formatting

All code MUST pass automated linting before commits. Use pre-commit hooks to enforce: ktlint/Detekt (Kotlin), SwiftLint (Swift), ESLint/Prettier (TypeScript), dart analyze (Dart). CI/CD pipelines MUST fail builds on lint violations or test coverage drops. Auto-formatting tools SHOULD be run before all commits.

**Rationale**: Consistent code style reduces cognitive load and merge conflicts across multiple languages and contributors.

### VII. Documentation as Code

All public APIs MUST have KDoc/Javadoc (Kotlin), DocC comments (Swift), TSDoc (TypeScript), or DartDoc (Dart). Each module MUST have a README with: Overview, Prerequisites, Setup, Usage, Testing, and Architecture sections. Complex logic or platform-specific implementations MUST include inline comments explaining the "why," not the "what."

**Rationale**: Multi-platform projects require clear documentation for developers working across different tech stacks. Knowledge transfer depends on thorough docs.

## Platform Integration Requirements

### Health API Integration

- iOS apps MUST use HealthKit APIs with proper authorization flows
- Android apps MUST use Health Connect APIs (androidx.health.connect)
- Permission requests MUST include clear user-facing explanations
- Data fetching MUST be asynchronous (coroutines, async/await)
- Failed permission requests MUST provide actionable guidance to users

### Demo App Architecture

Each demo app MUST implement a 3-tab bottom navigation structure:
1. **Tab 1**: Native Home (platform-native code only, no KMP)
2. **Tab 2**: Native Profile/Settings (platform-native code only, no KMP)
3. **Tab 3**: Health Dashboard (integrates `wellness-kmp-sdk`)

This structure validates that the KMP SDK integrates seamlessly alongside native code.

### Version Compatibility

- iOS: Minimum deployment target iOS 15.0+
- Android: minSdk 26 (Android 8.0), targetSdk 34+
- KMP SDK MUST support these minimum versions
- Test on both simulator/emulator AND physical devices

## Quality Standards

### Test Coverage Thresholds

- **KMP SDK (shared/)**: 85% minimum coverage (enforced by CI/CD)
- **Native iOS app**: 80% minimum coverage
- **Native Android app**: 80% minimum coverage
- **React Native app**: 80% minimum coverage
- **Flutter app**: 80% minimum coverage

Coverage reports MUST be generated on every CI build. PRs that decrease coverage below thresholds MUST be rejected.

### Testing Frameworks

- **Kotlin**: kotlin-test, MockK, kotlinx-coroutines-test
- **Swift**: XCTest
- **TypeScript**: Jest, React Native Testing Library
- **Dart**: flutter_test, Mockito

### Performance Requirements

- Health data fetching MUST complete within 5 seconds under normal network conditions
- UI MUST remain responsive during data loading (use loading indicators)
- Large datasets MUST be fetched on background threads (not main/UI thread)
- Cache frequently accessed data with appropriate expiration policies

## Development Workflow

### Branch Strategy

- `main`: Production-ready code only
- `develop`: Integration branch for ongoing work
- `feature/*`: New features (e.g., `feature/heart-rate-tracking`)
- `bugfix/*`: Bug fixes
- `hotfix/*`: Critical production fixes

### Commit Conventions

Follow [Conventional Commits](https://www.conventionalcommits.org/):
- `feat(scope)`: New features
- `fix(scope)`: Bug fixes
- `test(scope)`: Test additions/updates
- `docs(scope)`: Documentation changes
- `chore(scope)`: Build/tooling updates
- `refactor(scope)`: Code restructuring without behavior changes

Examples:
```
feat(kmp-sdk): add heart rate data fetching
fix(ios-app): resolve HealthKit authorization crash
test(android-app): add unit tests for HealthRepository
docs(readme): update Flutter integration instructions
```

### Code Review Requirements

- All PRs MUST be reviewed by at least one other developer
- PRs MUST pass all CI checks (linting, tests, coverage)
- Reviewers MUST verify adherence to this constitution
- Platform-specific changes MUST be reviewed by someone familiar with that platform

### Pre-commit Hooks

Set up Git hooks to run before every commit:
- ktlint/Detekt for Kotlin files
- SwiftLint for Swift files
- ESLint for TypeScript files
- dart analyze for Dart files

Commits MUST NOT bypass these checks unless exceptional circumstances are documented.

## Governance

This constitution supersedes all other development practices and conventions. All pull requests, code reviews, and design decisions MUST comply with these principles.

### Amendment Process

1. Propose amendment via GitHub Issue with rationale
2. Discuss impact on existing code and workflows
3. Require approval from at least 2 maintainers
4. Update this document with new version number (semantic versioning)
5. Communicate changes to all contributors
6. Create migration plan if existing code needs updates

### Version Semantics

- **MAJOR**: Backward-incompatible principle changes (e.g., removing a core principle)
- **MINOR**: New principles added or existing ones materially expanded
- **PATCH**: Clarifications, typo fixes, non-semantic refinements

### Compliance Enforcement

- CI/CD pipelines MUST enforce test coverage thresholds
- Code reviews MUST verify principle adherence
- Regular audits SHOULD check for technical debt or violations
- Violations MUST be addressed immediately or documented as intentional technical debt with justification and remediation plan

### Reference Documentation

For detailed implementation guidance, refer to `.github/copilot/default.instructions.md`, which contains language-specific conventions, tool configurations, and architectural patterns.

**Version**: 1.0.0 | **Ratified**: 2026-02-01 | **Last Amended**: 2026-02-01
