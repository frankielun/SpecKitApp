/**
 * TypeScript interface for WellnessSDK native module
 * Provides type-safe access to Health Connect/HealthKit via KMP SDK
 */

import { NativeModules } from 'react-native';

/**
 * Health metric data structure
 */
export interface HealthMetric {
  type: string;
  value: number;
  unit: string;
  timestamp: number;
  source: string;
}

/**
 * Error codes from native module
 */
export enum WellnessSDKError {
  PERMISSION_DENIED = 'PERMISSION_DENIED',
  DATA_NOT_AVAILABLE = 'DATA_NOT_AVAILABLE',
  UNSUPPORTED_PLATFORM = 'UNSUPPORTED_PLATFORM',
  UNKNOWN_ERROR = 'UNKNOWN_ERROR',
}

/**
 * Native module interface
 */
interface WellnessSDKNativeModule {
  requestPermissions(): Promise<boolean>;
  fetchStepCount(startDate: number, endDate: number): Promise<HealthMetric>;
}

const { WellnessSDK: NativeWellnessSDK } = NativeModules;

if (!NativeWellnessSDK) {
  throw new Error(
    'WellnessSDK native module is not available. ' +
    'Make sure the native module is properly linked.'
  );
}

/**
 * TypeScript wrapper for WellnessSDK native module
 */
class WellnessSDKService {
  private nativeModule: WellnessSDKNativeModule;

  constructor() {
    this.nativeModule = NativeWellnessSDK as WellnessSDKNativeModule;
  }

  /**
   * Request health data permissions from the user
   * @returns Promise resolving to true if permissions granted
   */
  async requestPermissions(): Promise<boolean> {
    try {
      return await this.nativeModule.requestPermissions();
    } catch (error: any) {
      throw this.mapError(error);
    }
  }

  /**
   * Fetch step count for today
   * @returns Promise resolving to HealthMetric with step count data
   */
  async fetchTodayStepCount(): Promise<HealthMetric> {
    const now = Date.now();
    const startOfDay = new Date();
    startOfDay.setHours(0, 0, 0, 0);
    
    return this.fetchStepCount(startOfDay.getTime(), now);
  }

  /**
   * Fetch step count for a specific time range
   * @param startDate Start timestamp in milliseconds
   * @param endDate End timestamp in milliseconds
   * @returns Promise resolving to HealthMetric with step count data
   */
  async fetchStepCount(startDate: number, endDate: number): Promise<HealthMetric> {
    try {
      if (startDate >= endDate) {
        throw new Error('Start date must be before end date');
      }
      
      return await this.nativeModule.fetchStepCount(startDate, endDate);
    } catch (error: any) {
      throw this.mapError(error);
    }
  }

  /**
   * Map native module errors to JavaScript errors with better messages
   */
  private mapError(error: any): Error {
    const code = error?.code || 'UNKNOWN_ERROR';
    const message = error?.message || 'An unknown error occurred';
    
    const mappedError = new Error(message);
    (mappedError as any).code = code;
    
    return mappedError;
  }
}

export default new WellnessSDKService();
