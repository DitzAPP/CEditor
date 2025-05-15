package com.ditzdev.ceditor.editor.util;

import java.util.Vector;


/**
 * Worker thread to carry our find and replaceAll operations.
 * The find thread should not be reused after it has completed. Create a new one
 * for another operation.
 */
public class FindThread extends Thread implements ProgressSource {
    /**
     * Reported progress will be scaled from 0 to MAX_PROGRESS
     */
    private final static int MAX_PROGRESS = 100;
    final private SearchStrategy FINDER = new LinearSearchStrategy();
    protected int _requestCode;
    protected Document _src;
    protected Vector<ProgressObserver> _progressObservers = new Vector<ProgressObserver>();
    protected String _searchText;
    protected String _replacementText;
    protected int _start;
    protected boolean _isCaseSensitive;
    protected boolean _isWholeWord;
    protected boolean _isDone = false;
    protected FindResults _results;
    private int _docSize = 0; // size, in chars, of the document to search

    private FindThread(int requestCode, Document src, String searchText, int start,
                       boolean isCaseSensitive, boolean isWholeWord) {
        _requestCode = requestCode;
        _src = src;
        _start = start;
        _searchText = searchText;
        _isCaseSensitive = isCaseSensitive;
        _isWholeWord = isWholeWord;
        _docSize = src.length();
    }

    private FindThread(int requestCode, Document src, String searchText, String replacementText, int start,
                       boolean isCaseSensitive, boolean isWholeWord) {
        _requestCode = requestCode;
        _src = src;
        _start = start;
        _searchText = searchText;
        _replacementText = replacementText;
        _isCaseSensitive = isCaseSensitive;
        _isWholeWord = isWholeWord;
        _docSize = src.length();
    }

    static public FindThread createFindThread(Document src, String searchText, int start, boolean isForwardSearch,
                                              boolean isCaseSensitive, boolean isWholeWord) {

        int requestCode = (isForwardSearch) ? ProgressSource.FIND : ProgressSource.FIND_BACKWARDS;

        return new FindThread(requestCode, src, searchText, start, isCaseSensitive, isWholeWord);
    }

    static public FindThread createReplaceAllThread(Document src, String searchText, String replacementText, int start,
                                                    boolean isCaseSensitive, boolean isWholeWord) {
        return new FindThread(ProgressSource.REPLACE_ALL, src, searchText, replacementText, start, isCaseSensitive, isWholeWord);
    }

    public void run() {
        _isDone = false;
        _results = new FindResults(_searchText.length());

        switch (_requestCode) {
            case ProgressSource.FIND:
                _results.foundOffset = FINDER.wrappedFind(_src, _searchText, _start, _isCaseSensitive, _isWholeWord);
                notifyComplete(_results);
                break;
            case ProgressSource.FIND_BACKWARDS:
                _results.foundOffset = FINDER.wrappedFindBackwards(_src, _searchText, _start,
                        _isCaseSensitive, _isWholeWord);
                notifyComplete(_results);
                break;
            case ProgressSource.REPLACE_ALL:
                Pair replaceResult = FINDER.replaceAll(_src, _searchText, _replacementText, _start,
						_isCaseSensitive, _isWholeWord);
                _results.replacementCount = replaceResult.first;
                _results.newStartPosition = replaceResult.second;
                notifyComplete(_results);
                break;
            default:
                TextWarriorException.assertVerbose(false, "Invalid request code for FindThread");
                break;
        }
    }

    @Override
    public final int getMin() {
        return 0;
    }

    @Override
    public final int getMax() {
        return MAX_PROGRESS;
    }

    @Override
    public final int getCurrent() {
        float progressProportion = (_docSize == 0) ? 0.f : (float) FINDER.getProgress() / (float) _docSize;
        return (int) (progressProportion * MAX_PROGRESS);
    }

    @Override
    public final void forceStop() {
        //TODO implement
    }

    @Override
    public final boolean isDone() {
        return _isDone;
    }

    @Override
    synchronized public final void registerObserver(ProgressObserver po) {
        _progressObservers.addElement(po);
    }

    @Override
    synchronized public final void removeObservers() {
        _progressObservers.clear();
    }

    synchronized protected void notifyComplete(Object result) {
        _isDone = true;
        for (ProgressObserver po : _progressObservers) {
            po.onComplete(_requestCode, result);
        }
    }

    public final int getRequestCode() {
        return _requestCode;
    }

    public final FindResults getResults() {
        return _results;
    }

    public static class FindResults {
        public int foundOffset = -1;
        public int replacementCount = 0;
        public int newStartPosition = 0;
        public int searchTextLength = 0; //for convenience

        public FindResults(int searchLength) {
            searchTextLength = searchLength;
        }
    }

}
