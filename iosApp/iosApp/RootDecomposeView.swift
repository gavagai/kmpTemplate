import SwiftUI
import shared

struct RootDecomposeView: View {
    @ObservedObject
    private var childStack: ObservableValue<ChildStack<AnyObject, RootChild>>

    init(_ component: Root) {
        self.childStack = ObservableValue(component.childStack)
    }

    var body: some View {
        let child = self.childStack.value.active.instance

        switch child {
        case let main as RootChild.Login:
            LoginDecomposeView(main.component)

        case let registration as RootChild.Registration:
            RegistrationDecomposeView(registration.component)
                .transition(
                    .asymmetric(
                        insertion: AnyTransition.move(edge: .trailing),
                        removal: AnyTransition.move(edge: .trailing)
                    )
                )

        case let home as RootChild.Home:
            HomeDecomposeView(home.component)

        default: EmptyView()
        }
    }
}
