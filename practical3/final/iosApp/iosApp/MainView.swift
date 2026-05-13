import SwiftUI

struct MainView: View {
    @Binding var path: NavigationPath

    var body: some View {
        VStack(spacing: 24) {
            Text("Where is Kodee?")
                .font(.largeTitle)
                .fontWeight(.bold)

            Button("Locate Kodee") {
                path.append(AppRoute.locateKodee)
            }
            .buttonStyle(.borderedProminent)

            Button("Follow Kodee") {
                path.append(AppRoute.followKodee)
            }
            .buttonStyle(.bordered)
        }
        .navigationTitle("Where is Kodee")
        .navigationBarTitleDisplayMode(.inline)
    }
}
