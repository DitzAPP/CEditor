package com.ditzdev.ceditor.editor.lang;

import com.ditzdev.ceditor.editor.util.*;

public class DefFormatter implements Formatter
{
	public static char indentChar='\t';
	private static DefFormatter _theOne = null;

	public static DefFormatter getInstance() {
		if (_theOne == null) {
			_theOne = new DefFormatter();
		}
		return _theOne;
	}
	public int createAutoIndent(CharSequence text){
		return 1;
	}
	public void format(Document text, int width){
		
	}
}

