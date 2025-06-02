package com.kustacks.kuring.report.domain;

import com.kustacks.kuring.common.exception.NotFoundException;
import com.kustacks.kuring.common.exception.code.ErrorCode;

public enum ReportTargetType {
    COMMENT;

    public static ReportTargetType fromString(String value) {
        for (ReportTargetType reportTargetType : ReportTargetType.values()) {
            if (reportTargetType.name().equalsIgnoreCase(value)) {
                return reportTargetType;
            }
        }
        throw new NotFoundException(ErrorCode.REPORT_INVALID_TARGET_TYPE);
    }

    public boolean match(ReportTargetType type) {
        return this.equals(type);
    }
}
