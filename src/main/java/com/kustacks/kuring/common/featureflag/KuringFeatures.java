package com.kustacks.kuring.common.featureflag;

public enum KuringFeatures {

    UPDATE_NOTICE_EMBEDDING(new Feature("update_notice_embedding")),
    UPDATE_DEPARTMENT_NOTICE(new Feature("update_department_notice")),
    UPDATE_KUIS_HOMEPAGE_NOTICE(new Feature("update_kuis_homepage_notice")),
    UPDATE_KUIS_NOTICE(new Feature("update_kuis_notice")),
    UPDATE_USER(new Feature("update_user")),
    UPDATE_STAFF(new Feature("update_staff")),
    UPDATE_ACADEMIC_EVENT(new Feature("update_academic_event")),
    NOTIFY_ACADEMIC_EVENT(new Feature("notify_academic_event"));

    private final Feature feature;

    KuringFeatures(Feature feature) {
        this.feature = feature;
    }

    public Feature getFeature() {
        return this.feature;
    }
}
