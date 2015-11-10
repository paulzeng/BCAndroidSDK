package com.beintoo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.beintoo.beclubapp.MainActivity;
import com.beintoo.beclubapp.R;

public class CardsCardAddedActivity extends BeNotificationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cards_added_card_activity);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayUseLogoEnabled(true);
        getActionBar().setTitle("Add a card");

        TextView tvActionText = (TextView) findViewById(R.id.action_button_text);
        tvActionText.setText(getString(R.string.cards_added_card_action_button));

        findViewById(R.id.action_button_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CardsCardAddedActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
