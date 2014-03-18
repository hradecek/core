package org.jboss.forge.addon.scaffold.metawidget.inspector.propertystyle;

import java.awt.Color;
import java.util.Calendar;
import java.util.Date;

import org.jboss.forge.addon.scaffold.metawidget.inspector.propertystyle.ManuallyGeneratedClass;

/**
 * Tests a manually specified subclass (i.e. not generated by Forge)
 */

public class ManuallyGeneratedSubclass extends ManuallyGeneratedClass
{
   //
   // Child field
   //
   
   private String childField;
   
   public String getChildField()
   {
      return this.childField;
   }

   public void setChildField(String _childField)
   {
      this.normalField = _childField;
   }

}
