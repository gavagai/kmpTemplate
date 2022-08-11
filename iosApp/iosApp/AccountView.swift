import SwiftUI
import shared

struct AccountView: View {

    @StateObject private var viewModel: ViewProxy

    init(registrationContext: RegistrationContext = RegistrationContext(email: nil, givenName: nil, familyName: nil)) {
        self._viewModel = StateObject(wrappedValue: .init(registrationContext: registrationContext))
        self.viewModel.onViewCreated()
        self.viewModel.onViewStarted()
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
                            viewModel.viewState.email.data = $0
                            viewModel.changeField(field: "Username",
                                                  value: viewModel.viewState.email.data,
                                                  validate: false)
                        }
                    )
                    TextField("Username", text: emailBinding)
                    let passwordBinding = Binding<String>(
                        get: {
                            viewModel.viewState.password.data as String
                        },
                        set: {
                            viewModel.viewState.password.data = $0
                            viewModel.changeField(field: "Password",
                                                  value: viewModel.viewState.password.data,
                                                  validate: false)
                        }
                    )
                    TextField("Password", text: passwordBinding)
                    let passwordConfirmationBinding = Binding<String>(
                        get: {
                            viewModel.viewState.passwordConfirmation.data as String
                        },
                        set: {
                            viewModel.viewState.passwordConfirmation.data = $0
                            viewModel.changeField(field: "PasswordConfirmation",
                                                  value: viewModel.viewState.passwordConfirmation.data,
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
                            viewModel.viewState.givenName.data = $0
                            viewModel.changeField(field: "FirstName",
                                                  value: viewModel.viewState.givenName.data,
                                                  validate: false)
                        }
                    )
                    TextField("First name", text: givenNameBinding)
                    let familyNameBinding = Binding<String>(
                        get: {
                            viewModel.viewState.familyName.data as String
                        },
                        set: {
                            viewModel.viewState.familyName.data = $0
                            viewModel.changeField(field: "LastName",
                                                  value: viewModel.viewState.familyName.data,
                                                  validate: false)
                        }
                    )
                    TextField("Last name", text: familyNameBinding)
                    let phoneBinding = Binding<String>(
                        get: {
                            viewModel.viewState.phone
                        },
                        set: {
                            viewModel.viewState.phone = $0
                            viewModel.changeField(field: "LastName",
                                                  value: viewModel.viewState.phone,
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
    }
}

struct AccountView_Previews: PreviewProvider {
	static var previews: some View {
		AccountView()
	}
}

extension AccountView {

    class ViewProxy: AccountSwiftUiViewModel, ObservableObject {

        @Published var viewState: AccountMVIViewModel =
            AccountMVIViewModel(
                email: ValidatedStringField(data: "", error: nil),
                password: ValidatedStringField(data: "", error: nil),
                passwordConfirmation: ValidatedStringField(data: "", error: nil),
                givenName: ValidatedStringField(data: "", error: nil),
                familyName: ValidatedStringField(data: "", error: nil),
                phone: "",
                dateOfBirth: nil,

                optionalsShown: false
            )

        override func onRender(model: AccountMVIViewModel) {
            viewState = model
        }


        override init(registrationContext: RegistrationContext) {
            super.init(registrationContext: registrationContext)
        }
    }

}
