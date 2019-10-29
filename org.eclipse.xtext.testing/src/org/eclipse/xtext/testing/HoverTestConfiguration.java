/**
 * Copyright (c) 2016, 2017 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.xtext.testing;

import org.eclipse.lsp4j.Hover;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

public class HoverTestConfiguration extends TextDocumentPositionConfiguration {
	private String expectedHover = "";

	private Procedure1<? super Hover> assertHover = null;

	public String getExpectedHover() {
		return expectedHover;
	}

	public void setExpectedHover(final String expectedHover) {
		this.expectedHover = expectedHover;
	}

	public Procedure1<? super Hover> getAssertHover() {
		return assertHover;
	}

	public void setAssertHover(final Procedure1<? super Hover> assertHover) {
		this.assertHover = assertHover;
	}
}
