import SwiftUI
import shared

struct RootDecomposeView: View {
    @ObservedObject
    private var routerStates: ObservableValue<RouterState<AnyObject, RootChild>>

    init(_ component: Root) {
        self.routerStates = ObservableValue(component.routerState)
    }

    var body: some View {
        let child = self.routerStates.value.activeChild.instance

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
                .animation(.easeInOut)

        case let home as RootChild.Home:
            HomeDecomposeView(home.component)

        default: EmptyView()
        }
    }
}
