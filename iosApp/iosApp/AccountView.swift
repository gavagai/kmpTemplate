import SwiftUI
import shared

struct AccountView: View {

    @StateObject
    private var viewModel: ViewProxy = ViewProxy()

    @StateObject
    private var holder: ControllerHolder

    init(registrationContext: RegistrationContext = RegistrationContext(email: nil, givenName: nil, familyName: nil)) {
        self._holder = StateObject(
            wrappedValue: ControllerHolder { lifecycle in
                AccountController(
                    lifecycle: lifecycle,
                    registrationContext: registrationContext
                )
            }
        )
    }


    var body: some View {

        VStack {
//             ProductImageView()
//                 .padding(.top, 15)

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
                            viewModel.changeField(field: "Username",
                                                  value: $0,
                                                  validate: true)
                        }
                    )
                    TextField("Username", text: emailBinding)
                    Text("Rendered: \(viewModel.rendered) #\(viewModel.viewState.email.error ?? "No error")#")
                    let passwordBinding = Binding<String>(
                        get: {
                            viewModel.viewState.password.data as String
                        },
                        set: {
                            viewModel.changeField(field: "Password",
                                                  value: $0,
                                                  validate: false)
                        }
                    )
                    TextField("Password", text: passwordBinding)
                    let passwordConfirmationBinding = Binding<String>(
                        get: {
                            viewModel.viewState.passwordConfirmation.data as String
                        },
                        set: {
                            viewModel.changeField(field: "PasswordConfirmation",
                                                  value: $0,
                                                  validate: false)
                        }
                    )
                    TextField("Password confirmation", text: passwordConfirmationBinding)
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
                            viewModel.changeField(field: "FirstName",
                                                  value: $0,
                                                  validate: false)
                        }
                    )
                    TextField("First name", text: givenNameBinding)
                    let familyNameBinding = Binding<String>(
                        get: {
                            viewModel.viewState.familyName.data as String
                        },
                        set: {
                            viewModel.changeField(field: "LastName",
                                                  value: $0,
                                                  validate: false)
                        }
                    )
                    TextField("Last name", text: familyNameBinding)
                    let phoneBinding = Binding<String>(
                        get: {
                            viewModel.viewState.phone
                        },
                        set: {
                            viewModel.changeField(field: "LastName",
                                                  value: $0,
                                                  validate: false)
                        }
                    )
                    TextField("Phone number", text: phoneBinding)
                }
            }
            .padding(.horizontal, 30)
            .padding(.top, 10)

            HStack {
                Button("Cancel", role: .cancel) {
                    viewModel.cancelPressed()
                }

                Button("Continue") {
                     viewModel.continuePressed()
                }
            }
            .padding(.horizontal, 30)
            .padding(.top, 25)

            Spacer()
        }
        .onFirstAppear { holder.controller.onViewCreated(view: viewModel, viewLifecycle: holder.lifecycle) }
        .onAppear { LifecycleRegistryExtKt.resume(holder.lifecycle) }
        .onDisappear { LifecycleRegistryExtKt.stop(holder.lifecycle) }
    }
}

struct AccountView_Previews: PreviewProvider {
	static var previews: some View {
		AccountView()
	}
}

extension AccountView {

    private class ViewProxy: AccountBaseMviView, ObservableObject {

        @Published
        var viewState: AccountMviViewModel = AccountMviViewModel()
        @Published
        var rendered: Int = 0


        override func render(model: AccountMviViewModel) {
            rendered = rendered + 1
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
