/**
 * Tests for HealthDashboardScreen component
 */

import React from 'react';
import { render, waitFor } from '@testing-library/react-native';
import HealthDashboardScreen from '../src/screens/HealthDashboardScreen';
import WellnessSDK from '../src/services/WellnessSDK';

// Mock WellnessSDK
jest.mock('../src/services/WellnessSDK', () => ({
  __esModule: true,
  default: {
    fetchTodayStepCount: jest.fn(),
    requestPermissions: jest.fn(),
  },
}));

describe('HealthDashboardScreen', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders loading state initially', () => {
    (WellnessSDK.fetchTodayStepCount as jest.Mock).mockImplementation(
      () => new Promise(() => {}) // Never resolves
    );

    const { getByText } = render(<HealthDashboardScreen />);
    expect(getByText('Loading health data...')).toBeTruthy();
  });

  it('displays step count when data is fetched successfully', async () => {
    (WellnessSDK.fetchTodayStepCount as jest.Mock).mockResolvedValue({
      type: 'steps',
      value: 5000,
      unit: 'count',
      timestamp: Date.now(),
      source: 'HealthKit',
    });

    const { getByText } = render(<HealthDashboardScreen />);

    await waitFor(() => {
      expect(getByText('5,000')).toBeTruthy();
      expect(getByText('steps today')).toBeTruthy();
    });
  });

  it('displays error message when fetch fails', async () => {
    (WellnessSDK.fetchTodayStepCount as jest.Mock).mockRejectedValue(
      new Error('Permission denied')
    );

    const { getByText } = render(<HealthDashboardScreen />);

    await waitFor(() => {
      expect(getByText('Permission denied')).toBeTruthy();
      expect(getByText('Request Permissions')).toBeTruthy();
      expect(getByText('Retry')).toBeTruthy();
    });
  });

  it('renders Health Dashboard title', async () => {
    (WellnessSDK.fetchTodayStepCount as jest.Mock).mockResolvedValue({
      type: 'steps',
      value: 3000,
      unit: 'count',
      timestamp: Date.now(),
      source: 'HealthKit',
    });

    const { getByText } = render(<HealthDashboardScreen />);

    await waitFor(() => {
      expect(getByText('Health Dashboard')).toBeTruthy();
    });
  });

  it('displays KMP SDK source attribution', async () => {
    (WellnessSDK.fetchTodayStepCount as jest.Mock).mockResolvedValue({
      type: 'steps',
      value: 8000,
      unit: 'count',
      timestamp: Date.now(),
      source: 'HealthKit',
    });

    const { getByText } = render(<HealthDashboardScreen />);

    await waitFor(() => {
      expect(getByText('Data from Health Connect/HealthKit via KMP SDK')).toBeTruthy();
    });
  });
});
