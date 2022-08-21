//
//  HomeView.swift
//  iosApp
//
//  Created by Donald Robertson on 8/20/22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct HomeView: View {
    private var onLogout: () -> Void
    
    init(onLogout: @escaping () -> Void) {
        self.onLogout = onLogout
    }
    
    var body: some View {
        Text("Home")
        Button("Logout") {
            onLogout()
        }
    }
}

struct HomeView_Previews: PreviewProvider {
    static var previews: some View {
        HomeView() {}
    }
}
