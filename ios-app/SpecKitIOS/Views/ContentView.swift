import SwiftUI

/// T087: Root view with 3-tab navigation structure
struct ContentView: View {
    var body: some View {
        TabView {
            // Tab 1: Home (Native only, no KMP)
            HomeView()
                .tabItem {
                    Label("Home", systemImage: "house.fill")
                }
            
            // Tab 2: Profile (Native only, no KMP)
            ProfileView()
                .tabItem {
                    Label("Profile", systemImage: "person.fill")
                }
            
            // Tab 3: Health Dashboard (Integrates KMP SDK)
            HealthDashboardView()
                .tabItem {
                    Label("Health", systemImage: "heart.fill")
                }
        }
    }
}

#Preview {
    ContentView()
}
