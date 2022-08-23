//
//  SnackbarView.swift
//  iosApp
//
//  Created by Donald Robertson on 8/23/22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

public struct SnackbarView: View {
    @Environment(\.colorScheme) private var colorScheme

    @Binding
    var snackbarShowing: Bool
    @Binding
    var snackbarText: String
    
    public init(
        snackbarShowing: Binding<Bool>,
        snackbarText: Binding<String>
    ) {
        self._snackbarShowing = snackbarShowing
        self._snackbarText = snackbarText
    }
    
    public var body: some View {
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
                    .padding(10)
            }
        }
        .frame(maxHeight: .infinity)
    }
    
    
    private func snackbarForeground() -> Color { colorScheme == .dark ? .black : .white }
    private func snackbarBackground() -> Color { colorScheme == .dark ? .white : .black }
}



struct SnackbarView_Previews: PreviewProvider {
    static var previews: some View {
        SnackbarView(
            snackbarShowing: .constant(true),
            snackbarText: .constant("Some snackbar text")
        )
        SnackbarView(
            snackbarShowing: .constant(false),
            snackbarText: .constant("Some hidden snackbar text")
        )
    }
}
