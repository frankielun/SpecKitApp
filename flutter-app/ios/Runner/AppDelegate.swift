import UIKit
import Flutter
import shared

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
    private let channelName = "com.speckit.wellness_sdk"
    private var healthRepository: HealthDataRepository?
    
    override func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        // Initialize KMP SDK
        let provider = HealthDataProviderKt.createHealthDataProvider()
        self.healthRepository = HealthDataRepository(provider: provider)
        
        // Setup Flutter method channel
        let controller : FlutterViewController = window?.rootViewController as! FlutterViewController
        let channel = FlutterMethodChannel(name: channelName,
                                          binaryMessenger: controller.binaryMessenger)
        
        channel.setMethodCallHandler { [weak self] (call: FlutterMethodCall, result: @escaping FlutterResult) in
            guard let self = self else { return }
            
            switch call.method {
            case "requestPermissions":
                self.requestPermissions(result: result)
            case "fetchStepCount":
                self.fetchStepCount(call: call, result: result)
            default:
                result(FlutterMethodNotImplemented)
            }
        }
        
        GeneratedPluginRegistrant.register(with: self)
        return super.application(application, didFinishLaunchingWithOptions: launchOptions)
    }
    
    private func requestPermissions(result: @escaping FlutterResult) {
        Task {
            guard let repository = self.healthRepository else {
                result(FlutterError(code: "INITIALIZATION_ERROR",
                                  message: "Health repository not initialized",
                                  details: nil))
                return
            }
            
            let healthResult = await repository.requestPermissions()
            
            switch healthResult {
            case let successResult as HealthResultSuccess<KotlinBoolean>:
                result(successResult.data.boolValue)
                
            case is HealthResultPermissionDenied:
                result(FlutterError(code: "PERMISSION_DENIED",
                                  message: "Health data permissions were denied",
                                  details: nil))
                
            case is HealthResultDataNotAvailable:
                result(FlutterError(code: "DATA_NOT_AVAILABLE",
                                  message: "HealthKit is not available",
                                  details: nil))
                
            case let errorResult as HealthResultUnknownError:
                result(FlutterError(code: "UNKNOWN_ERROR",
                                  message: errorResult.message,
                                  details: nil))
                
            default:
                result(FlutterError(code: "UNKNOWN_ERROR",
                                  message: "An unknown error occurred",
                                  details: nil))
            }
        }
    }
    
    private func fetchStepCount(call: FlutterMethodCall, result: @escaping FlutterResult) {
        guard let args = call.arguments as? [String: Any],
              let startDate = args["startDate"] as? Double,
              let endDate = args["endDate"] as? Double else {
            result(FlutterError(code: "INVALID_ARGUMENTS",
                              message: "Missing or invalid arguments",
                              details: nil))
            return
        }
        
        Task {
            guard let repository = self.healthRepository else {
                result(FlutterError(code: "INITIALIZATION_ERROR",
                                  message: "Health repository not initialized",
                                  details: nil))
                return
            }
            
            let startTimestamp = Int64(startDate)
            let endTimestamp = Int64(endDate)
            
            let healthResult = await repository.getStepCount(
                startTimestamp: startTimestamp,
                endTimestamp: endTimestamp
            )
            
            switch healthResult {
            case let successResult as HealthResultSuccess<HealthMetric>:
                let metric = successResult.data
                let response: [String: Any] = [
                    "type": metric.type,
                    "value": metric.value,
                    "unit": metric.unit,
                    "timestamp": metric.timestamp,
                    "source": metric.source
                ]
                result(response)
                
            case is HealthResultPermissionDenied:
                result(FlutterError(code: "PERMISSION_DENIED",
                                  message: "Health data permissions were denied. Please grant permissions in Settings.",
                                  details: nil))
                
            case is HealthResultDataNotAvailable:
                result(FlutterError(code: "DATA_NOT_AVAILABLE",
                                  message: "No step count data available for the specified time range",
                                  details: nil))
                
            case let errorResult as HealthResultUnknownError:
                result(FlutterError(code: "UNKNOWN_ERROR",
                                  message: errorResult.message,
                                  details: nil))
                
            default:
                result(FlutterError(code: "UNKNOWN_ERROR",
                                  message: "An unknown error occurred while fetching step count",
                                  details: nil))
            }
        }
    }
}
