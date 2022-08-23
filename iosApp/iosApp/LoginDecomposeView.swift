import SwiftUI
import StandardWidgets
import shared

struct LoginDecomposeView: View {

    @Environment(\.colorScheme) var colorScheme

    @State
    private var snackbarShowing = false
    @State
    private var snackbarText = ""
    
    enum LoginField {
        case username
        case password
        case passwordConfirmation
        case verificationCode
    }
    @FocusState private var focusedField: LoginField?

    private var component: Login
    @ObservedObject
    private var model: ObservableValue<LoginModel>
    
    init(_ component: Login) {
        self.component = component
        
        self.model = ObservableValue(component.model)

    }

    var body: some View {
        let model = self.model.value
        
        ZStack {
            VStack {
                 ProductImageView()
                     .padding(.top, 15)

                VStack {
                    StandardUsernameTextField("Username", username: Binding(get: { model.username.data }, set: component.changeUsername))
                        .validated(errorMessage: model.username.error)
                        .focused($focusedField, equals: .username)

                    if model.emailVerificationRequired {
                        StandardPasswordTextField("Password", password: Binding(get: { model.password.data }, set: component.changePassword),
                                                preventNewPasswordContentType: true, trailingImage: nil)
                            .validated(errorMessage: model.password.error)
                            .focused($focusedField, equals: .password)
                    }
                    else {
                        StandardPasswordTextField(
                            "Password", password: Binding(get: { model.password.data }, set: component.changePassword),
                            onAction: {
                                doLogin()
                            }
                        )
                        .validated(errorMessage: model.password.error)
                        .focused($focusedField, equals: .password)
                    }

                    if model.emailVerificationRequired {
                        StandardOneTimeCodeTextField("Verification code", code: Binding(get: { model.verificationCode.data }, set: component.changeVerificationCode),
                                                     trailingImage: { Image(systemName: "arrow.forward.circle") }
                        ) {
                            doLogin()
                        }
                        .validated(errorMessage: model.verificationCode.error)
                        .focused($focusedField, equals: .verificationCode)
                    }
                }
                .padding(.horizontal, 30)
                .padding(.top, 10)
                .viewFocus(focusedField: focusedField) { previousFocusedField, newFocusedField in
                    switch (previousFocusedField) {
                    case .username: component.focusChangeUsername(focused: false)
                    case .password: component.focusChangePassword(focused: false)
                    case .verificationCode: component.focusChangeVerificationCode(focused: false)

                    default: break
                    }
                    switch (newFocusedField) {
                    case .username: component.focusChangeUsername(focused: true)
                    case .password: component.focusChangePassword(focused: true)
                    case .verificationCode: component.focusChangeVerificationCode(focused: true)
                    default: break
                    }
                }

                if !model.emailVerificationRequired {
                    VStack {
                        Text("Need a new verification code?")
                             .padding(.top, 30)
                        Button("Get code") {
                            component.getNewCode { message in
                                showSnackbar(message, seconds: 3)
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
                        component.signup()
                    }
                    .buttonStyle(StandardButtonStyle(foregroundColor: .white, backgroundColor: .red))
                        .frame(width: 150)
                }
                .padding(.horizontal, 30)
                .padding(.top, 30)
            }
            
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
        component.login { message in
            if message != nil {
                showSnackbar(message ?? "", seconds: 3)
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
