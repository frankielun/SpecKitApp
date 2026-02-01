import SwiftUI

/// T089: Profile tab - Native iOS implementation (no KMP dependency)
struct ProfileView: View {
    var body: some View {
        NavigationView {
            List {
                Section(header: Text("User Information")) {
                    HStack {
                        Text("Name")
                        Spacer()
                        Text("Demo User")
                            .foregroundColor(.secondary)
                    }
                    
                    HStack {
                        Text("Version")
                        Spacer()
                        Text("1.0.0")
                            .foregroundColor(.secondary)
                    }
                }
                
                Section(header: Text("About")) {
                    Text("This profile tab demonstrates native iOS code without any KMP SDK integration.")
                        .font(.footnote)
                        .foregroundColor(.secondary)
                }
                
                Section(header: Text("SDK Information")) {
                    HStack {
                        Text("KMP SDK")
                        Spacer()
                        Text("v1.0.0")
                            .foregroundColor(.secondary)
                    }
                    
                    HStack {
                        Text("Platform")
                        Spacer()
                        Text("iOS")
                            .foregroundColor(.secondary)
                    }
                }
            }
            .navigationTitle("Profile")
        }
    }
}

#Preview {
    ProfileView()
}
