package com.kustacks.kuring.config;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

public class CustomMariaDbFunctionContributor implements FunctionContributor {

    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {
        functionContributions.getFunctionRegistry()
                .register(
                        "match",
                        new StandardSQLFunction(
                                "match(?1) against (?2 in boolean mode)",
                                StandardBasicTypes.DOUBLE
                        )
                );
    }
}
