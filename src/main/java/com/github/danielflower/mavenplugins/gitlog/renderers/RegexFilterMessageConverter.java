package com.github.danielflower.mavenplugins.gitlog.renderers;

import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * message converter that will remove all test except whatever matches the regex
 * it can apply multiple regexes (that can overlap) and pull out all matches in order
 */
public class RegexFilterMessageConverter implements MessageConverter {
    private final List<Pattern> patterns;
    private final MessageConverter converter;

    /**
     *
     * @param messageRegexFilter a string with regex separated with &&&
     * @param converter a converter that will be applied after this
     */
    public RegexFilterMessageConverter(String messageRegexFilter, /*nullable*/ MessageConverter converter) {
        patterns = Stream.of(messageRegexFilter.split("&&&"))
            .filter(re -> re.trim()
                .length() > 0)
            .map(re -> Pattern.compile(re))
            .collect(Collectors.toList());
        this.converter = converter;
    }

    @Override
    public String formatCommitMessage(String original) {

        TreeSet<Pair<Integer, Integer>> regionsToSave = new TreeSet<>((Comparator<Pair<Integer, Integer>>) (o1, o2) -> o1.getLeft()
            .compareTo(o2.getLeft()));

        patterns.forEach(pattern -> {
            Matcher m = pattern.matcher(original);
            while (m.find()) {
                if (m.group(1) != null) {
                    MatchResult matchResult = m.toMatchResult();
                    regionsToSave.add(new ImmutablePair<>(matchResult.start(1), matchResult.end(1)));
                }
            }
        });


        StringBuffer sb = new StringBuffer("");
        regionsToSave.forEach(pair -> {
                sb.append(original, pair.getLeft(), pair.getRight());
                sb.append(" ");
            }
        );

        String retStr = sb.toString()
            .trim();

        if (converter!=null){
            retStr=converter.formatCommitMessage(retStr);
        }
        return retStr;
    }
}
