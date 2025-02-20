package com.kustacks.kuring.email.application.port.out;

import java.util.Map;

public interface TemplateEnginePort {
    String process(String template, Map<String, Object> variables);
}
