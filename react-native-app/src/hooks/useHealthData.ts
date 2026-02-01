/**
 * Custom React hook for fetching health data via WellnessSDK
 */

import { useState, useEffect } from 'react';
import WellnessSDK, { HealthMetric, WellnessSDKError } from '../services/WellnessSDK';

interface UseHealthDataResult {
  stepCount: number | null;
  loading: boolean;
  error: string | null;
  refetch: () => void;
  requestPermissions: () => Promise<void>;
}

export const useHealthData = (): UseHealthDataResult => {
  const [stepCount, setStepCount] = useState<number | null>(null);
  const [loading, setLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);

  const fetchData = async () => {
    setLoading(true);
    setError(null);
    
    try {
      const metric: HealthMetric = await WellnessSDK.fetchTodayStepCount();
      setStepCount(Math.round(metric.value));
    } catch (err: any) {
      const errorMessage = err?.message || 'Failed to fetch step count';
      setError(errorMessage);
      setStepCount(null);
    } finally {
      setLoading(false);
    }
  };

  const requestPermissions = async () => {
    setLoading(true);
    setError(null);
    
    try {
      const granted = await WellnessSDK.requestPermissions();
      if (granted) {
        // Permissions granted, fetch data
        await fetchData();
      } else {
        setError('Permissions not granted');
      }
    } catch (err: any) {
      const errorMessage = err?.message || 'Failed to request permissions';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  return {
    stepCount,
    loading,
    error,
    refetch: fetchData,
    requestPermissions,
  };
};
