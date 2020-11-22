package udacity.gradle.builditbigger;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;


public class MainActivity extends AppCompatActivity {

    public final static String JOKE = "JOKE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().
                add(R.id.fragment,new MainActivityFragment()).commit();
    }

    /*public void tellJoke(View view) {
        //Toast.makeText(this, "derp", Toast.LENGTH_SHORT).show();
        //Toast.makeText(this,joke.getRandomJoke(), Toast.LENGTH_SHORT).show();
        JokeTell joke = new JokeTell();
        Intent intent = new Intent(this, JokeTellActivity.class);
        intent.putExtra(JOKE,joke.getRandomJoke());
        startActivity(intent);
    } */

    public void tellJoke(View view) {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        new EndpointsAsyncTask(this, progressBar).execute();
    }


}
