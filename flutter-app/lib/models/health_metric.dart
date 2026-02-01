/// Health metric model representing data from the KMP SDK
class HealthMetric {
  final String type;
  final double value;
  final String unit;
  final DateTime timestamp;
  final String source;

  HealthMetric({
    required this.type,
    required this.value,
    required this.unit,
    required this.timestamp,
    required this.source,
  });

  /// Create HealthMetric from JSON map returned by platform channel
  factory HealthMetric.fromJson(Map<dynamic, dynamic> json) {
    return HealthMetric(
      type: json['type'] as String,
      value: (json['value'] as num).toDouble(),
      unit: json['unit'] as String,
      timestamp: DateTime.fromMillisecondsSinceEpoch(
        (json['timestamp'] as num).toInt(),
      ),
      source: json['source'] as String,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'type': type,
      'value': value,
      'unit': unit,
      'timestamp': timestamp.millisecondsSinceEpoch,
      'source': source,
    };
  }

  @override
  String toString() {
    return 'HealthMetric(type: $type, value: $value, unit: $unit, timestamp: $timestamp, source: $source)';
  }
}
