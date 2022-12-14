import SwiftUI
import StandardWidgets
import shared

struct AccountView: View {

    @AppStorage("EmailVerified")
    private var emailVerified = false

    @StateObject
    private var viewModel: ViewProxy = ViewProxy()

    @StateObject
    private var holder: ControllerHolder
    
    private let onComplete: () -> Void
    
    enum AccountField {
        case email
        case password
        case passwordConfirmation
        case firstName
        case lastName
        case phoneNumber
        case dateOfBirth
    }
    @FocusState private var focusedField: AccountField?

    init(registrationContext: RegistrationContext = RegistrationContext(email: nil, givenName: nil, familyName: nil), onComplete: @escaping () -> Void) {
        self._holder = StateObject(
            wrappedValue: ControllerHolder { lifecycle in
                AccountController(
                    lifecycle: lifecycle,
                    registrationContext: registrationContext
                )
            }
        )
        self.onComplete = onComplete
    }

    var body: some View {

        VStack {
             ProductImageView()
                 .padding(.top, 15)

            VStack {
                Section(header:
                            HStack {
                                Text("Account Details".uppercased())
                                    .font(.subheadline)
                                Spacer()
                            }
                            .padding(.leading, 7)
                ) {
                    StandardEmailAddressTextField(email: Binding(get: { viewModel.viewState.email.data }, set: viewModel.changeEmail), leadingImage: nil)
                        .validated(errorMessage: viewModel.viewState.email.error)
                        .focused($focusedField, equals: .email)

                    StandardPasswordTextField(password: Binding(get: { viewModel.viewState.password.data }, set: viewModel.changePassword), leadingImage: nil)
                        .validated(errorMessage: viewModel.viewState.password.error)
                        .focused($focusedField, equals: .password)
                    
                    StandardPasswordTextField("Confirm password",
                                              password: Binding(get: { viewModel.viewState.passwordConfirmation.data }, set: viewModel.changePasswordConfirmation),
                                              leadingImage: nil)
                    .validated(errorMessage: viewModel.viewState.passwordConfirmation.error)
                    .focused($focusedField, equals: .passwordConfirmation)                }
                VStack {}
                    .padding(.top, 20)
                Section(header:
                            HStack {
                                Text("Optional personal details".uppercased())
                                    .font(.subheadline)
                                Spacer()
                            }
                            .padding(.leading, 7)
                ) {
                    StandardGivenNameTextField("First name", givenName: Binding(get: { viewModel.viewState.givenName.data }, set: viewModel.changeGivenName))
                        .validated(errorMessage: viewModel.viewState.givenName.error)
                        .focused($focusedField, equals: .firstName)
                    
                    StandardFamilyNameTextField("Last name", familyName: Binding(get: { viewModel.viewState.familyName.data }, set: viewModel.changeFamilyName))
                        .validated(errorMessage: viewModel.viewState.familyName.error)
                        .focused($focusedField, equals: .lastName)
                    
                    StandardPhoneNumberTextField(phoneNumber: Binding(get: { viewModel.viewState.phone }, set: viewModel.changePhoneNumber), leadingImage: nil)
                }
            }
            .padding(.horizontal, 30)
            .padding(.top, 10)
            .viewFocus(focusedField: focusedField) { previousFocusedField, newFocusedField in
                switch (previousFocusedField) {
                case .email: viewModel.focusChangeEmail(focused: false)
                case .password: viewModel.focusChangePassword(focused: false)
                case .passwordConfirmation: viewModel.focusChangePasswordConfirmation(focused: false)
                case .firstName: viewModel.focusChangeGivenName(focused: false)
                case .lastName: viewModel.focusChangeFamilyName(focused: false)

                default: break
                }
                switch (newFocusedField) {
                case .email: viewModel.focusChangeEmail(focused: true)
                case .password: viewModel.focusChangePassword(focused: true)
                case .passwordConfirmation: viewModel.focusChangePasswordConfirmation(focused: true)
                case .firstName: viewModel.focusChangeGivenName(focused: true)
                case .lastName: viewModel.focusChangeFamilyName(focused: true)
                default: break
                }
            }


            HStack {
                Button("Cancel", role: .cancel) {
                    viewModel.cancelPressed()
                    onComplete()
                }
                .buttonStyle(StandardButtonStyle())
                    .frame(width: 150)

                Button("Continue") {
                     viewModel.continuePressed()
                }
                .buttonStyle(StandardButtonStyle())
                    .frame(width: 150)
            }
            .padding(.horizontal, 30)
            .padding(.top, 25)
        }
        .onFirstAppear { holder.controller.onViewCreated(view: viewModel, viewLifecycle: holder.lifecycle) }
        .onAppear { LifecycleRegistryExtKt.resume(holder.lifecycle) }
        .onDisappear { LifecycleRegistryExtKt.stop(holder.lifecycle) }
    }
}

struct AccountView_Previews: PreviewProvider {
	static var previews: some View {
        AccountView() {}
	}
}

extension AccountView {

    private class ViewProxy: AccountBaseMviView, ObservableObject {

        @Published
        var viewState: AccountMviViewModel = AccountMviViewModel()


        override func render(model: AccountMviViewModel) {
            super.render(model: model)
            viewState = model
        }

        override func onContinue() {
        }
        override func onCancel() {
        }
    }

    private class ControllerHolder : ObservableObject {
        let lifecycle: LifecycleRegistry = LifecycleRegistryKt.LifecycleRegistry()
        let controller: AccountController

        init(factory: (Lifecycle) -> AccountController) {
            controller = factory(lifecycle)
        }

        deinit {
            LifecycleRegistryExtKt.destroy(lifecycle)
        }
    }
}
