package com.example.kelys.Adapters;

import android.graphics.drawable.GradientDrawable;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CategoriesHelperClass {
    GradientDrawable gradient;
    int image;
    String desc;
    TextView title;

    public CategoriesHelperClass(GradientDrawable gradient, int image, String desc) {
        this.gradient = gradient;
        this.image = image;
        this.desc = desc;
    }




    public GradientDrawable getGradient() {
        return gradient;
    }

    public int getImage() {
        return image;
    }

    public String getDesc() {
        return desc;
    }

    public TextView getTitle() {
        return title;
    }
}
