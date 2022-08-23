import SwiftUI
import shared

struct HomeDecomposeView: View {

    private var component: Home

    init(_ component: Home) {
        self.component = component
    }

    var body: some View {
        Text("Home")
        Button("Logout") {
            component.logoutPressed()
        }
    }
}
