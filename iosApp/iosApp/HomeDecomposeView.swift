import SwiftUI
import shared

struct HomeDecomposeView: View {

    @StateObject
    private var component: Home

    init(_ component: Home) {
        self.component = component
    }

    var body: some View {
        Text("Home")
        Button("Back") {
            component.backPressed()
        }
    }
}
