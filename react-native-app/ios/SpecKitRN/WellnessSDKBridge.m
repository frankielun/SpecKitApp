//
//  WellnessSDKBridge.m
//  SpecKitRN
//
//  Objective-C bridge header to expose Swift module to React Native
//

#import <React/RCTBridgeModule.h>

@interface RCT_EXTERN_MODULE(WellnessSDKBridge, NSObject)

RCT_EXTERN_METHOD(requestPermissions:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(fetchStepCount:(double)startDate
                  endDate:(double)endDate
                  resolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)

+ (BOOL)requiresMainQueueSetup
{
  return NO;
}

@end
