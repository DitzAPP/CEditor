package com.ditzdev.ceditor.editor.util;

public final class Pair {
	public int first;
	public int second;
	
	public Pair(int x, int y){
		first = x;
		second = y;
	}

    @Override
    public String toString() {
        return "("+first+","+second+")";
    }
}
