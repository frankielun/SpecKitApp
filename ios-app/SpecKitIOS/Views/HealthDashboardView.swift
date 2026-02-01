import SwiftUI

/// T090: Health Dashboard view integrating KMP SDK
/// T106-T111: UI implementation with loading states and error handling
struct HealthDashboardView: View {
    @StateObject private var viewModel = HealthDashboardViewModel()
    
    var body: some View {
        NavigationView {
            VStack(spacing: 24) {
                // T107: Show loading indicator
                if viewModel.isLoading {
                    ProgressView("Fetching health data...")
                        .progressViewStyle(CircularProgressViewStyle())
                        .scaleEffect(1.5)
                        .padding()
                }
                
                // T108: Show error message if present
                else if let errorMessage = viewModel.errorMessage {
                    VStack(spacing: 16) {
                        Image(systemName: "exclamationmark.triangle.fill")
                            .font(.system(size: 60))
                            .foregroundColor(.orange)
                        
                        Text(errorMessage)
                            .font(.body)
                            .multilineTextAlignment(.center)
                            .foregroundColor(.secondary)
                            .padding(.horizontal)
                        
                        // T110: Button to request permissions if denied
                        if errorMessage.contains("permission") {
                            Button(action: {
                                Task {
                                    await viewModel.requestPermissions()
                                }
                            }) {
                                Label("Grant Permissions", systemImage: "lock.open.fill")
                                    .padding()
                                    .background(Color.blue)
                                    .foregroundColor(.white)
                                    .cornerRadius(10)
                            }
                        }
                        
                        Button(action: {
                            Task {
                                await viewModel.fetchStepCount()
                            }
                        }) {
                            Label("Retry", systemImage: "arrow.clockwise")
                                .padding()
                                .background(Color.gray.opacity(0.2))
                                .cornerRadius(10)
                        }
                    }
                    .padding()
                }
                
                // T109: Show step count if available
                else if let stepCount = viewModel.stepCount {
                    ScrollView {
                        VStack(spacing: 24) {
                            // Step Count Card
                            VStack(spacing: 20) {
                                Image(systemName: "figure.walk")
                                    .font(.system(size: 60))
                                    .foregroundColor(.green)
                                
                                VStack(spacing: 8) {
                                    Text("\(stepCount)")
                                        .font(.system(size: 48, weight: .bold, design: .rounded))
                                        .foregroundColor(.primary)
                                    
                                    Text("steps today")
                                        .font(.title3)
                                        .foregroundColor(.secondary)
                                
                                Text("steps today")
                                        .font(.title3)
                                        .foregroundColor(.secondary)
                        Divider()
                            .padding(.horizontal, 20)
                        
                        VStack(alignment: .leading, spacing: 12) {
                            HStack {
                                Image(systemName: "checkmark.circle.fill")
                                    .foregroundColor(.green)
                                Text("Data fetched via KMP SDK")
                                    .font(.footnote)
                            }
                            
                            HStack {
                                Image(systemName: "cpu")
                                    .foregroundColor(.blue)
                                Text("Platform: iOS (HealthKit)")
                                    .font(.footnote)
                            }
                            
                            HStack {
                                Image(systemName: "calendar")
                                    .foregroundColor(.orange)
                                Text("Period: Today")
                                    .font(.footnote)
                            }
                        }
                        .padding()
                        .background(Color.gray.opacity(0.1))
                        .cornerRadius(12)
                        
                        Button(action: {
                            Task {
                                await viewModel.fetchAllHealthData()
                            }
                        }) {
                            Label("Refresh All Data
                        VStack(alignment: .leading, spacing: 12) {
                            HStack {
                                Image(systemName: "checkmark.circle.fill")
                                    .foregroundColor(.green)
                                Text("Data fetched via KMP SDK")
                                    .font(.footnote)
                            }
                            
                            HStack {
                                Image(systemName: "cpu")
                                    .foregroundColor(.blue)
                                Text("Platform: iOS (HealthKit)")
                                    .font(.footnote)
                            }
                            
                            HStack {
                                Image(systemName: "calendar")
                                    .foregroundColor(.orange)
                                Text("Period: Last 7 days")
                                    .font(.footnote)
                            }
                        }
                        .padding()
                        .background(Color.gray.opacity(0.1))
                        .cornerRadius(12)
                        
                        Button(action: {
                            Task {
                                await viewModel.fetchStepCount()
                            }
                        }) {
                            Label("Refresh", systemImage: "arrow.clockwise")
                                .padding()
                                .frame(maxWidth: .infinity)
                                .background(Color.blue)
                                .foregroundColor(.white)
                                .cornerRadius(10)
                        }
                        .padding(.horizontal)
                    }
                }
                
                // Initial state
                else {
                    VStack(spacing: 16) {
                        Image(systemName: "heart.text.square.fill")
                            .font(.system(size: 80))
                            .foregroundColor(.red)
                        
                        Text("Ready to fetch health data")
                            .font(.title2)
                            .fontWeight(.semibold)
                        
                        Text("Tap the button below to fetch your step count using the KMP SDK")
                            .font(.body)
                            .multilineTextAlignment(.center)
                            .foregroundColor(.secondary)
                            .padding(.horizontal, 40)
                        
                        Button(action: {AllHealthData()
                            }
                        }) {
                            Label("Fetch Health Data", systemImage: "arrow.down.circle.fill")
                                .padding()
                                .frame(maxWidth: .infinity)
                                .background(Color.blue)
                                .foregroundColor(.white)
                                .cornerRadius(10)
                        }
                        .padding(.horizontal)
                    }
                }
                
                Spacer()
            }
            .padding()
            .navigationTitle("Health Dashboard")
            // T111: Fetch data when view appears
            .task {
                await viewModel.fetchAllHealthDataars
            .task {
                await viewModel.fetchStepCount()
            }
        }
    }
}

#Preview {
    HealthDashboardView()
}
