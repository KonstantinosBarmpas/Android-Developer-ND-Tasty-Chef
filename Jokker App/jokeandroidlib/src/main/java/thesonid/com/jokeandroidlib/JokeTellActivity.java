package thesonid.com.jokeandroidlib;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by user on 14/8/17.
 */

public class JokeTellActivity extends AppCompatActivity {
    public final static String JOKE = "JOKE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.joke_display);
        String joke = getIntent().getStringExtra(JOKE);
        TextView textViewJoke = (TextView) findViewById(R.id.joke_text);
        textViewJoke.setText(joke);
    }
}
