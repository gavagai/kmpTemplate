import SwiftUI
import shared

struct LoginDecomposeView: View {

    @StateObject
    private var component: Login

    init(_ component: Login) {
        self.component = component
    }

    var body: some View {
        Text("Home")
        Button("Logout") {
            component.logoutPressed()
        }
    }
}
