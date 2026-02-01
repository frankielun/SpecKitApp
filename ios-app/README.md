# SpecKit iOS Demo App

Native iOS application demonstrating Kotlin Multiplatform SDK integration with SwiftUI.

## Overview

This app showcases:
- **Tab 1 (Home)**: Native SwiftUI view with no KMP dependency
- **Tab 2 (Profile)**: Native SwiftUI view with no KMP dependency  
- **Tab 3 (Health Dashboard)**: Integrates KMP SDK to fetch step count from HealthKit

## Requirements

- iOS 15.0+
- Xcode 15.0+
- CocoaPods 1.12+
- KMP SDK (`../wellness-kmp-sdk`)

## Setup

### 1. Install Dependencies

```bash
cd ios-app
pod install
```

### 2. Open Workspace

```bash
open SpecKitIOS.xcworkspace
```

### 3. Configure Signing

- Open project in Xcode
- Select SpecKitIOS target
- Go to Signing & Capabilities
- Select your development team
- Ensure HealthKit capability is enabled

### 4. Build and Run

- Select a simulator or device (iOS 15.0+)
- Press Cmd+R to build and run

## Architecture

```
SpecKitIOS/
├── App/
│   └── SpecKitIOSApp.swift          # App entry point
├── Views/
│   ├── ContentView.swift            # 3-tab TabView
│   ├── HomeView.swift               # Tab 1 (Native)
│   ├── ProfileView.swift            # Tab 2 (Native)
│   └── HealthDashboardView.swift    # Tab 3 (KMP Integration)
├── ViewModels/
│   └── HealthDashboardViewModel.swift  # ObservableObject for Tab 3
├── Services/
│   └── HealthKitService.swift       # Swift wrapper for KMP SDK
└── Info.plist                       # HealthKit permissions
```

## KMP SDK Integration

The app integrates the KMP SDK via CocoaPods:

1. **Podfile** references `wellness-kmp-sdk/shared/shared.podspec`
2. **HealthKitService** wraps KMP SDK's `HealthDataRepository`
3. **HealthDashboardViewModel** consumes the service using Swift async/await
4. **HealthDashboardView** displays the data with SwiftUI

## Testing

Run unit tests:

```bash
# Command line
xcodebuild test -workspace SpecKitIOS.xcworkspace -scheme SpecKitIOS -destination 'platform=iOS Simulator,name=iPhone 15'

# Or in Xcode: Cmd+U
```

Tests are located in `SpecKitIOSTests/HealthDashboardViewModelTests.swift`

## Manual Testing

1. Launch app on simulator or device
2. Grant HealthKit permissions when prompted
3. Navigate to Health tab (Tab 3)
4. Verify step count displays (fetched via KMP SDK)
5. Compare with Health app data (should match ±1 step)

## Troubleshooting

### CocoaPods Issues

```bash
# Clean and reinstall
pod deintegrate
pod install
```

### Build Issues

```bash
# Clean build folder
xcodebuild clean -workspace SpecKitIOS.xcworkspace -scheme SpecKitIOS
```

### HealthKit Permissions

- Check Settings > Privacy & Security > Health > SpecKitIOS
- Ensure "Steps" read permission is enabled

## License

See repository root for license information.
