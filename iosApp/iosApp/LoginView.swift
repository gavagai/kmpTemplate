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
    
    enum LoginField {
        case username
        case password
        case passwordConfirmation
        case verificationCode
    }
    @FocusState private var focusedField: LoginField?

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
                    StandardUsernameTextField("Username", username: Binding(get: { viewModel.viewState.username.data }, set: viewModel.changeUsername))
                        .validated(errorMessage: viewModel.viewState.username.error)
                        .focused($focusedField, equals: .username)

                    if viewModel.viewState.emailVerificationRequired {
                        StandardPasswordTextField("Password", password: Binding(get: { viewModel.viewState.password.data }, set: viewModel.changePassword),
                                                preventNewPasswordContentType: true, trailingImage: nil)
                            .validated(errorMessage: viewModel.viewState.password.error)
                            .focused($focusedField, equals: .password)
                    }
                    else {
                        StandardPasswordTextField(
                            "Password", password: Binding(get: { viewModel.viewState.password.data }, set: viewModel.changePassword),
                            onAction: {
                                doLogin()
                            }
                        )
                        .validated(errorMessage: viewModel.viewState.password.error)
                        .focused($focusedField, equals: .password)
                    }

                    if viewModel.viewState.emailVerificationRequired {
                        StandardOneTimeCodeTextField("Verification code", code: Binding(get: { viewModel.viewState.verificationCode.data }, set: viewModel.changeVerificationCode),
                                                     trailingImage: { Image(systemName: "arrow.forward.circle") }
                        ) {
                            doLogin()
                        }
                        .validated(errorMessage: viewModel.viewState.verificationCode.error)
                        .focused($focusedField, equals: .verificationCode)
                    }
                }
                .padding(.horizontal, 30)
                .padding(.top, 10)
                .viewFocus(focusedField: focusedField) { previousFocusedField, newFocusedField in
                    switch (previousFocusedField) {
                    case .username: viewModel.focusChangeUsername(focused: false)
                    case .password: viewModel.focusChangePassword(focused: false)
                    case .verificationCode: viewModel.focusChangeVerificationCode(focused: false)

                    default: break
                    }
                    switch (newFocusedField) {
                    case .username: viewModel.focusChangeUsername(focused: true)
                    case .password: viewModel.focusChangePassword(focused: true)
                    case .verificationCode: viewModel.focusChangeVerificationCode(focused: true)
                    default: break
                    }
                }

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
            super.render(model: model)
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
