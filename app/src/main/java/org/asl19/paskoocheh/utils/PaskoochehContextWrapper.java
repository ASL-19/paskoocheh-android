package org.asl19.paskoocheh.utils;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

public class PaskoochehContextWrapper extends ContextWrapper {

    public PaskoochehContextWrapper(Context context) {
        super(context);
    }

    public static ContextWrapper wrap(Context context) {
        Configuration config = context.getResources().getConfiguration();
        Locale locale = new Locale("fa");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            context = context.createConfigurationContext(config);
            config.setLocale(locale);
        } else {
            Resources.getSystem().updateConfiguration(config, context.getResources().getDisplayMetrics());
            config.locale = locale;
        }

        Locale.setDefault(locale);

        return new PaskoochehContextWrapper(context);
    }
}
