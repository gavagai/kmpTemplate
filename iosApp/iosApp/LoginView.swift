import SwiftUI
import shared
import StandardWidgets

struct LoginView: View {

    @Environment(\.colorScheme) var colorScheme

    @AppStorage("EmailVerified")
    private var emailVerified = false

    @AppStorage("RecentUsername")
    private var recentUsername: String = ""

    @StateObject
    private var viewModel: ViewProxy = ViewProxy()

    @StateObject
    private var holder: ControllerHolder
    
    @State
    private var snackbarShowing = false
    @State
    private var snackbarText = ""
    
    private let onSignup: () -> Void
    private let onLogin: () -> Void
    
    init(onSignup: @escaping () -> Void, onLogin: @escaping () -> Void) {
        self._holder = StateObject(
            wrappedValue: ControllerHolder { lifecycle in
                LoginController(
                    lifecycle: lifecycle
                )
            }
        )
        self.onSignup = onSignup
        self.onLogin = onLogin
    }

    var body: some View {
        
        ZStack {
            VStack {
                 ProductImageView()
                     .padding(.top, 15)

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
                        .validated(errorMessage: viewModel.viewState.username.error)
                    
                    let passwordBinding = Binding<String>(
                        get: {
                            viewModel.viewState.password.data as String
                        },
                        set: {
                            viewModel.changePassword(newVal: $0)
                        }
                    )
                    if viewModel.viewState.emailVerificationRequired {
                        StandardPasswordTextField("Password", password: passwordBinding, preventNewPasswordContentType: true, trailingImage: nil)
                            .validated(errorMessage: viewModel.viewState.password.error)
                    }
                    else {
                        StandardPasswordTextField(
                            "Password", password: passwordBinding,
                            onAction: {
                                doLogin()
                            }
                        )
                        .validated(errorMessage: viewModel.viewState.password.error)
                    }

                    if viewModel.viewState.emailVerificationRequired {
                        let verificationCodeBinding = Binding<String>(
                            get: {
                                viewModel.viewState.verificationCode.data as String
                            },
                            set: {
                                viewModel.changeVerificationCode(newVal: $0)
                            }
                        )
                        StandardOneTimeCodeTextField("Verification code", code: verificationCodeBinding,
                                                     trailingImage: { Image(systemName: "arrow.forward.circle") }
                        ) {
                            doLogin()
                        }
                        .validated(errorMessage: viewModel.viewState.verificationCode.error)
                    }
                }
                .padding(.horizontal, 30)
                .padding(.top, 10)
                
                if !emailVerified {
                    VStack {
                        Text("Need a new verification code?")
                             .padding(.top, 30)
                        Button("Get code") {
                            viewModel.getNewCode { message in
                                if message != nil {
                                    showSnackbar(message ?? "", seconds: 3)
                                }
                            }
                        }
                        .buttonStyle(StandardButtonStyle())
                            .frame(width: 150)
                    }
                    .padding(.horizontal, 30)
                    .padding(.top, 10)
                }

                VStack {
                    Text("Don't have an account?")
                        .padding(.top, 30)
                    Button("Sign up") {
                        onSignup()
                    }
                    .buttonStyle(StandardButtonStyle(foregroundColor: .white, backgroundColor: .red))
                        .frame(width: 150)
                }
                .padding(.horizontal, 30)
                .padding(.top, 30)
            }
            .onFirstAppear { holder.controller.onViewCreated(view: viewModel, viewLifecycle: holder.lifecycle) }
            .onAppear { LifecycleRegistryExtKt.resume(holder.lifecycle) }
            .onDisappear { LifecycleRegistryExtKt.stop(holder.lifecycle) }
            
            VStack {
                Spacer()
                if snackbarShowing {
                    Text(snackbarText)
                        .lineLimit(3)
                        .frame(maxWidth: .infinity)
                        .padding(10)
                        .foregroundColor(snackbarForeground())
                        .background(snackbarBackground())
                        .cornerRadius(10)
                        .padding(20)
                }
            }
            .frame(maxHeight: .infinity)
        }
    }

    private func doLogin() {
        viewModel.login { message in
            if message != nil {
                showSnackbar(message ?? "", seconds: 3)
            }
            else {
                onLogin()
            }
        }
    }
    
    private func showSnackbar(_ text: String, seconds: Double = 3) {
        snackbarText = text
        snackbarShowing = true
        DispatchQueue.main.asyncAfter(deadline: .now() + seconds) {
            snackbarShowing = false
        }
    }

    private func snackbarForeground() -> Color { colorScheme == .dark ? .black : .white }
    private func snackbarBackground() -> Color { colorScheme == .dark ? .white : .black }

}

struct LoginView_Previews: PreviewProvider {
	static var previews: some View {
        LoginView(onSignup: {}, onLogin: {})
	}
}

extension LoginView {

    private class ViewProxy: LoginBaseMviView, ObservableObject {

        @Published
        var viewState: LoginMviViewModel = LoginMviViewModel()

        override func render(model: LoginMviViewModel) {
            viewState = model
        }

        override func onSuccessfulLogin() {
            UserDefaults.standard.set(viewState.username.data, forKey: "RecentUsername")
            UserDefaults.standard.set(true, forKey: "EmailVerified")
        }

        override func onSignup() {
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
