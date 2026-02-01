# SpecKitRN - React Native Demo App

React Native demonstration app integrating the KMP Health SDK via native modules.

## Overview

This app demonstrates cross-platform health data access using:
- **React Native 0.73** with TypeScript
- **Native iOS module** (Swift) bridging to KMP SDK
- **Native Android module** (Kotlin) bridging to KMP SDK
- **React Navigation** for bottom tab navigation
- **Jest** for unit testing

## Architecture

```
JavaScript Layer (React Native + TypeScript)
    ↓ (Native Modules API)
iOS Bridge (Swift) ← → Android Bridge (Kotlin)
    ↓                          ↓
    KMP SDK (Kotlin Multiplatform)
    ↓                          ↓
HealthKit (iOS)          Health Connect (Android)
```

## Project Structure

```
react-native-app/
├── src/
│   ├── screens/          # React components for each tab
│   │   ├── HomeScreen.tsx
│   │   ├── ProfileScreen.tsx
│   │   └── HealthDashboardScreen.tsx
│   ├── services/         # TypeScript SDK wrapper
│   │   └── WellnessSDK.ts
│   ├── hooks/            # Custom React hooks
│   │   └── useHealthData.ts
│   └── App.tsx           # Main app with navigation
├── ios/                  # iOS native module
│   └── SpecKitRN/
│       ├── WellnessSDKBridge.swift
│       └── WellnessSDKBridge.m
├── android/              # Android native module
│   └── app/src/main/java/com/speckit/rn/
│       ├── WellnessSDKModule.kt
│       └── WellnessSDKPackage.kt
└── __tests__/            # Jest unit tests
```

## Setup

### Prerequisites

- Node.js 18+
- React Native development environment
- iOS: Xcode 14+, CocoaPods
- Android: Android Studio, JDK 17

### Installation

```bash
# Install dependencies
npm install

# iOS: Install CocoaPods dependencies
cd ios && pod install && cd ..
```

## Running the App

### iOS
```bash
npm run ios
```

### Android
```bash
npm run android
```

## Testing

```bash
# Run Jest tests
npm test

# Run with coverage
npm test -- --coverage

# Run lint
npm run lint
```

## Features

- **Tab 1 (Home)**: Native React Native UI
- **Tab 2 (Profile)**: Native React Native UI
- **Tab 3 (Health Dashboard)**: Integrates KMP SDK
  - Displays today's step count
  - Handles permissions requests
  - Error handling with retry
  - Loading states

## Native Module API

### WellnessSDK Service

```typescript
// Request permissions
await WellnessSDK.requestPermissions(): Promise<boolean>

// Fetch today's step count
await WellnessSDK.fetchTodayStepCount(): Promise<HealthMetric>

// Fetch step count for date range
await WellnessSDK.fetchStepCount(startDate, endDate): Promise<HealthMetric>
```

## Development

- **TypeScript**: Full type safety with strict mode
- **ESLint**: Code quality checks
- **Prettier**: Code formatting
- **Jest**: Unit testing framework

## Notes

- iOS requires HealthKit capability and Info.plist permissions
- Android requires Health Connect installed and permissions granted
- Native modules bridge React Native to KMP SDK seamlessly
- All health data access is read-only via KMP SDK
