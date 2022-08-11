import SwiftUI
import shared

@main
struct iOSApp: App {
    init() {
        BridgeKt.startKoin()
    }

	var body: some Scene {
		WindowGroup {
			AccountView(registrationContext: RegistrationContext(email: nil, givenName: nil, familyName: nil))
		}
	}
}