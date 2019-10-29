/**
 * Copyright (c) 2016, 2017 TypeFox GmbH (http://www.typefox.io) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.xtext.testing;

import java.util.List;

import org.eclipse.lsp4j.Location;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

public class ReferenceTestConfiguration extends TextDocumentPositionConfiguration {
	private boolean includeDeclaration = false;

	private String expectedReferences = "";

	private Procedure1<? super List<? extends Location>> assertReferences = null;

	public boolean isIncludeDeclaration() {
		return includeDeclaration;
	}

	public void setIncludeDeclaration(final boolean includeDeclaration) {
		this.includeDeclaration = includeDeclaration;
	}

	public String getExpectedReferences() {
		return expectedReferences;
	}

	public void setExpectedReferences(final String expectedReferences) {
		this.expectedReferences = expectedReferences;
	}

	public Procedure1<? super List<? extends Location>> getAssertReferences() {
		return assertReferences;
	}

	public void setAssertReferences(final Procedure1<? super List<? extends Location>> assertReferences) {
		this.assertReferences = assertReferences;
	}
}
