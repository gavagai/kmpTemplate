//
//  RegistrationView.swift
//  iosApp
//
//  Created by Donald Robertson on 8/20/22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct RegistrationView: View {
    
    private let onComplete: () -> Void
    
    init(onComplete: @escaping () -> Void) {
        self.onComplete = onComplete
    }
    
    var body: some View {
        NavigationView {
            AccountView(onComplete: {
                self.onComplete()
            })
                .navigationBarTitleDisplayMode(.inline)
        }
        .navigationViewStyle(.stack) // NB! Avoids weird nav pops when alerts are dismissed
    }
}

struct RegistrationView_Previews: PreviewProvider {
    static var previews: some View {
        RegistrationView(onComplete: {})
    }
}
