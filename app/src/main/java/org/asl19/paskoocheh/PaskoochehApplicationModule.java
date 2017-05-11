package org.asl19.paskoocheh;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module
public class PaskoochehApplicationModule {

    private final Context context;

    public PaskoochehApplicationModule(Context context) {
        this.context = context;
    }

    @Provides
    Context providesContext() {
        return context;
    }
}
