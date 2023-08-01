package com.kustacks.kuring.category.exception;

import com.kustacks.kuring.common.exception.BusinessException;
import com.kustacks.kuring.common.exception.ErrorCode;

public class CategoryNotFoundException extends BusinessException {

    public CategoryNotFoundException() {
        super(ErrorCode.API_NOTICE_NOT_EXIST_CATEGORY);
    }
}
