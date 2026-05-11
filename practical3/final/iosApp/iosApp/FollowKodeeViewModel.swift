import Shared
import Foundation

class FollowKodeeViewModel: ObservableObject {
    @Published var locations: [KodeeLocation] = []

    private let sdk: KodeeSDK

    init() {
        self.sdk = KodeeSDK()
    }

    @MainActor
    func startObserving() async {
            do {
                let sequence = sdk.locationHistory().asAsyncSequence()
                for try await location in sequence {
                    self.locations.insert(location, at: 0)
                }
            } catch {
                print("Flow was cancelled")
            }
    }
}
