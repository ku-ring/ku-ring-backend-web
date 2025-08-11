package com.kustacks.kuring.common.utils.validator;

import com.kustacks.kuring.common.domain.WordHolder;
import com.kustacks.kuring.common.exception.BadWordContainsException;
import com.kustacks.kuring.common.exception.code.ErrorCode;
import com.kustacks.kuring.notice.application.port.out.BadWordsQueryPort;
import com.kustacks.kuring.notice.application.port.out.WhiteListQueryPort;
import com.kustacks.kuring.notice.domain.BadWord;
import com.kustacks.kuring.notice.domain.WhitelistWord;
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
public class BadWordValidator implements BadWordInitProcessor, WhitelistWordInitProcessor, BadWordChecker {

    private static final String COMMENT_REGEX = "[^가-힣a-zA-Z0-9]";

    private Trie badWordTrie;
    private Trie whitelistWordTrie;

    private final BadWordsQueryPort badWordQueryPort;
    private final WhiteListQueryPort whiteListQueryPort;

    @PostConstruct
    public void wordsInit() {
        this.initBadWords();
        this.initWhitelistWords();
    }

    @Override
    public void initBadWords() {
        List<BadWord> activeBadWords = badWordQueryPort.findAllByActive();
        badWordTrie = buildWordTrie(activeBadWords);

        log.info("금칙어 로드 완료 - 총 {} 개", activeBadWords.size());
    }

    @Override
    public void initWhitelistWords() {
        List<WhitelistWord> activeWhitelistWords = whiteListQueryPort.findAllByActive();
        whitelistWordTrie = buildWordTrie(activeWhitelistWords);

        log.info("허용 단어 로드 완료 - 총 {} 개", activeWhitelistWords.size());
    }

    @Override
    public void process(String content) {
        validateText(content);
    }

    private <T extends WordHolder> Trie buildWordTrie(List<T> words) {
        Trie.TrieBuilder builder = Trie.builder().ignoreCase();

        for (T word : words) {
            builder.addKeyword(word.getWord());
        }

        return builder.build();
    }

    private void validateText(String content) {
        validateOriginText(content);
        validateNormalizedText(content);
    }

    private void validateOriginText(String content) {
        checkBadWordExcludeWhitelist(content);
    }

    private void validateNormalizedText(String content) {
        String normalizedContent = content.replaceAll(COMMENT_REGEX, "").toLowerCase();
        checkBadWordExcludeWhitelist(normalizedContent);
    }

    private void checkBadWordExcludeWhitelist(String content) {
        if (badWordTrie == null) {
            log.warn("금칙어 Trie가 초기화되지 않았습니다.");
            return;
        }
        
        Collection<Emit> badWordMatches = badWordTrie.parseText(content);
        if (badWordMatches.isEmpty()) {
            return;
        }

        if (whitelistWordTrie == null) {
            log.warn("화이트리스트 Trie가 초기화되지 않았습니다.");
            return;
        }

        Collection<Emit> whitelistWordMatches = whitelistWordTrie.parseText(content);
        if (isNotAllowText(whitelistWordMatches, badWordMatches)) {
            throw new BadWordContainsException(ErrorCode.COMMENT_BAD_WORD_CONTAINS);
        }
    }

    //금칙어 목록이 모두 화이트리스트 통과한 단어라면 정상 / 하나라도 통관 못하면 예외발생
    private boolean isNotAllowText(Collection<Emit> whitelistWordMatches, Collection<Emit> badWordMatches) {
        return !badWordMatches.stream()
                .allMatch(emit -> isAnyWhitelistWord(emit, whitelistWordMatches));
    }

    //정상 단어 시작위치 <= 문제 단어 시작 위치 && 정상 단어 종료 위치 >= 문제 단어 종료 위치
    // ex. 고르곤졸라피자 -> 금칙어 졸라(3,4) 검출 -> 허용 고르곤졸라(0,6) 검출 -> 위치 비교
    private boolean isAnyWhitelistWord(Emit badWord, Collection<Emit> whitelist) {
        for (Emit white : whitelist) {
            if (white.getStart() <= badWord.getStart() && white.getEnd() >= badWord.getEnd()) {
                return true;
            }
        }
        return false;
    }
}
