/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container.addons;

import java.util.Set;

import org.jboss.forge.container.services.ExportedInstance;

/**
 * Provides methods for registering, starting, stopping, and interacting with registered {@link Addon} instances.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface AddonRegistry
{
   <T> Set<ExportedInstance<T>> getExportedInstances(Class<T> clazz);

   <T> Set<ExportedInstance<T>> getExportedInstances(String clazz);

   <T> ExportedInstance<T> getExportedInstance(Class<T> type);

   <T> ExportedInstance<T> getExportedInstance(String type);

   /**
    * Get the registered {@link Addon} for the given {@link AddonId} instance. If no such {@link Addon} is currently
    * registered, register it and return the new reference.
    * 
    * @return the registered {@link Addon} (Never null.)
    */
   Addon getAddon(AddonId id);

   /**
    * Get all currently registered {@link Addon} instances.
    */
   Set<Addon> getAddons();

   /**
    * Get all registered {@link Addon} instances matching the given {@link AddonFilter}.
    */
   Set<Addon> getAddons(AddonFilter filter);
}
