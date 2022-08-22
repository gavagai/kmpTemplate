//
//  ObservableValue.swift
//  iosApp
//
//  Created by Donald Robertson on 8/22/22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import shared

public class ObservableValue<T : AnyObject> : ObservableObject {
    private let observableValue: Value<T>

    @Published
    var value: T
    
    private var observer: ((T) -> Void)?
    
    init(_ value: Value<T>) {
        self.observableValue = value
        self.value = observableValue.value
        self.observer = { [weak self] value in self?.value = value }

        observableValue.subscribe(observer: observer!)
    }
    
    deinit {
        self.observableValue.unsubscribe(observer: self.observer!)
    }
}
