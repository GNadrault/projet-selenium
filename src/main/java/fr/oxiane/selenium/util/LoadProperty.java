package fr.oxiane.selenium.util;

import fr.oxiane.dto.MethodeDeTest;
import org.junit.Ignore;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class LoadProperty {

    private static final String DEBUG_PROPERTIES = "/debug.properties";
    private static final String APPLICATION_PROPERTIES = "application.properties";
    private static final String SELENIUM_TESTS = "selenium.properties";
    private static final String PARAMETER_TESTS = "tests";
    private static final String PARAMETER_IGNORE = "ignore";
    private static final String PACKAGE_SCAN = "fr.oxiane.selenium.test";

    public void updateListMethodsTest(){
        MethodeDeTest methodeDeTest = getListeMethode();
        methodeDeTest = formaterListeMethodes(methodeDeTest);
        writeIntoPropertyFile(methodeDeTest);
    }

    private MethodeDeTest formaterListeMethodes(MethodeDeTest methodeDeTest){
        for(Method method:methodeDeTest.getMethodTestable()){
            methodeDeTest.getNameMethodATester().append(method.getName()).append(";");
        }
        for (Method method:methodeDeTest.getMethodIgnore()) {
            methodeDeTest.getNameMethodAIgnorer().append(method.getName()).append(";");
        }
        return methodeDeTest;
    }

    public static MethodeDeTest getListeMethode(){
        MethodeDeTest methodeDeTest = new MethodeDeTest();
        List<Method> listeMethodeTestable = new ArrayList<>();
        List<Method> listeMethodeIgnore = new ArrayList<>();
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(PACKAGE_SCAN))
                .setScanners(new MethodAnnotationsScanner()));
        Set<Method> listeMethodeAnnotatedTest = reflections.getMethodsAnnotatedWith(Test.class);
        for (Method method: listeMethodeAnnotatedTest) {
            if (method.getAnnotation(Ignore.class) == null){
                listeMethodeTestable.add(method);
            }else {
                listeMethodeIgnore.add(method);
            }
        }
        methodeDeTest.setMethodTestable(listeMethodeTestable);
        methodeDeTest.setMethodIgnore(listeMethodeIgnore);
        return methodeDeTest;
    }

    public void writeIntoPropertyFile(MethodeDeTest methodeDeTest){
        Properties props = new Properties();
        String pathToProperties = Thread.currentThread()
                .getContextClassLoader()
                .getResource(SELENIUM_TESTS).getPath();
        try (OutputStream outputStream = new FileOutputStream(pathToProperties)) {
            props.setProperty(PARAMETER_TESTS, methodeDeTest.getNameMethodATester().toString());
            props.setProperty(PARAMETER_IGNORE, methodeDeTest.getNameMethodAIgnorer().toString());
            props.store(outputStream, "Liste des méthodes de tests");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

    }
}
