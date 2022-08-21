//
//  ViewFocusModifier.swift
//  TeamShare
//
//  Created by Donald Robertson on 7/3/22.
//

import SwiftUI

struct ViewFocusModifier<F: Hashable & Equatable> : ViewModifier {

    let focusedField: F?
    let onFocusChange: (F?, F?) -> Void
    @State private var previousFocusedField: F? = nil

    func body(content: Content) -> some View {
        content
            .onChange(of: focusedField) { newFocusedField in
                onFocusChange(previousFocusedField, newFocusedField)
                previousFocusedField = newFocusedField
            }
    }
}

extension View {
    func viewFocus<F: Hashable & Equatable>(focusedField: F?,
                                            onFocusChange: @escaping (F?, F?) -> Void) -> some View {
        modifier(ViewFocusModifier<F>(focusedField: focusedField, onFocusChange: onFocusChange))
    }
}
