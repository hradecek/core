package org.jboss.forge.maven.dependencies;

import org.jboss.forge.dependencies.Dependency;

import org.jboss.forge.container.util.Predicate;

/**
 * {@link Predicate} used to filter out SNAPSHOT {@link Dependency} instances.
 */
public class NonSnapshotDependencyFilter implements Predicate<Dependency>
{
   @Override
   public boolean accept(Dependency dependency)
   {
      return dependency != null && !dependency.getCoordinate().isSnapshot();
   }
}