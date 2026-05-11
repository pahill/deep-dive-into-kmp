import SwiftUI

@main
struct iOSApp: App {
    @State private var path = NavigationPath()

    var body: some Scene {
        WindowGroup {
            NavigationStack(path: $path) {
                MainView(path: $path)
                    .navigationDestination(for: AppRoute.self) { route in
                        switch route {
                        case .locateKodee: LocateKodeeView()
                        case .followKodee: FollowKodeeView()
                        }
                    }
            }
        }
    }
}
