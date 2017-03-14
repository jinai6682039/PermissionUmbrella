package fdd.permission.processor;

import com.google.auto.common.SuperficialValidation;
import com.google.auto.service.AutoService;

import com.fangdd.annotation.PermissionAllocation;
import com.fangdd.annotation.PermissionDeny;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.sun.source.util.Trees;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeScanner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import fdd.permission.binder.BinderSet;
import fdd.permission.binder.MethodBinder;
import fdd.permission.binder.Parameter;
import fdd.permission.exception.MultipleMethodAddException;
import fdd.permission.exception.WriteJavaFileFailureException;
import fdd.permission.utils.ProcesserUtil;

import static fdd.permission.utils.CheckUtil.checkMethod;
import static fdd.permission.utils.CheckUtil.checkMethodHaveParameters;
import static java.util.Arrays.asList;

/**
 * Created by hanxu on 2017/3/2.
 */

@AutoService(Processor.class)
public class PermissionAllocationProcessor extends AbstractProcessor {

    private Types mTypes;
    private Elements mElements;
    private Filer mFiler;
    private Messager mMessager;
    private Filer filer;

    private Trees trees;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<TypeElement, BinderSet> binderSetMap = findParseClass(roundEnv);
        for (Map.Entry<TypeElement, BinderSet> entry : binderSetMap.entrySet()) {
            TypeElement typeElement = entry.getKey();
            BinderSet binderSet = entry.getValue();

            JavaFile javaFile = binderSet.generateJava(1);

            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                throw new WriteJavaFileFailureException(typeElement.getQualifiedName().toString(), e.getMessage());
            }
        }
        return false;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mTypes = processingEnv.getTypeUtils();
        mElements = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();

        filer = processingEnv.getFiler();

        try {
            trees = Trees.instance(processingEnv);
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : getSupportedAnnotations()) {
            types.add(annotation.getCanonicalName());
        }
        return types;
    }

    private Set<Class<? extends Annotation>> getSupportedAnnotations() {
        Set<Class<? extends Annotation>> annotations = new LinkedHashSet<>();

        annotations.add(PermissionAllocation.class);
        annotations.add(PermissionDeny.class);

        return annotations;
    }

    @Override
    public Set<String> getSupportedOptions() {
        return Collections.singleton("PermissionAllocation");
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private Map<TypeElement, BinderSet> findParseClass(RoundEnvironment environment) {
        Map<TypeElement, BinderSet.Builder> builderMap = new LinkedHashMap<>();
        Set<TypeElement> erasedTargetNames = new LinkedHashSet<>();

        for (Element element : environment.getElementsAnnotatedWith(PermissionAllocation.class)) {
            if (!SuperficialValidation.validateElement(element))
                continue;
            parseAnnotatedMethod(PermissionAllocation.class, element, builderMap, erasedTargetNames);
        }

        for (Element element : environment.getElementsAnnotatedWith(PermissionDeny.class)) {
            if (!SuperficialValidation.validateElement(element))
                continue;
            parseAnnotatedMethod(PermissionDeny.class, element, builderMap, erasedTargetNames);
        }

        // Associate superclass binders with their subclass binders. This is a queue-based tree walk
        // which starts at the roots (superclasses) and walks to the leafs (subclasses).
        Deque<Map.Entry<TypeElement, BinderSet.Builder>> entries =
                new ArrayDeque<>(builderMap.entrySet());
        Map<TypeElement, BinderSet> binderSetMap = new LinkedHashMap<>();
        while (!entries.isEmpty()) {

            Map.Entry<TypeElement, BinderSet.Builder> entry = entries.removeFirst();

            TypeElement type = entry.getKey();
            BinderSet.Builder builder = entry.getValue();

            TypeElement parentType = findParentType(type, erasedTargetNames);
            if (parentType == null) {
                binderSetMap.put(type, builder.build());
            } else {
                BinderSet parentBinding = binderSetMap.get(parentType);
                if (parentBinding != null) {
                    builder.setParent(parentBinding);
                    binderSetMap.put(type, builder.build());
                } else {
                    // Has a superclass binding but we haven't built it yet. Re-enqueue for later.
                    entries.addLast(entry);
                }
            }
        }

        return binderSetMap;
    }

    private void parseAnnotatedMethod(Class<? extends Annotation> annotationClass, Element element,
                                      Map<TypeElement, BinderSet.Builder> builderMap, Set<TypeElement> erasedTargetNames) {

        if (!(element instanceof ExecutableElement) || element.getKind() != ElementKind.METHOD) {
            throw new IllegalStateException(
                    String.format("@%s annotation must be on a method.", annotationClass.getSimpleName()));
        }

        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        ExecutableElement executableElement = (ExecutableElement) element;
        Annotation annotation = element.getAnnotation(annotationClass);

        BinderSet.Builder builder = getOrCreateBindingBuilder(builderMap, enclosingElement);
        MethodBinder binder;

        if (annotationClass.getName().equals(PermissionAllocation.class.getName())) {
            List<String> permissions = ProcesserUtil.getAnnotationPermissions(executableElement, annotationClass);
            int methodFlag = ProcesserUtil.getAnnotationFlag(executableElement, annotationClass);
            String methodName = executableElement.getSimpleName().toString();

            checkMethod(executableElement, annotationClass);

            Parameter[] parameters = Parameter.NONE;
            binder = new MethodBinder(methodName, Arrays.asList(parameters), methodFlag, permissions);

            if (!builder.addPermissionMethod(binder)) {
                throw new MultipleMethodAddException(methodName, enclosingElement.getQualifiedName().toString(), annotationClass);
            }

        } else if (annotationClass.getName().equals(PermissionDeny.class.getName())) {
            int methodFlag = ProcesserUtil.getAnnotationFlag(executableElement, annotationClass);
            String methodName = executableElement.getSimpleName().toString();

            checkMethodHaveParameters(executableElement, annotationClass);
            Parameter[] parameters = Parameter.NONE;
            List<? extends VariableElement> methodparameters = executableElement.getParameters();
            parameters = new Parameter[methodparameters.size()];
            for (int i = 0; i < methodparameters.size(); i++) {
                VariableElement variableElement = methodparameters.get(i);
                TypeMirror typeMirror = variableElement.asType();

                parameters[i] = new Parameter(i, TypeName.get(typeMirror), variableElement.getSimpleName().toString());
            }

            binder = new MethodBinder(methodName, Arrays.asList(parameters), methodFlag, null);

            if (!builder.addPermissionDenyMethod(binder)) {
                throw new MultipleMethodAddException(methodName, enclosingElement.getQualifiedName().toString(), annotationClass);
            }
        }

        erasedTargetNames.add(enclosingElement);
    }

    private static AnnotationMirror getAnnotationMirror(Element element,
                                                        Class<? extends Annotation> annotation) {
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            if (annotationMirror.getAnnotationType().toString().equals(annotation.getCanonicalName())) {
                return annotationMirror;
            }
        }
        return null;
    }

    private BinderSet.Builder getOrCreateBindingBuilder(
            Map<TypeElement, BinderSet.Builder> builderMap, TypeElement enclosingElement) {
        BinderSet.Builder builder = builderMap.get(enclosingElement);
        if (builder == null) {
            builder = BinderSet.newBuilder(enclosingElement);
            builderMap.put(enclosingElement, builder);
        }
        return builder;
    }

    private TypeElement findParentType(TypeElement typeElement, Set<TypeElement> parents) {
        TypeMirror typeMirror;
        while (true) {
            typeMirror = typeElement.getSuperclass();
            if (typeMirror.getKind() == TypeKind.NONE) {
                return null;
            }
            typeElement = (TypeElement) ((DeclaredType) typeMirror).asElement();
            if (parents.contains(typeElement)) {
                return typeElement;
            }
        }
    }

    private void scanForRClasses(RoundEnvironment env) {
        if (trees == null)
            return;


    }

    private static class RClassScanner extends TreeScanner {
        // Maps the currently evaulated rPackageName to R Classes
        private final Map<String, Set<String>> rClasses = new LinkedHashMap<>();
        private String currentPackageName;

        @Override
        public void visitSelect(JCTree.JCFieldAccess jcFieldAccess) {
            //
            Symbol symbol = jcFieldAccess.sym;
            if (symbol != null
                    && symbol.getEnclosingElement() != null
                    && symbol.getEnclosingElement().getEnclosingElement() != null
                    && symbol.getEnclosingElement().getEnclosingElement().enclClass() != null) {
                Set<String> rClassSet = rClasses.get(currentPackageName);
                if (rClassSet == null) {
                    rClassSet = new HashSet<>();
                    rClasses.put(currentPackageName, rClassSet);
                }
                // won't add same class name repeatly.
                rClassSet.add(symbol.getEnclosingElement().getEnclosingElement().enclClass().className());
            }
        }

        Map<String, Set<String>> getRClasses() {
            return rClasses;
        }

        void setCurrentPackageName(String respectivePackageName) {
            this.currentPackageName = respectivePackageName;
        }
    }
}
