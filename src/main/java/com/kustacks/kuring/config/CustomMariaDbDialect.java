package com.kustacks.kuring.config;

import org.hibernate.dialect.MariaDB103Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;

public class CustomMariaDbDialect extends MariaDB103Dialect {

    public CustomMariaDbDialect() {
        super();
        registerFunction("match", new SQLFunctionTemplate(StandardBasicTypes.DOUBLE, "match(?1) against (?2 in boolean mode)"));
    }
}
