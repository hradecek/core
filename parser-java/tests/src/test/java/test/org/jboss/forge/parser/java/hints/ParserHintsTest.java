/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package test.org.jboss.forge.parser.java.hints;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.container.addons.AddonId;
import org.jboss.forge.container.repositories.AddonDependencyEntry;
import org.jboss.forge.environment.Environment;
import org.jboss.forge.parser.java.resources.JavaResource;
import org.jboss.forge.ui.hints.HintsLookup;
import org.jboss.forge.ui.hints.InputType;
import org.jboss.forge.ui.hints.InputTypes;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ParserHintsTest
{
   @Deployment
   @Dependencies({ @Addon(name = "org.jboss.forge.addon:ui-hints", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge.addon:environment", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge.addon:resources", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge.addon:parser-java", version = "2.0.0-SNAPSHOT") })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge.addon:resources", "2.0.0-SNAPSHOT")),
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge.addon:ui-hints", "2.0.0-SNAPSHOT")),
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge.addon:parser-java", "2.0.0-SNAPSHOT")),
                        AddonDependencyEntry.create(AddonId.from("org.jboss.forge.addon:environment", "2.0.0-SNAPSHOT"))
               );

      return archive;
   }

   @Inject
   private Environment environment;

   @Test
   public void testNotNull() throws Exception
   {
      Assert.assertNotNull(environment);
   }

   @Test
   public void testSimpleHintLookup() throws Exception
   {
      HintsLookup hints = new HintsLookup(environment);
      InputType type = hints.getInputType(JavaResource.class);
      Assert.assertNotNull(type);
      Assert.assertEquals(InputTypes.JAVA_CLASS_PICKER, type);
   }
}
