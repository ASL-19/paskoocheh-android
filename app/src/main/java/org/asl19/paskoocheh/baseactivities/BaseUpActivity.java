package org.asl19.paskoocheh.baseactivities;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import org.asl19.paskoocheh.R;
import org.asl19.paskoocheh.utils.FontStyle;
import org.asl19.paskoocheh.utils.PaskoochehContextWrapper;

import butterknife.BindView;
import butterknife.ButterKnife;
//import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class BaseUpActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(FontStyle.valueOf(FontStyle.Small.name()).getResId(), true);
        setContentView(R.layout.activity_base_up);

        ButterKnife.bind(this);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.navigation_arrow);
        } else {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.navigation_arrow_ltr);
        }
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(PaskoochehContextWrapper.wrap(newBase)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (android.R.id.home):
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
