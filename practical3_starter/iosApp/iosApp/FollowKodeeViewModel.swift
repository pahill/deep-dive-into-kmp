import Shared
import Combine

class FollowKodeeViewModel: ObservableObject {
    @Published var locations: [KodeeLocation] = []

    private let sdk: KodeeSDK

    init(sdk: KodeeSDK = KodeeSDK()) {
        self.sdk = sdk
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
