/**
 * HealthDashboardScreen - Integrates KMP SDK via WellnessSDK native module
 */

import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  ActivityIndicator,
  TouchableOpacity,
} from 'react-native';
import { useHealthData } from '../hooks/useHealthData';

const HealthDashboardScreen = (): JSX.Element => {
  const { stepCount, loading, error, refetch, requestPermissions } = useHealthData();

  if (loading) {
    return (
      <View style={styles.container}>
        <ActivityIndicator size="large" color="#6200ee" />
        <Text style={styles.loadingText}>Loading health data...</Text>
      </View>
    );
  }

  if (error) {
    return (
      <View style={styles.container}>
        <Text style={styles.errorText}>{error}</Text>
        <TouchableOpacity
          style={styles.button}
          onPress={requestPermissions}
        >
          <Text style={styles.buttonText}>Request Permissions</Text>
        </TouchableOpacity>
        <TouchableOpacity
          style={[styles.button, styles.retryButton]}
          onPress={refetch}
        >
          <Text style={styles.buttonText}>Retry</Text>
        </TouchableOpacity>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Health Dashboard</Text>
      {stepCount !== null ? (
        <View style={styles.dataContainer}>
          <Text style={styles.label}>Step Count</Text>
          <Text style={styles.value}>{stepCount.toLocaleString()}</Text>
          <Text style={styles.unit}>steps today</Text>
          <Text style={styles.source}>
            Data from Health Connect/HealthKit via KMP SDK
          </Text>
        </View>
      ) : (
        <Text style={styles.noData}>No data available</Text>
      )}
      <TouchableOpacity
        style={[styles.button, styles.refreshButton]}
        onPress={refetch}
      >
        <Text style={styles.buttonText}>Refresh</Text>
      </TouchableOpacity>
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
    marginBottom: 30,
    color: '#333',
  },
  dataContainer: {
    alignItems: 'center',
    marginBottom: 30,
  },
  label: {
    fontSize: 18,
    color: '#666',
    marginBottom: 10,
  },
  value: {
    fontSize: 48,
    fontWeight: 'bold',
    color: '#6200ee',
    marginBottom: 5,
  },
  unit: {
    fontSize: 14,
    color: '#999',
    marginBottom: 20,
  },
  source: {
    fontSize: 12,
    color: '#aaa',
    fontStyle: 'italic',
    textAlign: 'center',
  },
  loadingText: {
    marginTop: 10,
    fontSize: 16,
    color: '#666',
  },
  errorText: {
    fontSize: 16,
    color: '#d32f2f',
    textAlign: 'center',
    marginBottom: 20,
  },
  noData: {
    fontSize: 16,
    color: '#999',
    marginBottom: 20,
  },
  button: {
    backgroundColor: '#6200ee',
    paddingHorizontal: 30,
    paddingVertical: 12,
    borderRadius: 8,
    marginTop: 10,
    minWidth: 200,
    alignItems: 'center',
  },
  retryButton: {
    backgroundColor: '#03dac6',
  },
  refreshButton: {
    backgroundColor: '#018786',
  },
  buttonText: {
    color: '#fff',
    fontSize: 16,
    fontWeight: 'bold',
  },
});

export default HealthDashboardScreen;
