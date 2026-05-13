import Shared
import SwiftUI

struct LocateKodeeView: View {
    @StateObject private var viewModel = LocateKodeeViewModel()

    var body: some View {
        Group {
            switch viewModel.state {
            case .loading:
                VStack(spacing: 16) {
                    ProgressView()
                        .scaleEffect(1.5)
                    Text("Searching for Kodee…")
                        .foregroundStyle(.secondary)
                }
            case .result(let location):
                ScrollView {
                    VStack(spacing: 20) {
                        Image(location.imageName)
                            .resizable()
                            .scaledToFit()
                            .frame(maxWidth: .infinity)
                            .cornerRadius(12)
                            .padding(.horizontal)

                        VStack(alignment: .leading, spacing: 8) {
                            Text(location.venueName)
                                .font(.title2)
                                .fontWeight(.semibold)
                            Text("\(location.city), \(location.country)")
                                .font(.subheadline)
                                .foregroundStyle(.secondary)
                            Text(Date(timeIntervalSince1970: Double(location.timestamp)).formatted(date: .long, time: .shortened))
                                .font(.caption)
                                .foregroundStyle(.tertiary)
                        }
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .padding(.horizontal)

                        Button("Locate again") {
                            viewModel.locate()
                        }
                        .buttonStyle(.borderedProminent)
                        .padding(.top, 8)
                    }
                    .padding(.vertical)
                }
            }
        }
        .navigationTitle("Locate Kodee")
        .navigationBarTitleDisplayMode(.inline)
    }
}
