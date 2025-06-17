package com.kustacks.kuring.common.utils.validator;

import com.kustacks.kuring.common.exception.BadWordContainsException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.notice.application.port.out.BadWordsQueryPort;
import com.kustacks.kuring.notice.domain.BadWord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BadWordValidator implements BadWordInitProcessor, BadWordChecker {

    private static final String COMMENT_REGEX = "[^가-힣a-zA-Z0-9]";

    private Trie badWordTrie;

    private final BadWordsQueryPort badWordQueryPort;

    @PostConstruct
    public void badWordInit() {
        this.process();
    }

    @Override
    public void process() {
        List<BadWord> activeBadWords = badWordQueryPort.findAllByActive();
        if (!activeBadWords.isEmpty()) {
            Trie.TrieBuilder builder = Trie.builder().ignoreCase();

            for (BadWord badWord : activeBadWords) {
                builder.addKeyword(badWord.getWord());
            }

            badWordTrie = builder.build();
        }

        log.info("금칙어 로드 완료 - 총 {} 개", activeBadWords.size());
    }

    @Override
    public void process(String content) {
        validateText(content);
    }

    private void validateText(String content) {
        if (badWordTrie != null) {
            // 원본 텍스트에서 금칙어 검사
            Collection<Emit> originalMatches = badWordTrie.parseText(content);
            if (!originalMatches.isEmpty()) {
                throw new BadWordContainsException(ErrorCode.COMMENT_BAD_WORD_CONTAINS);
            }

            // 특수문자 제거 및 소문자 변환 후 금칙어 검사
            String normalizedContent = content.replaceAll(COMMENT_REGEX, "").toLowerCase();
            Collection<Emit> normalizedMatches = badWordTrie.parseText(normalizedContent);
            if (!normalizedMatches.isEmpty()) {
                throw new BadWordContainsException(ErrorCode.COMMENT_BAD_WORD_CONTAINS);
            }
        }
    }
}
