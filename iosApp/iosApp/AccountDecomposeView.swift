//
//  AccountDecomposeView.swift
//  iosApp
//
//  Created by Donald Robertson on 8/23/22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import StandardWidgets
import shared


struct AccountDecomposeView: View {

    enum Field {
        case email
        case password
        case passwordConfirmation
        case firstName
        case lastName
        case phoneNumber
    }
    @FocusState private var focusedField: Field?
    
    private var component: Account
    @ObservedObject
    private var model: ObservableValue<AccountModel>
    
    init(_ component: Account) {
        self.component = component
        
        self.model = ObservableValue(component.model)
    }

 
    var body: some View {
        
        let model = self.model.value
        
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
                    StandardEmailAddressTextField(email: Binding(get: { model.email.data }, set: component.changeEmail), leadingImage: nil)
                        .validated(errorMessage: model.email.error)
                        .focused($focusedField, equals: .email)
                    StandardPasswordTextField(password: Binding(get: { model.password.data }, set: component.changePassword), leadingImage: nil)
                        .validated(errorMessage: model.password.error)
                        .focused($focusedField, equals: .password)
                    StandardPasswordTextField("Confirm password",
                                              password: Binding(get: { model.passwordConfirmation.data }, set: component.changePasswordConfirmation),
                                              leadingImage: nil)
                        .validated(errorMessage: model.passwordConfirmation.error)
                        .focused($focusedField, equals: .passwordConfirmation)
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
                    StandardGivenNameTextField("First name", givenName: Binding(get: { model.givenName.data }, set: component.changeFirstName))
                        .validated(errorMessage: model.givenName.error)
                        .focused($focusedField, equals: .firstName)
                    StandardFamilyNameTextField("Last name", familyName: Binding(get: { model.familyName.data }, set: component.changeLastName))
                        .validated(errorMessage: model.familyName.error)
                        .focused($focusedField, equals: .lastName)
                    StandardPhoneNumberTextField(phoneNumber: Binding(get: { model.phone }, set: component.changePhoneNumber), leadingImage: nil)
                        .focused($focusedField, equals: .phoneNumber)
                }
                
            }
            .padding(.horizontal, 30)
            .padding(.top, 10)
            .viewFocus(focusedField: focusedField) { previousFocusedField, newFocusedField in
                switch (previousFocusedField) {
                case .email: component.focusChangeEmail(focused: false)
                case .password: component.focusChangePassword(focused: false)
                case .passwordConfirmation: component.focusChangePasswordConfirmation(focused: false)
                case .firstName: component.focusChangeFirstName(focused: false)
                case .lastName: component.focusChangeLastName(focused: false)
                default: break
                }
                switch (newFocusedField) {
                case .email: component.focusChangeEmail(focused: true)
                case .password: component.focusChangePassword(focused: true)
                case .passwordConfirmation: component.focusChangePasswordConfirmation(focused: true)
                case .firstName: component.focusChangeFirstName(focused: true)
                case .lastName: component.focusChangeLastName(focused: true)
                default: break
                }
            }
            
            HStack {
                Button("Cancel", role: .cancel) {
                    component.cancelPressed()
                }
                .buttonStyle(StandardButtonStyle())

                Button("Continue") {

                    switch (focusedField) {
                    case .email: component.focusChangeEmail(focused: false)
                    case .password: component.focusChangePassword(focused: false)
                    case .passwordConfirmation: component.focusChangePasswordConfirmation(focused: false)
                    case .firstName: component.focusChangeFirstName(focused: false)
                    case .lastName: component.focusChangeLastName(focused: false)
                    default: break
                    }
                    component.continuePressed()
                }
                .buttonStyle(StandardButtonStyle())
            }
            .padding(.horizontal, 30)
            .padding(.top, 25)

            Spacer()
        }
        .navigationBarHidden(true)
    }
}




//struct AccountDecomposeView_Previews: PreviewProvider {
//    static var previews: some View {
//        AccountDecomposeView(AccountComponent())
//    }
//}
//
