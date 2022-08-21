//
//  RootView.swift
//  iosApp
//
//  Created by Donald Robertson on 8/20/22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct RootView: View {
    @State
    private var view: TopLevelViews = .login
    
    var body: some View {
        switch (view) {
        case .login:
            LoginView(
                onSignup: {
                    view = .registration
                },
                onLogin: {
                    view = .home
                }
            )
        case .registration:
            RegistrationView(
                onComplete: {
                    view = .login
                }
            )
        case .home:
            HomeView(
                onLogout: {
                    view = .login
                }
            )
        }
    }
}

struct RootView_Previews: PreviewProvider {
    static var previews: some View {
        RootView()
    }
}

enum TopLevelViews {
    case login
    case registration
    case home
}
