import Shared
import Combine

enum LocateState {
    case loading
    case result(KodeeLocation)
}

class LocateKodeeViewModel: ObservableObject {
    @Published var state: LocateState = .loading

    private let sdk: KodeeSDK
    init() {
        self.sdk = KodeeSDK()
    }

    @MainActor
    func locate() async {
        state = .loading

        do {
           let location = try await sdk.locateKodee()
           state = .result(location)
        } catch {
           print("Task was cancelled")
        }
    }
}
