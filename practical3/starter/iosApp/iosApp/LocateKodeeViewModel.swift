import Shared

enum LocateState {
    case loading
    case result(KodeeLocation)
}

class LocateKodeeViewModel: ObservableObject {
    @Published var state: LocateState = .loading

    private let sdk: KodeeSDK
    init() {
        self.sdk = KodeeSDK()
        locate()
    }

    func locate() {
        state = .loading

        sdk.locateKodeeWrapper().subscribe(
            onSuccess: { [weak self] result in
                if let location = result as? KodeeLocation {
                    self?.state = .result(location)
                }
            },
            onError: { _ in }
        )
    }
}
