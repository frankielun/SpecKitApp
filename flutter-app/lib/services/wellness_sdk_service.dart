import 'package:flutter/services.dart';
import '../models/health_metric.dart';

/// Service class to interact with the native WellnessSDK via platform channels
class WellnessSDKService {
  static const MethodChannel _channel = MethodChannel('com.speckit.wellness_sdk');

  /// Request permissions to access health data
  /// Returns true if permissions are granted, false otherwise
  /// Throws PlatformException on errors
  Future<bool> requestPermissions() async {
    try {
      final bool result = await _channel.invokeMethod('requestPermissions');
      return result;
    } on PlatformException catch (e) {
      if (e.code == 'PERMISSION_DENIED') {
        return false;
      }
      rethrow;
    }
  }

  /// Fetch step count data for today
  /// Returns HealthMetric with step count data
  /// Throws PlatformException on errors
  Future<HealthMetric> fetchTodayStepCount() async {
    final now = DateTime.now();
    final startOfDay = DateTime(now.year, now.month, now.day);
    final endOfDay = DateTime(now.year, now.month, now.day, 23, 59, 59);
    
    return fetchStepCount(startOfDay, endOfDay);
  }

  /// Fetch step count data for a specific date range
  /// Returns HealthMetric with step count data
  /// Throws PlatformException on errors
  Future<HealthMetric> fetchStepCount(DateTime startDate, DateTime endDate) async {
    try {
      final Map<dynamic, dynamic> result = await _channel.invokeMethod(
        'fetchStepCount',
        {
          'startDate': startDate.millisecondsSinceEpoch,
          'endDate': endDate.millisecondsSinceEpoch,
        },
      );
      
      return HealthMetric.fromJson(result);
    } on PlatformException catch (e) {
      switch (e.code) {
        case 'PERMISSION_DENIED':
          throw PermissionDeniedException(
            e.message ?? 'Health permissions were denied',
          );
        case 'DATA_NOT_AVAILABLE':
          throw DataNotAvailableException(
            e.message ?? 'No health data available',
          );
        case 'INVALID_ARGUMENTS':
          throw InvalidArgumentsException(
            e.message ?? 'Invalid arguments provided',
          );
        default:
          throw WellnessSDKException(
            e.message ?? 'Unknown error occurred',
            e.code,
          );
      }
    }
  }
}

/// Base exception class for WellnessSDK errors
class WellnessSDKException implements Exception {
  final String message;
  final String? code;

  WellnessSDKException(this.message, [this.code]);

  @override
  String toString() => 'WellnessSDKException: $message${code != null ? ' (code: $code)' : ''}';
}

/// Exception thrown when health permissions are denied
class PermissionDeniedException extends WellnessSDKException {
  PermissionDeniedException(String message) : super(message, 'PERMISSION_DENIED');
}

/// Exception thrown when health data is not available
class DataNotAvailableException extends WellnessSDKException {
  DataNotAvailableException(String message) : super(message, 'DATA_NOT_AVAILABLE');
}

/// Exception thrown when invalid arguments are provided
class InvalidArgumentsException extends WellnessSDKException {
  InvalidArgumentsException(String message) : super(message, 'INVALID_ARGUMENTS');
}
