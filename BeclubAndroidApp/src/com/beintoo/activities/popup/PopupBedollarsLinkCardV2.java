package com.beintoo.activities.popup;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.beintoo.beclubapp.R;

public class PopupBedollarsLinkCardV2 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_bedollars_link_card_v2);

        TextView tvActionText = (TextView) findViewById(R.id.action_button_text);
        tvActionText.setText("Link my card now");

    }
}
