/*
 * Copyright (c) 2011 Tah Wei Hoon.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License Version 2.0,
 * with full text available at http://www.apache.org/licenses/LICENSE-2.0.html
 *
 * This software is provided "as is". Use at your own risk.
 */
package com.ditzdev.ceditor.editor.util;

/**
 * Can be registered with a {@link ProgressSource} of interest, which will cause
 * progress updates to be sent to the ProgressObserver.
 */
public interface ProgressObserver {
	public void onComplete(int requestCode, Object result);
	public void onError(int requestCode, int errorCode, String message);
	public void onCancel(int requestCode);
}
