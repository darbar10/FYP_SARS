package com.example.sars_segmentation

import android.os.Parcel
import android.os.Parcelable

data class Product(
    var Product_Name: String?,
    var Category: String?,
    var Price: String?,
    var ImageURL: String?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(Product_Name)
        parcel.writeString(Category)
        parcel.writeString(Price)
        parcel.writeString(ImageURL)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}
