import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_app/screens/home_screen.dart';

void main() {
  group('HomeScreen', () {
    testWidgets('displays welcome message', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: HomeScreen(),
        ),
      );

      expect(find.text('Welcome to SpecKit Wellness'), findsOneWidget);
      expect(find.byIcon(Icons.home), findsOneWidget);
    });

    testWidgets('displays app description', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: HomeScreen(),
        ),
      );

      expect(
        find.text('Track your health metrics with our Kotlin Multiplatform SDK'),
        findsOneWidget,
      );
    });

    testWidgets('has AppBar with title', (WidgetTester tester) async {
      await tester.pumpWidget(
        const MaterialApp(
          home: HomeScreen(),
        ),
      );

      expect(find.widgetWithText(AppBar, 'Home'), findsOneWidget);
    });
  });
}
