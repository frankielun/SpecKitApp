/**
 * Tests for ProfileScreen component
 */

import React from 'react';
import { render } from '@testing-library/react-native';
import ProfileScreen from '../src/screens/ProfileScreen';

describe('ProfileScreen', () => {
  it('renders profile title', () => {
    const { getByText } = render(<ProfileScreen />);
    expect(getByText('User Profile')).toBeTruthy();
  });

  it('renders subtitle', () => {
    const { getByText } = render(<ProfileScreen />);
    expect(getByText('React Native Profile Screen')).toBeTruthy();
  });

  it('renders description text', () => {
    const { getByText } = render(<ProfileScreen />);
    expect(
      getByText('This tab demonstrates native React Native UI without KMP SDK integration.')
    ).toBeTruthy();
  });
});
