package com.example.quiphassignment.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.example.quiphassignment.R

class DataBindingAdapters {

    companion object {
        @JvmStatic
        @BindingAdapter("imageResource")
        fun setImageResource(imageView: ImageView, starred: String?) {
            if (starred == "1") {
                imageView.setImageResource(R.drawable.ic_star_on_vector)
            } else {
                imageView.setImageResource(R.drawable.ic_star_off_vector)
            }
        }
    }

}