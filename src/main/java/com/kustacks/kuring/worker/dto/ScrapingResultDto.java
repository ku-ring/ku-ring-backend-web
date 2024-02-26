package com.kustacks.kuring.worker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jsoup.nodes.Document;

@Getter
@AllArgsConstructor
public class ScrapingResultDto {

    private Document document;

    private String viewUrl;
}
