package com.kustacks.kuring.config.featureflag;

public enum KuringFeatures {

    UPDATE_NOTICE_EMBEDDING(new Feature("update_notice_embedding"));

    private final Feature feature;

    KuringFeatures(Feature feature) {
        this.feature = feature;
    }

    public Feature getFeature() {
        return this.feature;
    }
}
