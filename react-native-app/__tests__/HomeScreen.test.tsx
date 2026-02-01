/**
 * Tests for HomeScreen component
 */

import React from 'react';
import { render } from '@testing-library/react-native';
import HomeScreen from '../src/screens/HomeScreen';

describe('HomeScreen', () => {
  it('renders welcome message', () => {
    const { getByText } = render(<HomeScreen />);
    expect(getByText('Welcome to SpecKit')).toBeTruthy();
  });

  it('renders subtitle', () => {
    const { getByText } = render(<HomeScreen />);
    expect(getByText('React Native Home Screen')).toBeTruthy();
  });

  it('renders description text', () => {
    const { getByText } = render(<HomeScreen />);
    expect(
      getByText('This tab demonstrates native React Native UI without KMP SDK integration.')
    ).toBeTruthy();
  });
});
