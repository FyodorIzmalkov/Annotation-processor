package example;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@AutoService(Processor.class)
public class FactoryProcessor extends AbstractProcessor {
    private Types typeUtils;
    private Elements elementsUtils;
    private Filer filer;
    private Messager messager;
    private Map<String, FactoryGroupedClasses> factoryClasses = new LinkedHashMap<String, FactoryGroupedClasses>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        elementsUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(Factory.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // Iterate over all @Factory annotated elements
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(Factory.class)){

            // Check if a class has been annotated with @Factory
            if (annotatedElement.getKind() != ElementKind.CLASS){
                error(annotatedElement, "Only classes can be annotated with @%s",
                        Factory.class.getSimpleName());
                return true; // Exit processing
            }

            TypeElement typeElement = (TypeElement) annotatedElement;

            try {
                FactoryAnnotatedClass annotatedClass =
                        new FactoryAnnotatedClass(typeElement); //throws IllegalArgExc

                if (!isValidClass(annotatedClass)){
                    return true; // Error message printed, exit processing
                }

                //Everything is fine, so try to add
                FactoryGroupedClasses factoryClass = factoryClasses.get(annotatedClass.getQualifiedFactoryGroupName());
                if (factoryClass == null){
                    String qualifiedGroupName = annotatedClass.getQualifiedFactoryGroupName();
                    factoryClass = new FactoryGroupedClasses(qualifiedGroupName);
                    factoryClasses.put(qualifiedGroupName, factoryClass);
                }

                // Throws IdAlreadyUsedException if id is conflicting with
                // another @Factory annotated class with the same id
                factoryClass.add(annotatedClass);
            } catch (IllegalArgumentException e){
                // @Factory.id() is empty
                error(typeElement, e.getMessage());
                return true;
            } catch (IdAlreadyUsedException e){
                FactoryAnnotatedClass existing = e.getExisting();
                //Already existing
                error(annotatedElement,
                        "Conflict: The class %s is annotated with @%s with id ='%s' but %s already uses the same id",
                        typeElement.getQualifiedName().toString(), Factory.class.getSimpleName(),
                        existing.getTypeElement().getQualifiedName().toString());
                return true;
            }
        }

        try {
            for (FactoryGroupedClasses factoryClass : factoryClasses.values()){
                factoryClass.generateCode(elementsUtils, filer);
            }
        } catch (IOException e){
            error(null, e.getMessage());
        }

        return true;
    }

    private boolean isValidClass(FactoryAnnotatedClass item){

        //Cast to TypeElement, has more type specific methods
        TypeElement classElement = item.getTypeElement();

        if (!classElement.getModifiers().contains(Modifier.PUBLIC)){
            error(classElement, "The class %s is not public.",
                    classElement.getQualifiedName().toString());
            return false;
        }

        //Check if it's an abstract Class
        if (classElement.getModifiers().contains(Modifier.ABSTRACT)){
            error(classElement, "The class %s is abstract. You can't annotate abstract classes with @%s",
                    classElement.getQualifiedName().toString(), Factory.class.getSimpleName());
            return false;
        }

        // Check inheritance: Class must be childclass as specified in @Factory.type();
        TypeElement superClassElement =
                elementsUtils.getTypeElement(item.getQualifiedFactoryGroupName());
        if (superClassElement.getKind() == ElementKind.INTERFACE){
            // Check interface implemented
            if (!classElement.getInterfaces().contains(superClassElement.asType())){
                error(classElement, "The class %s annotated with @%s must implement the interface %s",
                        classElement.getQualifiedName().toString(), Factory.class.getSimpleName(), item.getQualifiedFactoryGroupName());
                return false;
            }
        } else {
            // Check subclassing
            TypeElement currentClass = classElement;
            while (true) {
                TypeMirror superClassType = currentClass.getSuperclass();

                if (superClassType.getKind() == TypeKind.NONE){
                    // Basic class (java.lang.Object) is reached, so exit
                    error(classElement, "The class %s annotated with %s must inherit from %s",
                            classElement.getQualifiedName().toString(), Factory.class.getSimpleName(), item.getQualifiedFactoryGroupName());
                    return false;
                }

                if (superClassType.toString().equals(item.getQualifiedFactoryGroupName())){
                    // Required super class found
                    break;
                }

                // Moving up in inheritance tree
                currentClass = (TypeElement) typeUtils.asElement(superClassType);
            }
        }

        // Check if an empty public constructor is given
        for (Element enclosed : classElement.getEnclosedElements()){
            if (enclosed.getKind() == ElementKind.CONSTRUCTOR){
                ExecutableElement constructorElement = (ExecutableElement) enclosed;
                if (constructorElement.getParameters().size() == 0 && constructorElement.getModifiers()
                .contains(Modifier.PUBLIC)){
                    // found an empty constructor
                    return true;
                }
            }
        }

        // No empty constructor found
        error(classElement, "The class %s must provide an public empty default constructor",
                classElement.getQualifiedName().toString());
        return false;
    }

    private void error(Element e, String msg, Object ...args){
        messager.printMessage(
                Diagnostic.Kind.ERROR,
                String.format(msg, args),
                e);
    }
}
