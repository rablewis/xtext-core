/*******************************************************************************
 * Copyright (c) 2010 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.valueconverter;

import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.XtextStandaloneSetup;
import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.conversion.impl.IDValueConverter;
import org.eclipse.xtext.junit4.AbstractXtextTests;
import org.junit.Test;

/**
 * @author Moritz Eysholdt - Initial contribution and API
 * @author Sebastian Zarnekow
 */
public class IDValueConverterTest extends AbstractXtextTests {

	private IDValueConverter idConverter;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		with(XtextStandaloneSetup.class);
		idConverter = get(IDValueConverter.class);
		idConverter.setRule(GrammarUtil.findRuleForName(getGrammarAccess().getGrammar(), "ID"));
	}

	@Test public void testSimple() throws Exception {
		String s = "abc";
		String value = idConverter.toValue(s, null);
		assertEquals("abc", value);
		assertEquals(s, idConverter.toString(value));
	}
	
	@Test public void testEscaped() throws Exception {
		String s = "grammar";
		String value = idConverter.toString(s);
		assertEquals("^grammar", value);
		assertEquals(s, idConverter.toValue(value, null));
	}

	@Test public void testNull() throws Exception {
		try {
			idConverter.toString(null);
			fail("Null value not detected.");
		} catch (ValueConverterException e) {
			// normal operation
//			System.out.println(e.getMessage());
		}
	}

	@Test public void testEmpty() throws Exception {
		try {
			idConverter.toString("");
			fail("Empty value not detected.");
		} catch (ValueConverterException e) {
			// normal operation
//			System.out.println(e.getMessage());
		}
	}

	@Test public void testInvalidChar1() throws Exception {
		try {
			idConverter.toString("^foo");
			fail("invalid char not detected..");
		} catch (ValueConverterException e) {
			// normal operation
//			System.out.println(e.getMessage());
		}
	}

	@Test public void testInvalidChar2() throws Exception {
		try {
			idConverter.toString("foo%bar[]");
			fail("Empty value not detected.");
		} catch (ValueConverterException e) {
			// normal operation
//			System.out.println(e.getMessage());
		}
	}

	@Test public void testInvalidChar3() throws Exception {
		try {
			idConverter.toString("0foo");
			fail("Empty value not detected.");
		} catch (ValueConverterException e) {
			// normal operation
//			System.out.println(e.getMessage());
		}
	}

}