/*******************************************************************************
 * Copyright (c) 2012 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtext.generator.trace;

import static com.google.common.collect.Lists.*;
import static com.google.common.collect.Maps.*;
import static com.google.common.collect.Sets.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.LanguageInfo;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.smap.SDEInstaller;
import org.eclipse.xtext.smap.SmapGenerator;
import org.eclipse.xtext.smap.SmapStratum;
import org.eclipse.xtext.util.Files;

import com.google.inject.Inject;

/**
 * @author Sven Efftinge - Initial contribution and API
 */
public class SmapSupport {
	
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(SmapSupport.class);
	
	@Inject private IResourceServiceProvider.Registry serviceProviderRegistry;

	public String generateSmap(AbstractTraceRegion rootTraceRegion, String outputFileName) {
		final Set<LineMapping> lineData = newLinkedHashSet();
		createSmapInfo(rootTraceRegion, lineData, -1);
		List<LineMapping> lineInfo = normalizeLineInfo(lineData);

		return toSmap(outputFileName, lineInfo);
	}

	protected String toSmap(String outputFileName, List<LineMapping> lineInfo) {
		SmapGenerator generator = new SmapGenerator();
		generator.setOutputFileName(outputFileName);
		Map<String,SmapStratum> strata = newHashMap(); 
		for (LineMapping lm : lineInfo) {
			String stratumName = getStratumName(lm.source);
			SmapStratum stratum = strata.get(stratumName);
			if (stratum == null) {
				stratum = new SmapStratum(stratumName);
				strata.put(stratumName, stratum);
				generator.addStratum(stratum, true);
			}
			final String path = getPath(lm.source);
			final String fileName = lm.source.lastSegment();
			stratum.addFile(fileName, path);
			stratum.addLineData(lm.sourceStartLine, fileName, 1, lm.targetStartLine+1, lm.targetEndLine - lm.targetStartLine + 1);
		}
		return generator.getString();
	}

	public List<LineMapping> normalizeLineInfo(Set<LineMapping> lineData) {
		ArrayList<LineMapping> list = newArrayList(lineData);
		Collections.sort(list, new Comparator<LineMapping>() {
			public int compare(LineMapping o1, LineMapping o2) {
				int compareResult = o2.targetStartLine - o1.targetStartLine;
				if (compareResult == 0) {
					return o2.targetEndLine - o1.targetEndLine;
				}
				return compareResult;
			}
		});
		List<LineMapping> result = newLinkedList();
		LineMapping current = null;
		for (LineMapping mapping : list) {
			if (current != null 
				&& current.sourceStartLine == mapping.sourceStartLine
				&& current.source.equals(mapping.source)) {
				
				current.targetStartLine = mapping.targetStartLine;
			} else {
				int lastLine = mapping.targetEndLine;
				if (current != null) {
					lastLine = current.targetStartLine-1;
					result.add(0,current);
				}
				if (lastLine < mapping.targetStartLine) {
					lastLine = mapping.targetStartLine;
				}
				current = new LineMapping(mapping.sourceStartLine, mapping.targetStartLine, lastLine, mapping.source);
			}
		}
		result.add(0, current);
		return result;
	}

	public void createSmapInfo(AbstractTraceRegion targetRegion, Set<LineMapping> lineMappings, int mappedLine) {
		if (mappedLine == targetRegion.getMyLineNumber()) {
			if (mappedLine == targetRegion.getMyEndLineNumber())
				return; //SKIP
			for (AbstractTraceRegion nested: targetRegion.getNestedRegions()) {
				createSmapInfo(nested, lineMappings, mappedLine);
			}
		}
		ILocationData location = targetRegion.getMergedAssociatedLocation();
		if (location != null) {
			final URI path = targetRegion.getAssociatedPath();
			if (path != null) {
				lineMappings.add(new LineMapping(location.getLineNumber()+1, targetRegion.getMyLineNumber(), targetRegion.getMyEndLineNumber(), path));
			}
		}
		if (targetRegion.getMyLineNumber() != targetRegion.getMyEndLineNumber()) {
			for (AbstractTraceRegion nested: targetRegion.getNestedRegions()) {
				createSmapInfo(nested, lineMappings, targetRegion.getMyLineNumber());
			}
		}
	}

	protected String getStratumName(final URI path) {
		IResourceServiceProvider provider = serviceProviderRegistry.getResourceServiceProvider(path.trimFragment());
		String name = provider.get(LanguageInfo.class).getShortName();
		return name;
	}

	protected String getPath(URI path) {
		String fullPath = path.trimFragment().toPlatformString(true);
		final String substring = fullPath.substring(fullPath.substring(1).indexOf('/')+1);
		return substring;
	}
	
	/**
	 * Installs the given SMAP as source debug extension on the given byte code.
	 */
	public InputStream getModifiedByteCode(String smap, InputStream originalByteCode) throws CoreException, IOException {
		byte[] byteCode = Files.readStreamIntoByteArray(originalByteCode);
		InputStream newByteCode = new ByteArrayInputStream(
				new SDEInstaller(byteCode, smap.getBytes()).getUpdatedByteCode());
		return newByteCode;
	}

	public static class LineMapping {
 
		public int 	sourceStartLine,
					targetStartLine,
					targetEndLine;
		public URI source;
		public LineMapping(int sourceStartLine, int targetStartLine, int targetEndLine,
				URI source) {
			this.sourceStartLine = sourceStartLine;
			this.targetStartLine = targetStartLine;
			this.targetEndLine = targetEndLine;
			this.source = source;
			if (sourceStartLine < 0 || targetStartLine < 0 || targetEndLine <0 || targetStartLine>targetEndLine || source == null)
				throw new IllegalArgumentException(toString());
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((source == null) ? 0 : source.hashCode());
			result = prime * result + sourceStartLine;
			result = prime * result + targetEndLine;
			result = prime * result + targetStartLine;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			LineMapping other = (LineMapping) obj;
			if (source == null) {
				if (other.source != null)
					return false;
			} else if (!source.equals(other.source))
				return false;
			if (sourceStartLine != other.sourceStartLine)
				return false;
			if (targetEndLine != other.targetEndLine)
				return false;
			if (targetStartLine != other.targetStartLine)
				return false;
			return true;
		}
		@Override
		public String toString() {
			return "LineMapping [sourceStartLine=" + sourceStartLine + ", targetStartLine=" + targetStartLine
					+ ", targetEndLine=" + targetEndLine + ", source=" + source + "]";
		}
		
	}
}
