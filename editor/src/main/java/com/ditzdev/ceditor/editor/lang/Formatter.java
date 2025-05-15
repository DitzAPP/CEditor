package com.ditzdev.ceditor.editor.lang;

import com.ditzdev.ceditor.editor.util.*;

public interface Formatter
{
	//public int createAutoIndent(CharSequence text);
	public void format(Document text, int width);
}
