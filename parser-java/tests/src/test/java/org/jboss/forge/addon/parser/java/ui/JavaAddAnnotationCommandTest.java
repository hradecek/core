package org.jboss.forge.addon.parser.java.ui;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.ui.annotations.JavaAddAnnotationCommand;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Testing {@link org.jboss.forge.addon.parser.java.ui.annotations.JavaAddAnnotationCommand} class.
 * Use cases:
 * 1. Adding annotation on class
 * 2. Adding annotation on property
 * 3. Adding annotation on method
 * 4. Adding same annotation more than once - should be overwritten
 *
 * @author <a href="mailto:robert@balent.cz">Robert Balent</a>
 */
@RunWith(Arquillian.class)
public class JavaAddAnnotationCommandTest
{
   private static final String TEST_CLASS_STRING = "public class Person { "
            + "private String name; "
            + "public String getName() { return name; } "
            + "public void setName(String name) { this.name = name; } "
            + "}";
   @Inject
   private ProjectFactory projectFactory;
   @Inject
   private UITestHarness testHarness;
   @Inject
   private FacetFactory facetFactory;
   private Project project;
   private JavaClassSource targetClass;
   private CommandController commandController;

   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:parser-java"),
            @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static ForgeArchive getDeployment()
   {
      return ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:parser-java"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness")
               );
   }

   @Before
   public void setup() throws Exception
   {
      createTempProject();

   }

   @Test
   public void testAddAnnotationOnClass() throws Exception
   {
      createTargetClass(TEST_CLASS_STRING);
      createCommandController();
      commandController.initialize();
      commandController.setValueFor("annotation", "javax.persistence.Entity");
      commandController.execute();
      reloadTargetClass();

      List<AnnotationSource<JavaClassSource>> classAnnotations = targetClass.getAnnotations();
      assertEquals(1, classAnnotations.size());
      assertEquals("Entity", classAnnotations.get(0).getName());

      assertEquals(0, targetClass.getField("name").getAnnotations().size());
      assertEquals(0, targetClass.getMethod("getName").getAnnotations().size());
      assertEquals(0, targetClass.getMethod("setName", "String").getAnnotations().size());
   }

   @Test
   public void testAddAnnotationOnProperty() throws Exception
   {
      createTargetClass(TEST_CLASS_STRING);
      createCommandController();
      commandController.initialize();
      commandController.setValueFor("annotation", "javax.persistence.Column");
      commandController.setValueFor("onProperty", "name");
      commandController.execute();
      reloadTargetClass();

      List<AnnotationSource<JavaClassSource>> fieldAnnotations = targetClass.getField("name").getAnnotations();
      assertEquals(1, fieldAnnotations.size());
      assertEquals("Column", fieldAnnotations.get(0).getName());

      assertEquals(0, targetClass.getAnnotations().size());
      assertEquals(0, targetClass.getMethod("getName").getAnnotations().size());
      assertEquals(0, targetClass.getMethod("setName", "String").getAnnotations().size());
   }

   @Test
   public void testAddAnnotationOnMethod() throws Exception
   {
      createTargetClass(TEST_CLASS_STRING);
      createCommandController();
      commandController.initialize();
      commandController.setValueFor("annotation", "javax.persistence.Column");
      commandController.setValueFor("onMethod", "getName");
      commandController.execute();
      reloadTargetClass();

      List<AnnotationSource<JavaClassSource>> methodAnnotations = targetClass.getMethod("getName").getAnnotations();
      assertEquals(1, methodAnnotations.size());
      assertEquals("Column", methodAnnotations.get(0).getName());

      assertEquals(0, targetClass.getAnnotations().size());
      assertEquals(0, targetClass.getField("name").getAnnotations().size());
      assertEquals(0, targetClass.getMethod("setName", "String").getAnnotations().size());
   }

   @Test
   public void testAddAnnotationOnClassThreeTimes() throws Exception
   {
      createTargetClass(TEST_CLASS_STRING);
      createCommandController();
      commandController.initialize();
      commandController.setValueFor("annotation", "javax.persistence.Entity");
      commandController.execute();
      reloadTargetClass();
      createCommandController();
      commandController.initialize();
      commandController.setValueFor("annotation", "javax.persistence.Entity");
      commandController.execute();
      reloadTargetClass();
      createCommandController();
      commandController.initialize();
      commandController.setValueFor("annotation", "Entity");
      commandController.execute();
      reloadTargetClass();

      List<AnnotationSource<JavaClassSource>> classAnnotations = targetClass.getAnnotations();
      assertEquals(1, classAnnotations.size());
      assertEquals("Entity", classAnnotations.get(0).getName());

      assertEquals(0, targetClass.getField("name").getAnnotations().size());
      assertEquals(0, targetClass.getMethod("getName").getAnnotations().size());
      assertEquals(0, targetClass.getMethod("setName", "String").getAnnotations().size());
   }

   @Test
   public void testAddAnnotationOnPropertyThreeTimes() throws Exception
   {
      createTargetClass(TEST_CLASS_STRING);
      createCommandController();
      commandController.initialize();
      commandController.setValueFor("annotation", "javax.persistence.Column");
      commandController.setValueFor("onProperty", "name");
      commandController.execute();
      reloadTargetClass();
      createCommandController();
      commandController.initialize();
      commandController.setValueFor("annotation", "javax.persistence.Column");
      commandController.setValueFor("onProperty", "name");
      commandController.execute();
      reloadTargetClass();
      createCommandController();
      commandController.initialize();
      commandController.setValueFor("annotation", "Column");
      commandController.setValueFor("onProperty", "name");
      commandController.execute();
      reloadTargetClass();

      List<AnnotationSource<JavaClassSource>> fieldAnnotations = targetClass.getField("name").getAnnotations();
      assertEquals(1, fieldAnnotations.size());
      assertEquals("Column", fieldAnnotations.get(0).getName());

      assertEquals(0, targetClass.getAnnotations().size());
      assertEquals(0, targetClass.getMethod("getName").getAnnotations().size());
      assertEquals(0, targetClass.getMethod("setName", "String").getAnnotations().size());
   }

   @Test
   public void testAddAnnotationOnMethodThreeTimes() throws Exception
   {
      createTargetClass(TEST_CLASS_STRING);
      createCommandController();
      commandController.initialize();
      commandController.setValueFor("annotation", "javax.persistence.Column");
      commandController.setValueFor("onMethod", "getName");
      commandController.execute();
      reloadTargetClass();
      createCommandController();
      commandController.initialize();
      commandController.setValueFor("annotation", "javax.persistence.Column");
      commandController.setValueFor("onMethod", "getName");
      commandController.execute();
      reloadTargetClass();
      createCommandController();
      commandController.initialize();
      commandController.setValueFor("annotation", "Column");
      commandController.setValueFor("onMethod", "getName");
      commandController.execute();
      reloadTargetClass();

      List<AnnotationSource<JavaClassSource>> methodAnnotations = targetClass.getMethod("getName").getAnnotations();
      assertEquals(1, methodAnnotations.size());
      assertEquals("Column", methodAnnotations.get(0).getName());

      assertEquals(0, targetClass.getAnnotations().size());
      assertEquals(0, targetClass.getField("name").getAnnotations().size());
      assertEquals(0, targetClass.getMethod("setName", "String").getAnnotations().size());
   }

   @Test
   public void testAddComplexAnnotation() throws Exception
   {
      createTargetClass(TEST_CLASS_STRING);
      createCommandController();
      commandController.initialize();

      String complexAnnotation = "TestAnnotation(" +
               "param1 = {\"str1\", \"str2\"}, " +
               "param2 = \"hello\", " +
               "param3 = {String.class, Main.class}, " +
               "param4 = ENUM_VAL, " +
               "param5 = {ENUM_VAL_1,ENUM_VAL_2,ENUM_VAL_3}" +
               ")";

      commandController.setValueFor("annotation", complexAnnotation);
      commandController.execute();
      reloadTargetClass();

      List<AnnotationSource<JavaClassSource>> classAnnotations = targetClass.getAnnotations();
      assertEquals(1, classAnnotations.size());
      AnnotationSource<JavaClassSource> annotationSource = classAnnotations.get(0);
      assertEquals("TestAnnotation", annotationSource.getName());
      String[] param1 = annotationSource.getStringArrayValue("param1");
      assertEquals("str1", param1[0]);
      assertEquals("str2", param1[1]);
      assertEquals("hello", annotationSource.getStringValue("param2"));
      assertEquals("{String.class,Main.class}", annotationSource.getLiteralValue("param3"));
      assertEquals("ENUM_VAL", annotationSource.getLiteralValue("param4"));
      assertEquals("{ENUM_VAL_1,ENUM_VAL_2,ENUM_VAL_3}", annotationSource.getLiteralValue("param5"));

      assertEquals(0, targetClass.getField("name").getAnnotations().size());
      assertEquals(0, targetClass.getMethod("getName").getAnnotations().size());
      assertEquals(0, targetClass.getMethod("setName", "String").getAnnotations().size());
   }

   @Test
   public void testAddIncorrectNameAnnotation() throws Exception
   {
      createTargetClass(TEST_CLASS_STRING);
      createCommandController();
      commandController.initialize();

      String complexAnnotation = "null";

      commandController.setValueFor("annotation", complexAnnotation);
      try
      {
         commandController.execute();
         fail("IllegalArgumentException should be thrown.");
      }
      catch (IllegalArgumentException ex)
      {
         assertTrue(ex.getMessage().contains("Annotation with name \"null\" couldn't be added."));
      }
   }

   @Test
   public void testAddMissingParameterValueAnnotation() throws Exception
   {
      createTargetClass(TEST_CLASS_STRING);
      createCommandController();
      commandController.initialize();

      String complexAnnotation = "Test(param1=)";

      commandController.setValueFor("annotation", complexAnnotation);
      try
      {
         commandController.execute();
         fail("IllegalArgumentException should be thrown.");
      }
      catch (IllegalArgumentException ex)
      {
         assertTrue(ex.getMessage().contains("Parameter \"param1\" is missing or is incomplete."));
      }
   }

   @Test
   public void testAddUnparsableAnnotation() throws Exception
   {
      createTargetClass(TEST_CLASS_STRING);
      createCommandController();
      commandController.initialize();

      String complexAnnotation = "Test.";

      commandController.setValueFor("annotation", complexAnnotation);
      try
      {
         commandController.execute();
         fail("IllegalArgumentException should be thrown.");
      }
      catch (IllegalArgumentException ex)
      {
         assertTrue(ex.getMessage().contains("Can't parse annotation"));
      }
   }

   private void createTempProject()
   {
      project = projectFactory.createTempProject();
      facetFactory.install(project, JavaSourceFacet.class);
   }

   private void createTargetClass(String classString) throws FileNotFoundException
   {
      targetClass = Roaster.parse(JavaClassSource.class, classString);
      project.getFacet(JavaSourceFacet.class).saveJavaSource(targetClass);
   }

   private void createCommandController() throws Exception
   {
      commandController = testHarness.createCommandController(JavaAddAnnotationCommand.class,
               project.getFacet(JavaSourceFacet.class).getJavaResource(targetClass));
   }

   private void reloadTargetClass() throws FileNotFoundException
   {
      targetClass = Roaster.parse(JavaClassSource.class,
               project.getFacet(JavaSourceFacet.class).getJavaResource(targetClass)
                        .getUnderlyingResourceObject());
   }

}