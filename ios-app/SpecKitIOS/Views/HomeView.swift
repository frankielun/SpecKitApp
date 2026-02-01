import SwiftUI

/// T088: Home tab - Native iOS implementation (no KMP dependency)
struct HomeView: View {
    var body: some View {
        NavigationView {
            VStack(spacing: 20) {
                Image(systemName: "house.fill")
                    .font(.system(size: 80))
                    .foregroundColor(.blue)
                
                Text("Welcome to SpecKit")
                    .font(.title)
                    .fontWeight(.bold)
                
                Text("This is a demo app showcasing Kotlin Multiplatform SDK integration")
                    .font(.body)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal, 40)
                    .foregroundColor(.secondary)
                
                Spacer()
            }
            .padding()
            .navigationTitle("Home")
        }
    }
}

#Preview {
    HomeView()
}
