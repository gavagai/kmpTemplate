import SwiftUI
import shared

struct RegistrationDecomposeView: View {

    @StateObject
    private var component: Registration

    init(_ component: Registration) {
        self.component = component
    }

    var body: some View {
        Text("Registration")
        Button("Cancel") {
            component.cancelPressed()
        }
    }
}
