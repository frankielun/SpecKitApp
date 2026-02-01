import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_app/screens/health_dashboard_screen.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  group('HealthDashboardScreen', () {
    const channel = MethodChannel('com.speckit.wellness_sdk');

    tearDown(() {
      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMethodCallHandler(channel, null);
    });

    testWidgets('shows loading indicator initially', (WidgetTester tester) async {
      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMethodCallHandler(channel, (MethodCall methodCall) async {
        await Future.delayed(const Duration(seconds: 1));
        return null;
      });

      await tester.pumpWidget(
        const MaterialApp(
          home: HealthDashboardScreen(),
        ),
      );

      expect(find.byType(CircularProgressIndicator), findsOneWidget);
      expect(find.text('Loading health data...'), findsOneWidget);
    });

    testWidgets('shows permission required when denied', (WidgetTester tester) async {
      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMethodCallHandler(channel, (MethodCall methodCall) async {
        if (methodCall.method == 'fetchStepCount') {
          throw PlatformException(
            code: 'PERMISSION_DENIED',
            message: 'Permissions denied',
          );
        }
        return null;
      });

      await tester.pumpWidget(
        const MaterialApp(
          home: HealthDashboardScreen(),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Health Permissions Required'), findsOneWidget);
      expect(find.text('Grant Permissions'), findsOneWidget);
      expect(find.byIcon(Icons.health_and_safety), findsOneWidget);
    });

    testWidgets('displays step count data', (WidgetTester tester) async {
      final mockData = {
        'type': 'STEP_COUNT',
        'value': 7500.0,
        'unit': 'steps',
        'timestamp': DateTime.now().millisecondsSinceEpoch,
        'source': 'HealthKit',
      };

      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMethodCallHandler(channel, (MethodCall methodCall) async {
        if (methodCall.method == 'fetchStepCount') {
          return mockData;
        }
        return null;
      });

      await tester.pumpWidget(
        const MaterialApp(
          home: HealthDashboardScreen(),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('Today\'s Steps'), findsOneWidget);
      expect(find.text('7500'), findsOneWidget);
      expect(find.text('steps'), findsOneWidget);
    });

    testWidgets('shows error state on failure', (WidgetTester tester) async {
      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMethodCallHandler(channel, (MethodCall methodCall) async {
        if (methodCall.method == 'fetchStepCount') {
          throw PlatformException(
            code: 'DATA_NOT_AVAILABLE',
            message: 'No data available',
          );
        }
        return null;
      });

      await tester.pumpWidget(
        const MaterialApp(
          home: HealthDashboardScreen(),
        ),
      );

      await tester.pumpAndSettle();

      expect(find.text('No data available'), findsOneWidget);
    });

    testWidgets('refresh button triggers data reload', (WidgetTester tester) async {
      int callCount = 0;
      final mockData = {
        'type': 'STEP_COUNT',
        'value': 5000.0,
        'unit': 'steps',
        'timestamp': DateTime.now().millisecondsSinceEpoch,
        'source': 'HealthKit',
      };

      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMethodCallHandler(channel, (MethodCall methodCall) async {
        if (methodCall.method == 'fetchStepCount') {
          callCount++;
          return mockData;
        }
        return null;
      });

      await tester.pumpWidget(
        const MaterialApp(
          home: HealthDashboardScreen(),
        ),
      );

      await tester.pumpAndSettle();
      expect(callCount, 1);

      // Tap refresh button
      await tester.tap(find.byIcon(Icons.refresh));
      await tester.pumpAndSettle();

      expect(callCount, 2);
    });
  });
}
