import SwiftUI
import shared

struct RegistrationDecomposeView: View {

    @ObservedObject
    private var childStack: ObservableValue<ChildStack<AnyObject, RegistrationChild>>

    init(_ component: Registration) {
        self.childStack = ObservableValue(component.childStack)
    }


    var body: some View {
        let child = self.childStack.value.active.instance

        switch child {
        case let account as RegistrationChild.Account:
            AccountDecomposeView(account.component)
                .transition(
                    .asymmetric(
                        insertion: AnyTransition.move(edge: .trailing),
                        removal: AnyTransition.move(edge: .trailing)
                    )
                )

        default: EmptyView()
        }
    }
}
