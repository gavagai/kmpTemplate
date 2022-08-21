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
                    let emailBinding = Binding<String>(
                        get: {
                            viewModel.viewState.email.data as String
                        },
                        set: {
                            viewModel.changeField(field: AccountField.username,
                                                  value: $0,
                                                  validate: true)
                        }
                    )
                    StandardEmailAddressTextField(email: emailBinding, leadingImage: nil)
                        .validated(errorMessage: viewModel.viewState.email.error)

                    let passwordBinding = Binding<String>(
                        get: {
                            viewModel.viewState.password.data as String
                        },
                        set: {
                            viewModel.changeField(field: AccountField.password,
                                                  value: $0,
                                                  validate: false)
                        }
                    )
                    StandardPasswordTextField(password: passwordBinding, leadingImage: nil)
                        .validated(errorMessage: viewModel.viewState.password.error)

                    let passwordConfirmationBinding = Binding<String>(
                        get: {
                            viewModel.viewState.passwordConfirmation.data as String
                        },
                        set: {
                            viewModel.changeField(field: AccountField.passwordconfirmation,
                                                  value: $0,
                                                  validate: false)
                        }
                    )
                    StandardPasswordTextField("Confirm password",
                                              password: passwordConfirmationBinding,
                                              leadingImage: nil)
                    .validated(errorMessage: viewModel.viewState.passwordConfirmation.error)
                }
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
                    let givenNameBinding = Binding<String>(
                        get: {
                            viewModel.viewState.givenName.data
                        },
                        set: {
                            viewModel.changeField(field: AccountField.firstname,
                                                  value: $0,
                                                  validate: false)
                        }
                    )
                    StandardGivenNameTextField("First name", givenName: givenNameBinding)
                        .validated(errorMessage: viewModel.viewState.givenName.error)
                    
                    let familyNameBinding = Binding<String>(
                        get: {
                            viewModel.viewState.familyName.data as String
                        },
                        set: {
                            viewModel.changeField(field: AccountField.lastname,
                                                  value: $0,
                                                  validate: false)
                        }
                    )
                    StandardFamilyNameTextField("Last name", familyName: familyNameBinding)
                        .validated(errorMessage: viewModel.viewState.familyName.error)

                    let phoneBinding = Binding<String>(
                        get: {
                            viewModel.viewState.phone
                        },
                        set: {
                            viewModel.changeField(field: AccountField.phonenumber,
                                                  value: $0,
                                                  validate: false)
                        }
                    )
                    StandardPhoneNumberTextField(phoneNumber: phoneBinding, leadingImage: nil)
                }
            }
            .padding(.horizontal, 30)
            .padding(.top, 10)

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
