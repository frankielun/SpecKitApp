/**
 * HomeScreen - Native React Native screen without KMP SDK dependency
 */

import React from 'react';
import { View, Text, StyleSheet } from 'react-native';

const HomeScreen = (): JSX.Element => {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>Welcome to SpecKit</Text>
      <Text style={styles.subtitle}>React Native Home Screen</Text>
      <Text style={styles.description}>
        This tab demonstrates native React Native UI without KMP SDK integration.
      </Text>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 20,
    backgroundColor: '#fff',
  },
  title: {
    fontSize: 24,
    fontWeight: 'bold',
    marginBottom: 10,
    color: '#333',
  },
  subtitle: {
    fontSize: 18,
    color: '#666',
    marginBottom: 20,
  },
  description: {
    fontSize: 14,
    color: '#999',
    textAlign: 'center',
  },
});

export default HomeScreen;
