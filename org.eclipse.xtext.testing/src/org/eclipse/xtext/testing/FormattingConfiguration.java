/**
 * Copyright (c) 2016, 2017 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.xtext.testing;

public class FormattingConfiguration extends TextDocumentConfiguration {
	private String expectedText = "";

	public String getExpectedText() {
		return expectedText;
	}

	public void setExpectedText(final String expectedText) {
		this.expectedText = expectedText;
	}
}
