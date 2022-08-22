import SwiftUI
import shared

struct ContentDecomposeView: View {
    @State
    private var componentHolder =
        ComponentHolder {
            RootComponent(
                componentContext: $0
            )
        }

    var body: some View {
        RootDecomposeView(componentHolder.component)
            .onAppear { LifecycleRegistryExtKt.resume(self.componentHolder.lifecycle) }
            .onDisappear { LifecycleRegistryExtKt.stop(self.componentHolder.lifecycle) }
    }
}

struct ContentDecomposeView_Previews: PreviewProvider {
    static var previews: some View {
        ContentDecomposeView()
    }
}
