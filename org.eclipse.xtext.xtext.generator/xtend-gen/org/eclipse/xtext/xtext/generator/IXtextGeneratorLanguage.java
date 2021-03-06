/**
 * Copyright (c) 2015, 2017 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.xtext.xtext.generator;

import java.util.List;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.Grammar;
import org.eclipse.xtext.xtext.generator.model.GuiceModuleAccess;
import org.eclipse.xtext.xtext.generator.model.StandaloneSetupAccess;

/**
 * Configuration for an Xtext language. Implemented by {@link XtextGeneratorLanguage}.
 * 
 * @noimplement This interface should not be implemented by clients.
 */
@SuppressWarnings("all")
public interface IXtextGeneratorLanguage {
  Grammar getGrammar();
  
  List<String> getFileExtensions();
  
  StandaloneSetupAccess getRuntimeGenSetup();
  
  GuiceModuleAccess getRuntimeGenModule();
  
  GuiceModuleAccess getIdeGenModule();
  
  GuiceModuleAccess getEclipsePluginGenModule();
  
  GuiceModuleAccess getIdeaGenModule();
  
  GuiceModuleAccess getWebGenModule();
  
  ResourceSet getResourceSet();
  
  /**
   * @deprecated Use {@link CodeConfig#isPreferXtendStubs()} instead
   */
  @Deprecated
  boolean isGenerateXtendStubs();
}
