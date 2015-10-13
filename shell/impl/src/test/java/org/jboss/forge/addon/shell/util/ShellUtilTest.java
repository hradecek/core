/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ShellUtilTest
{
   /**
    * Test method for {@link org.jboss.forge.addon.shell.util.ShellUtil#shellifyOptionName(java.lang.String)}.
    */
   @Test
   public void testShellifyOptionName()
   {
      assertEquals("target-package", ShellUtil.shellifyOptionName("targetPackage"));
   }

   @Test
   public void testShellifyOptionNameWithMultipleCapitalWords()
   {
      assertEquals("java-ee-version", ShellUtil.shellifyOptionName("javaEEVersion"));
   }

   @Test
   public void testShellifyOptionNameMultipleWords()
   {
      assertEquals("ship-a-release", ShellUtil.shellifyOptionName("shipARelease"));
   }

   @Test
   public void testShellifyOptionNameWithSingleSpace()
   {
      assertEquals("another-parameter", ShellUtil.shellifyOptionName("Another Parameter"));
   }

   @Test
   public void testShellifyOptionNameWithMultipleSpaces()
   {
      assertEquals("java-ee-version", ShellUtil.shellifyOptionName("Java EE Version"));
   }

   @Test
   public void testShellifyOptionNameEndingWithCapitals()
   {
      assertEquals("java-ee", ShellUtil.shellifyOptionName("javaEE"));
   }

}
