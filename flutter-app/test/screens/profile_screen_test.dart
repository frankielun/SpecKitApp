import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_app/screens/profile_screen.dart';

void main() {
  group('ProfileScreen', () {
    testWidgets('displays user profile information', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: ProfileScreen(),
        ),
      );

      expect(find.text('Demo User'), findsOneWidget);
      expect(find.text('user@example.com'), findsOneWidget);
      expect(find.byIcon(Icons.person), findsOneWidget);
    });

    testWidgets('displays profile menu items', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: ProfileScreen(),
        ),
      );

      expect(find.text('Notifications'), findsOneWidget);
      expect(find.text('Privacy & Security'), findsOneWidget);
      expect(find.text('Help & Support'), findsOneWidget);
      expect(find.text('About'), findsOneWidget);
    });

    testWidgets('has AppBar with title', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: ProfileScreen(),
        ),
      );

      expect(find.widgetWithText(AppBar, 'Profile'), findsOneWidget);
    });

    testWidgets('notifications switch is displayed', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: ProfileScreen(),
        ),
      );

      expect(find.byType(Switch), findsOneWidget);
    });
  });
}
