package com.ditzdev.ceditor.editor.util;

import com.ditzdev.ceditor.editor.lang.Language;

public class LinearSearchStrategy implements SearchStrategy {
    private int _unitsDone = 0;

    @Override
    // only applicable to replaceAll operation
    public int getProgress() {
        return _unitsDone;
    }

    @Override
    public int wrappedFind(Document src, String target, int start,
                           boolean isCaseSensitive, boolean isWholeWord) {

        // search towards end of doc first...
        int foundOffset = find(src, target, start, src.length(),
                isCaseSensitive, isWholeWord);
        // ...then from beginning of doc
        if (foundOffset < 0) {
            foundOffset = find(src, target, 0, start,
                    isCaseSensitive, isWholeWord);
        }

        return foundOffset;
    }

    @Override
    public int find(Document src, String target, int start, int end,
                    boolean isCaseSensitive, boolean isWholeWord) {
        if (target.length() == 0) {
            return -1;
        }
        if (start < 0) {
            TextWarriorException.fail("TextBuffer.find: Invalid start position");
            start = 0;
        }
        if (end > src.length()) {
            TextWarriorException.fail("TextBuffer.find: Invalid end position");
            end = src.length();
        }

        end = Math.min(end, src.length() - target.length() + 1);
        int offset = start;
        while (offset < end) {
            if (equals(src, target, offset, isCaseSensitive) &&
                    (!isWholeWord || isSandwichedByWhitespace(src, offset, target.length()))) {
                break;
            }

            ++offset;
            ++_unitsDone;
        }

        if (offset < end) {
            return offset;
        } else {
            return -1;
        }
    }

    @Override
    public int wrappedFindBackwards(Document src, String target, int start,
                                    boolean isCaseSensitive, boolean isWholeWord) {

        // search towards beginning of doc first...
        int foundOffset = findBackwards(src, target, start, -1,
                isCaseSensitive, isWholeWord);
        // ...then from end of doc
        if (foundOffset < 0) {
            foundOffset = findBackwards(src, target, src.length() - 1, start,
                    isCaseSensitive, isWholeWord);
        }

        return foundOffset;
    }


    @Override
    public int findBackwards(Document src, String target, int start, int end,
                             boolean isCaseSensitive, boolean isWholeWord) {
        if (target.length() == 0) {
            return -1;
        }
        if (start >= src.length()) {
            TextWarriorException.fail("Invalid start position given to TextBuffer.find");
            start = src.length() - 1;
        }
        if (end < -1) {
            TextWarriorException.fail("Invalid end position given to TextBuffer.find");
            end = -1;
        }
        int offset = Math.min(start, src.length() - target.length());
        while (offset > end) {
            if (equals(src, target, offset, isCaseSensitive) &&
                    (!isWholeWord || isSandwichedByWhitespace(src, offset, target.length()))) {
                break;
            }

            --offset;
        }

        if (offset > end) {
            return offset;
        } else {
            return -1;
        }
    }

    @Override
    public Pair replaceAll(Document src, String searchText,
                           String replacementText, int mark,
                           boolean isCaseSensitive, boolean isWholeWord) {
        int replacementCount = 0;
        int anchor = mark;
        _unitsDone = 0;

        final char[] replacement = replacementText.toCharArray();
        int foundIndex = find(src, searchText, 0, src.length(),
                isCaseSensitive, isWholeWord);
        long timestamp = System.nanoTime();

        src.beginBatchEdit();
        while (foundIndex != -1) {
            src.deleteAt(foundIndex, searchText.length(), timestamp);
            src.insertBefore(replacement, foundIndex, timestamp);
            if (foundIndex < anchor) {
                // adjust anchor because of differences in doc length
                // after word replacement
                anchor += replacementText.length() - searchText.length();
            }
            ++replacementCount;
            _unitsDone += searchText.length(); //skip replaced chars
            foundIndex = find(
                    src,
                    searchText,
                    foundIndex + replacementText.length(),
                    src.length(),
                    isCaseSensitive,
                    isWholeWord);
        }
        src.endBatchEdit();

        return new Pair(replacementCount, Math.max(anchor, 0));
    }


    protected boolean equals(Document src, String target,
                             int srcOffset, boolean isCaseSensitive) {
        if ((src.length() - srcOffset) < target.length()) {
            //compared range in src must at least be as long as target
            return false;
        }

        int i;
        for (i = 0; i < target.length(); ++i) {
            if (isCaseSensitive &&
                    target.charAt(i) != src.charAt(i + srcOffset)) {
                return false;
            }
            // for case-insensitive search, compare both strings in lower case
            if (!isCaseSensitive &&
                    Character.toLowerCase(target.charAt(i)) !=
                            Character.toLowerCase(src.charAt(i + srcOffset))) {
                return false;
            }

        }

        return true;
    }

    /**
     * Checks if a word starting at startPosition with size length is bounded
     * by whitespace.
     */
    protected boolean isSandwichedByWhitespace(Document src,
                                               int start, int length) {
        Language charSet = Tokenizer.getLanguage();
        boolean startWithWhitespace = (start == 0) || charSet.isWhitespace(src.charAt(start - 1));

        int end = start + length;
        boolean endWithWhitespace = (end == src.length()) || charSet.isWhitespace(src.charAt(end));

        return (startWithWhitespace && endWithWhitespace);
    }

}
