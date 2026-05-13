import Shared
import SwiftUI

struct FollowKodeeView: View {
    @StateObject private var viewModel = FollowKodeeViewModel()

    var body: some View {
        List(viewModel.locations, id: \.timestamp) { location in
            VStack(alignment: .leading, spacing: 4) {
                Text(location.venueName)
                    .font(.headline)
                Text("\(location.city), \(location.country)")
                    .font(.subheadline)
                    .foregroundStyle(.secondary)
                Text(Date(timeIntervalSince1970: Double(location.timestamp)).formatted(date: .long, time: .shortened))
                    .font(.caption)
                    .foregroundStyle(.tertiary)
            }
            .padding(.vertical, 4)
        }
        .navigationTitle("Follow Kodee")
        .navigationBarTitleDisplayMode(.inline)
    }
}
