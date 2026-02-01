//
//  WellnessSDKBridge.swift
//  SpecKitRN
//
//  Created by SpecKit on 2026-02-01.
//  iOS native module bridging KMP SDK to React Native
//

import Foundation
import shared

@objc(WellnessSDKBridge)
class WellnessSDKBridge: NSObject {
    
    private let healthRepository: HealthDataRepository
    
    override init() {
        // Initialize KMP SDK repository
        let provider = HealthDataProviderKt.createHealthDataProvider()
        self.healthRepository = HealthDataRepository(provider: provider)
        super.init()
    }
    
    @objc
    static func requiresMainQueueSetup() -> Bool {
        return false
    }
    
    @objc
    func requestPermissions(
        _ resolve: @escaping RCTPromiseResolveBlock,
        rejecter reject: @escaping RCTPromiseRejectBlock
    ) {
        Task {
            let result = await healthRepository.requestPermissions()
            
            switch result {
            case let successResult as HealthResultSuccess<KotlinBoolean>:
                resolve(successResult.data.boolValue)
                
            case is HealthResultPermissionDenied:
                reject("PERMISSION_DENIED", "Health data permissions were denied", nil)
                
            case is HealthResultDataNotAvailable:
                reject("DATA_NOT_AVAILABLE", "HealthKit is not available", nil)
                
            case let errorResult as HealthResultUnknownError:
                reject("UNKNOWN_ERROR", errorResult.message, nil)
                
            default:
                reject("UNKNOWN_ERROR", "An unknown error occurred", nil)
            }
        }
    }
    
    @objc
    func fetchStepCount(
        _ startDate: Double,
        endDate: Double,
        resolver resolve: @escaping RCTPromiseResolveBlock,
        rejecter reject: @escaping RCTPromiseRejectBlock
    ) {
        Task {
            // Convert JavaScript timestamp (milliseconds) to KMP SDK Long format
            let startTimestamp = Int64(startDate)
            let endTimestamp = Int64(endDate)
            
            let result = await healthRepository.getStepCount(
                startTimestamp: startTimestamp,
                endTimestamp: endTimestamp
            )
            
            switch result {
            case let successResult as HealthResultSuccess<HealthMetric>:
                let metric = successResult.data
                let response: [String: Any] = [
                    "type": metric.type,
                    "value": metric.value,
                    "unit": metric.unit,
                    "timestamp": metric.timestamp,
                    "source": metric.source
                ]
                resolve(response)
                
            case is HealthResultPermissionDenied:
                reject("PERMISSION_DENIED", "Health data permissions were denied. Please grant permissions in Settings.", nil)
                
            case is HealthResultDataNotAvailable:
                reject("DATA_NOT_AVAILABLE", "No step count data available for the specified time range", nil)
                
            case let errorResult as HealthResultUnknownError:
                reject("UNKNOWN_ERROR", errorResult.message, nil)
                
            default:
                reject("UNKNOWN_ERROR", "An unknown error occurred while fetching step count", nil)
            }
        }
    }
}
