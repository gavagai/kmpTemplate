//
//  ProductImageView.swift
//  iosApp
//
//  Created by Donald Robertson on 8/20/22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct ProductImageView: View {
    @Environment(\.colorScheme) var colorScheme
    
    @State var productName: String = "Shall We"
    
    internal let foregroundColor: Color = Color.indigo
    
    var body: some View {
        VStack {
            Image(systemName: "figure.walk")
                .resizable()
                .aspectRatio(contentMode: .fit)
                .frame(width: 70, height: 35)
                .foregroundColor(foregroundColor)
            Text(productName)
                .foregroundColor(foregroundColor)
                .font(.system(size: 16))
                .fontWeight(.bold)
        }
    }
}

struct ProductImageView_Previews: PreviewProvider {
    static var previews: some View {
        ProductImageView()
    }
}
