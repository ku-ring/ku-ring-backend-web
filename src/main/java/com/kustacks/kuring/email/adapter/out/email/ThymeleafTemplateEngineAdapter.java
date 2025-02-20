package com.kustacks.kuring.email.adapter.out.email;

import com.kustacks.kuring.email.application.port.out.TemplateEnginePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ThymeleafTemplateEngineAdapter implements TemplateEnginePort {
    private final TemplateEngine templateEngine;

    @Override
    public String process(String template, Map<String, Object> variables) {
        Context context = new Context();
        variables.forEach(context::setVariable);
        return templateEngine.process(template, context);
    }
}
