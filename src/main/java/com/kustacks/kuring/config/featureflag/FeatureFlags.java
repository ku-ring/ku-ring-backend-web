package com.kustacks.kuring.config.featureflag;

import java.util.function.Supplier;

public interface FeatureFlags {

    boolean isEnabled(Feature feature);

    boolean isDisabled(Feature feature);

    default <T> T ifEnabled(Feature feature, Supplier<T> action) {
        if (this.isEnabled(feature)) {
            return action.get();
        } else {
            return null;
        }
    }

    class AlwaysEnabledFeatureFlags implements FeatureFlags {

        @Override
        public boolean isEnabled(Feature feature) {
            return true;
        }

        @Override
        public boolean isDisabled(Feature feature) {
            return false;
        }
    }

    class AlwaysDisabledFeatureFlags implements FeatureFlags {

        @Override
        public boolean isEnabled(Feature feature) {
            return false;
        }

        @Override
        public boolean isDisabled(Feature feature) {
            return true;
        }
    }
}


