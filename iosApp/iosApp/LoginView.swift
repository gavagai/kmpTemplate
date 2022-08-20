import SwiftUI
import shared
import StandardWidgets

struct LoginView: View {

    @AppStorage("EmailVerified")
    private var emailVerified = false

    @AppStorage("RecentUsername")
    private var recentUsername: String = ""

    @StateObject
    private var viewModel: ViewProxy = ViewProxy()

    @StateObject
    private var holder: ControllerHolder

    @State
    private var snackbarText: String? = nil

    init() {
        self._holder = StateObject(
            wrappedValue: ControllerHolder { lifecycle in
                LoginController(
                    lifecycle: lifecycle
                )
            }
        )
    }

    var body: some View {

        VStack {
//             ProductImageView()
//                 .padding(.top, 15)

            VStack {
                let usernameBinding = Binding<String>(
                    get: {
                        viewModel.viewState.username.data as String
                    },
                    set: {
                        viewModel.changeUsername(newVal: $0)
                    }
                )
                StandardUsernameTextField("Username", username: usernameBinding)
//                TextField("Username", text: usernameBinding)
//                    .autocapitalization(.none)
                let passwordBinding = Binding<String>(
                    get: {
                        viewModel.viewState.password.data as String
                    },
                    set: {
                        viewModel.changePassword(newVal: $0)
                    }
                )
                StandardPasswordTextField("Password", password: passwordBinding) {
                    viewModel.login { message in
                        snackbarText = message ?? "" //showSnackbar(snackbarMessage ?: "")
                    }
                }
//                TextField("Password", text: passwordBinding)
//                    .autocapitalization(.none)
                if viewModel.viewState.emailVerificationRequired {
                    let verificationCodeBinding = Binding<String>(
                        get: {
                            viewModel.viewState.verificationCode.data as String
                        },
                        set: {
                            viewModel.changeVerificationCode(newVal: $0)
                        }
                    )
                    StandardOneTimeCodeTextField("Verification code", code: verificationCodeBinding)
//                    TextField("Verification code", text: verificationCodeBinding)
//                        .keyboardType(.numberPad)
                }
            }
            .padding(.horizontal, 30)
            .padding(.top, 10)

            VStack {
                Button("Login") {
                    viewModel.login { message in
                        snackbarText = message ?? "" //showSnackbar(snackbarMessage ?: "")                        
                    }
                }
                .buttonStyle(StandardButtonStyle())
                Button("Get code") {
                    viewModel.getNewCode { message in
                        snackbarText = message ?? "" //showSnackbar(snackbarMessage ?: "")
                    }
                }
            }
            .padding(.horizontal, 30)
            .padding(.top, 25)

            Text(snackbarText ?? "Nothing to report here")
        }
        .onFirstAppear { holder.controller.onViewCreated(view: viewModel, viewLifecycle: holder.lifecycle) }
        .onAppear { LifecycleRegistryExtKt.resume(holder.lifecycle) }
        .onDisappear { LifecycleRegistryExtKt.stop(holder.lifecycle) }
    }
}

struct LoginView_Previews: PreviewProvider {
	static var previews: some View {
		LoginView()
	}
}

extension LoginView {

    private class ViewProxy: LoginBaseMviView, ObservableObject {

        @Published
        var viewState: LoginMviViewModel = LoginMviViewModel()

        override func render(model: LoginMviViewModel) {
            viewState = model
        }
    }

    private class ControllerHolder : ObservableObject {
        let lifecycle: LifecycleRegistry = LifecycleRegistryKt.LifecycleRegistry()
        let controller: LoginController

        init(factory: (Lifecycle) -> LoginController) {
            controller = factory(lifecycle)
        }

        deinit {
            LifecycleRegistryExtKt.destroy(lifecycle)
        }
    }
}
