    package com.mattkormann.tournamentmanager;

    import android.os.Bundle;
    import android.support.design.widget.FloatingActionButton;
    import android.support.design.widget.Snackbar;
    import android.support.v4.app.FragmentActivity;
    import android.support.v7.app.AppCompatActivity;
    import android.support.v7.widget.Toolbar;
    import android.view.View;

    public class MainActivity extends FragmentActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
			
            if (findViewById(R.id.fragment_container) != null) {

                if (savedInstanceState != null) {
                    return;
                }

                MainActivityFragment mainFragment = new MainActivityFragment();
                mainFragment.setArguments(getIntent().getExtras());
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, mainFragment).commit();
            }

        }

    }
