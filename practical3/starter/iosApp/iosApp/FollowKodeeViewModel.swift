import Shared

class FollowKodeeViewModel: ObservableObject {
    @Published var locations: [KodeeLocation] = []

    private let sdk: KodeeSDK

    init(sdk: KodeeSDK = KodeeSDK()) {
        self.sdk = sdk
        startObserving()
    }

    func startObserving() {
        sdk.locationHistoryWrapper().subscribe(
            onEach: { [weak self] location in
                if let location = location as? KodeeLocation {
                    self?.locations.insert(location, at: 0)
                }
            },
            onComplete: { },
            onError: { _ in }
        )
    }
}
