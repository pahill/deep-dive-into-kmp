import Shared
import Foundation

enum LocateState {
    case loading
    case result(KodeeLocation)
    case error
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
            state = .error;
        }
    }
}
