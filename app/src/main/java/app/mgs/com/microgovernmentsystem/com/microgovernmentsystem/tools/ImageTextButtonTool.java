package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import app.mgs.com.microgovernmentsystem.R;


public class ImageTextButtonTool extends LinearLayout {
    private ImageView iv;
    private TextView tv;

    public ImageTextButtonTool(Context context) {
        super(context);
    }

    public ImageTextButtonTool(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.tool_image_text_button, this, true);
        iv = (ImageView) findViewById(R.id.custom_itb_iv);
        tv = (TextView) findViewById(R.id.custom_itb_tv);
    }

    public void setDefaultImageResource(int resId) {
        iv.setImageResource(resId);
    }

    public void setDefaultTextViewText(String text) {
        tv.setText(text);
    }

    public void setImageResource(int resId) {
        iv.setImageResource(resId);
    }


    public void setTextViewText(String text) {
        tv.setText(text);
    }

    public void setTextColor(int color) {
        tv.setTextColor(color);
    }
}
