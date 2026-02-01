import 'package:flutter_test/flutter_test.dart';
import 'package:flutter/services.dart';
import 'package:flutter_app/models/health_metric.dart';
import 'package:flutter_app/services/wellness_sdk_service.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  group('WellnessSDKService', () {
    late WellnessSDKService service;
    const channel = MethodChannel('com.speckit.wellness_sdk');

    setUp(() {
      service = WellnessSDKService();
    });

    tearDown(() {
      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMethodCallHandler(channel, null);
    });

    test('requestPermissions returns true when granted', () async {
      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMethodCallHandler(channel, (MethodCall methodCall) async {
        if (methodCall.method == 'requestPermissions') {
          return true;
        }
        return null;
      });

      final result = await service.requestPermissions();
      expect(result, true);
    });

    test('requestPermissions returns false when denied', () async {
      TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
          .setMockMethodCallHandler(channel, (MethodCall methodCall) async {
        if (methodCall.method == 'requestPermissions') {
          throw PlatformException(code: 'PERMISSION_DENIED');
        }
        return null;
      });

      final result = await service.requestPermissions();
      expect(result, false);
    });

    test('fetchTodayStepCount returns HealthMetric', () async {
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
          return mockData;
        }
        return null;
      });

      final result = await service.fetchTodayStepCount();
      expect(result.type, 'STEP_COUNT');
      expect(result.value, 5000.0);
      expect(result.unit, 'steps');
      expect(result.source, 'HealthKit');
    });

    test('fetchStepCount throws PermissionDeniedException', () async {
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

      expect(
        () => service.fetchTodayStepCount(),
        throwsA(isA<PermissionDeniedException>()),
      );
    });

    test('fetchStepCount throws DataNotAvailableException', () async {
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

      expect(
        () => service.fetchTodayStepCount(),
        throwsA(isA<DataNotAvailableException>()),
      );
    });
  });

  group('HealthMetric', () {
    test('fromJson creates HealthMetric correctly', () {
      final json = {
        'type': 'STEP_COUNT',
        'value': 8000.0,
        'unit': 'steps',
        'timestamp': 1234567890000,
        'source': 'HealthConnect',
      };

      final metric = HealthMetric.fromJson(json);
      expect(metric.type, 'STEP_COUNT');
      expect(metric.value, 8000.0);
      expect(metric.unit, 'steps');
      expect(metric.timestamp.millisecondsSinceEpoch, 1234567890000);
      expect(metric.source, 'HealthConnect');
    });

    test('toJson converts HealthMetric correctly', () {
      final metric = HealthMetric(
        type: 'STEP_COUNT',
        value: 6000.0,
        unit: 'steps',
        timestamp: DateTime.fromMillisecondsSinceEpoch(1234567890000),
        source: 'HealthKit',
      );

      final json = metric.toJson();
      expect(json['type'], 'STEP_COUNT');
      expect(json['value'], 6000.0);
      expect(json['unit'], 'steps');
      expect(json['timestamp'], 1234567890000);
      expect(json['source'], 'HealthKit');
    });
  });
}
