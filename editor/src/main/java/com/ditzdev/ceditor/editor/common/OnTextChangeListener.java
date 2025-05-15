package com.ditzdev.ceditor.editor.common;

import com.ditzdev.ceditor.editor.util.*;

public interface OnTextChangeListener {
	//void onBeginBatch();
	void onChanged(CharSequence c, int start, boolean ins, boolean typ);
	//void onEndBatch();
}
