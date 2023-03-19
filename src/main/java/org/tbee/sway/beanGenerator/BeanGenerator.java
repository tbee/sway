package org.tbee.sway.beanGenerator;

import com.google.auto.service.AutoService;
import org.tbee.sway.beanGenerator.annotations.Bean;
import org.tbee.sway.beanGenerator.annotations.ListProperty;
import org.tbee.sway.beanGenerator.annotations.Property;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// https://www.baeldung.com/java-annotation-processing-builder
// https://hannesdorfmann.com/annotation-processing/annotationprocessing101/

@SupportedAnnotationTypes("org.tbee.sway.beanGenerator.annotations.Bean")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public class BeanGenerator extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        try {
            // Iterate over all to the processor registered annotations (only one)
            for (TypeElement annotation : annotations) {

                // Get all elements annotated by the current annotation
                Set<? extends Element> annotatedElements = roundEnvironment.getElementsAnnotatedWith(annotation);

                // Filter wrongly placed annotations
                List<? extends Element> annotatedClassElements = annotatedElements.stream()
                        .filter(ae -> { // Must be only class
                            boolean isClass = ae.getKind().isClass();
                            if (!isClass) {
                                processingEnv.getMessager().printMessage(Diagnostic.Kind.MANDATORY_WARNING, "The @" + Bean.class.getName() + " can only be applied to a class, not on: " + ((TypeElement) ae.getEnclosingElement()).getQualifiedName() + "." + ae, ae);
                            }
                            return isClass;
                        })
                        .toList();

                // Now process each class
                for (Element element : annotatedClassElements) {
                    Bean beanAnnotation = element.getAnnotation(Bean.class);
                    TypeElement classTypeElement = (TypeElement) element;
                    processBeanAnnotation(beanAnnotation, classTypeElement);
                }
            }
            return true; // don't process there annotations by another processor
        }
        catch (IOException e) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
        return false;
    }

    private void processBeanAnnotation(Bean beanAnnotation, TypeElement classTypeElement) throws IOException {
        Map<String, String> classContext = new HashMap<>();

        // Get the annotated class
        String annotatedClassName = classTypeElement.getQualifiedName().toString();
        classContext.put("AnnotatedClassName", annotatedClassName);

        // Derive package from annotated class
        String packageName = null;
        int lastDot = annotatedClassName.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = annotatedClassName.substring(0, lastDot);
        }
        classContext.put("packageName", packageName);
        String annotatedClassSimpleName = annotatedClassName.substring(lastDot + 1);
        classContext.put("AnnotatedClassSimpleName", annotatedClassSimpleName);

        // Derive other class names from annotated class
        String beanClassSimpleName = annotatedClassSimpleName;
        if (beanClassSimpleName.endsWith(beanAnnotation.stripSuffix())) {
            beanClassSimpleName = beanClassSimpleName.substring(0, beanClassSimpleName.length() - beanAnnotation.stripSuffix().length());
        }
        String classSimpleName = beanClassSimpleName;
        String className = packageName + "." + classSimpleName;
        beanClassSimpleName += beanAnnotation.appendSuffixToBean();
        String beanClassName = packageName + "." + beanClassSimpleName;
        classContext.put("BeanClassSimpleName", beanClassSimpleName);
        classContext.put("BeanClassName", beanClassName);
        classContext.put("ClassSimpleName", classSimpleName);
        classContext.put("ClassName", className);

        // Start writing the source file
        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Generating " + beanClassName);
        JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(beanClassName);
        try (PrintWriter writer = new PrintWriter(builderFile.openWriter())) {

            // package
            if (packageName != null) {
                writer.print(resolve(classContext, """
                         package %packageName%; 
                             
                         """));
            }

            // class declaration
            writer.print(resolve(classContext, """
                         public class %BeanClassSimpleName% extends %AnnotatedClassName% { 
                             
                         """));

            // process the class' contents
            List<VariableRecord> variableRecords = new ArrayList<>();
            for (Element element : classTypeElement.getEnclosedElements()) {
                if (element instanceof VariableElement variableElement) {
                    instanceVariable(writer, variableElement, classContext, variableRecords);
                }
            }

            // hashcode
            String variableNames = variableRecords.stream()
                    .map(vr -> vr.variableContext)
                    .map(vc -> vc.get("variableName"))
                    .collect(Collectors.joining(","));
            writer.print(resolve(classContext, """
                             @Override 
                             public int hashCode() {    
                                 return java.util.Objects.hash(%variableNames%);
                             }
                             
                         """, Map.of("variableNames", variableNames)));

            // equals
            String equals = variableRecords.stream()
                    .map(vr -> vr.variableContext)
                    .map(vc -> "java.util.Objects.equals(this." + vc.get("variableName") + ", other." + vc.get("variableName") + ")")
                    .collect(Collectors.joining("\n            && "));
            writer.print(resolve(classContext, """
                             @Override 
                             public boolean equals(Object obj) {
                                 if (this == obj) return true;
                                 if (obj == null) return false;
                                 if (getClass() != obj.getClass()) return false;
                                 %BeanClassName% other = (%BeanClassName%)obj;    
                                 return %equals%;
                             }
                             
                         """, Map.of("equals", equals)));

            // toString
            String toString = variableRecords.stream()
                    .filter(vr -> vr.propertyRecord.includeInToString())
                    .map(vr -> vr.variableContext)
                    .map(vc -> "\"," + vc.get("variableName") + "=\" + " + vc.get("variableName"))
                    .collect(Collectors.joining("\n             + "));
            writer.print(resolve(classContext, """
                             @Override 
                             public String toString() {
                                 return super.toString()
                                      + %toString%;
                             }
                             
                         """, Map.of("toString", toString)));

            // close class
            writer.println("}");
        }
    }

    private record PropertyRecord(boolean includeInToString){}
    private record VariableRecord(PropertyRecord propertyRecord, Map<String, String> variableContext){}

    private void instanceVariable(PrintWriter writer, VariableElement variableElement, Map<String, String> classContext, List<VariableRecord> variableRecords) {

        // Property annotation is required
        // TBEERNOT should we assume a default property? And introduce an ignore boolean?
        Property propertyAnnotation = variableElement.getAnnotation(Property.class);
        if (propertyAnnotation != null) {
            processPropertyAnnotation(propertyAnnotation, writer, variableElement, classContext, variableRecords);
            return;
        }
        ListProperty listPropertyAnnotation = variableElement.getAnnotation(ListProperty.class);
        if (listPropertyAnnotation != null) {
            processListPropertyAnnotation(listPropertyAnnotation, writer, variableElement, classContext, variableRecords);
            return;
        }
    }

    private void processPropertyAnnotation(Property propertyAnnotation, PrintWriter writer, VariableElement variableElement, Map<String, String> classContext, List<VariableRecord> variableRecords) {

        Map<String, String> variableContext = new HashMap<>(classContext);
        variableRecords.add(new VariableRecord(new PropertyRecord(propertyAnnotation.includeInToString()), variableContext));

        // Get info
        String variableType = variableElement.asType().toString();
        variableContext.put("VariableType", variableType);
        String bindType = determineBindType(variableElement.asType().getKind(), variableType);
        variableContext.put("BindType", bindType);
        String variableName = variableElement.getSimpleName().toString();
        variableContext.put("variableName", variableName);
        String propertyName = !propertyAnnotation.name().isBlank() ? propertyAnnotation.name() : variableName;
        variableContext.put("propertyName", propertyName);
        variableContext.put("PropertyName", firstUpper(propertyName));
        variableContext.put("PROPERTYNAME", propertyName.toUpperCase());

        writer.print(resolve(variableContext, """
                        // --------------------- 
                        // %propertyName%
                    """));
        if (propertyAnnotation.getter()) {
            writer.print(resolve(variableContext, """
                        public %VariableType% get%PropertyName%() { 
                            return this.%variableName%; 
                        }
                    """));
        }
        if (propertyAnnotation.recordStyleGetter()) {
            writer.print(resolve(variableContext, """
                        public %VariableType% %propertyName%() { 
                            return this.%variableName%; 
                        }
                    """));
        }
        if (propertyAnnotation.setter()) {
            writer.print(resolve(variableContext, """
                        public void set%PropertyName%(%VariableType% v) { 
                            fireVetoableChange("%propertyName%", this.%variableName%, v); 
                            firePropertyChange("%propertyName%", this.%variableName%, this.%variableName% = v); 
                        }
                    """));
        }
        if (propertyAnnotation.recordStyleSetter()) {
            writer.print(resolve(variableContext, """
                        public void %propertyName%(%VariableType% v) { 
                            set%PropertyName%(v);
                        }
                    """));
        }
        if (propertyAnnotation.wither()) {
            writer.print(resolve(variableContext, """
                        public %ClassName% with%PropertyName%(%VariableType% v) { 
                            set%PropertyName%(v);
                            return (%ClassName%)this;
                        }
                    """));
        }
        if (propertyAnnotation.recordStyleWither()) {
            writer.print(resolve(variableContext, """
                        public %ClassName% %propertyName%(%VariableType% v) { 
                            set%PropertyName%(v);
                            return (%ClassName%)this;
                        }
                    """));
        }
        if (propertyAnnotation.bindEndpoint()) {
            writer.print(resolve(variableContext, """
                        public org.tbee.sway.binding.BindingEndpoint<%BindType%> %propertyName%$() { 
                            return org.tbee.sway.binding.BindingEndpoint.of(this, "%propertyName%");
                        }
                    """));
        }
        if (propertyAnnotation.beanBinderEndpoint()) {
            writer.print(resolve(variableContext, """
                        static public org.tbee.sway.binding.BindingEndpoint<%BindType%> %propertyName%$(org.tbee.sway.binding.BeanBinder<%ClassName%> beanBinder) { 
                            return org.tbee.sway.binding.BindingEndpoint.of(beanBinder, "%propertyName%");
                        }
                    """));
        }
        if (propertyAnnotation.propertyNameConstant()) {
            writer.print(resolve(variableContext, """
                        final static public String %PROPERTYNAME% = \"%propertyName%\"; 
                    """));
        }
        writer.print("\n");
    }


    private void processListPropertyAnnotation(ListProperty listPropertyAnnotation, PrintWriter writer, VariableElement variableElement, Map<String, String> classContext, List<VariableRecord> variableRecords) {

        Map<String, String> variableContext = new HashMap<>(classContext);
        variableRecords.add(new VariableRecord(new PropertyRecord(listPropertyAnnotation.includeInToString()), variableContext));

        // Get info
        String variableType = variableElement.asType().toString();
        variableContext.put("VariableType", variableType);
        DeclaredType declaredType = (DeclaredType) variableElement.asType();
        String listType = declaredType.getTypeArguments().get(0).toString();
        variableContext.put("ListType", listType);
        variableContext.put("BindType", variableType); // no primaries are possible here
        String variableName = variableElement.getSimpleName().toString();
        variableContext.put("variableName", variableName);
        String propertyName = !listPropertyAnnotation.name().isBlank() ? listPropertyAnnotation.name() : variableName;
        variableContext.put("propertyName", propertyName);
        variableContext.put("PropertyName", firstUpper(propertyName));
        variableContext.put("PROPERTYNAME", propertyName.toUpperCase());
        String propertyNameSingular = !listPropertyAnnotation.nameSingular().isBlank() ? listPropertyAnnotation.nameSingular() : propertyName;
        variableContext.put("propertyNameSingular", propertyNameSingular);
        variableContext.put("PropertyNameSingular", firstUpper(propertyNameSingular));
        variableContext.put("PROPERTYNAMESingular", propertyNameSingular.toUpperCase());

        writer.print(resolve(variableContext, """
                        // --------------------- 
                        // %propertyName%
                    """));
        if (listPropertyAnnotation.getter()) {
            writer.print(resolve(variableContext, """
                        public %VariableType% get%PropertyName%() { 
                            return java.util.Collections.unmodifiableList(this.%variableName%); 
                        }
                    """));
        }
        if (listPropertyAnnotation.recordStyleGetter()) {
            writer.print(resolve(variableContext, """
                        public %VariableType% %propertyName%() { 
                            return get%PropertyName%(); 
                        }
                    """));
        }
        if (listPropertyAnnotation.adder()) {
            writer.print(resolve(variableContext, """
                        public void add%PropertyNameSingular%(%ListType% v) { 
                            var pretendedNewValue = java.util.Collections.unmodifiableList(new org.tbee.sway.beanGenerator.ListPretendingToHaveAddedItem<%ListType%>(this.%variableName%, v));
                            fireVetoableChange("%propertyName%", get%PropertyName%(), pretendedNewValue); 
                            boolean wasAdded = this.%variableName%.add(v);
                            if (wasAdded) {
                                var pretendedOldValue = java.util.Collections.unmodifiableList(new org.tbee.sway.beanGenerator.ListPretendingToHaveRemovedItem<%ListType%>(this.%variableName%, v));
                                firePropertyChange("%propertyName%", pretendedOldValue, get%PropertyName%());
                            } 
                        }
                    """));
        }
        if (listPropertyAnnotation.remover()) {
            writer.print(resolve(variableContext, """
                        public void remove%PropertyNameSingular%(%ListType% v) { 
                            var pretendedNewValue = java.util.Collections.unmodifiableList(new org.tbee.sway.beanGenerator.ListPretendingToHaveRemovedItem<%ListType%>(this.%variableName%, v));
                            fireVetoableChange("%propertyName%", get%PropertyName%(), pretendedNewValue); 
                            boolean wasRemoved = this.%variableName%.remove(v);
                            if (wasRemoved) {
                                var pretendedOldValue = java.util.Collections.unmodifiableList(new org.tbee.sway.beanGenerator.ListPretendingToHaveAddedItem<%ListType%>(this.%variableName%, v));
                                firePropertyChange("%propertyName%", pretendedOldValue, get%PropertyName%());
                            } 
                        }
                    """));
        }
        if (listPropertyAnnotation.bindEndpoint()) {
            writer.print(resolve(variableContext, """
                        public org.tbee.sway.binding.BindingEndpoint<%BindType%> %propertyName%$() { 
                            return org.tbee.sway.binding.BindingEndpoint.of(this, "%propertyName%");
                        }
                    """));
        }
        if (listPropertyAnnotation.beanBinderEndpoint()) {
            writer.print(resolve(variableContext, """
                        static public org.tbee.sway.binding.BindingEndpoint<%BindType%> %propertyName%$(org.tbee.sway.binding.BeanBinder<%ClassName%> beanBinder) { 
                            return org.tbee.sway.binding.BindingEndpoint.of(beanBinder, "%propertyName%");
                        }
                    """));
        }
        if (listPropertyAnnotation.propertyNameConstant()) {
            writer.print(resolve(variableContext, """
                        final static public String %PROPERTYNAME% = \"%propertyName%\"; 
                    """));
        }
        writer.print("\n");
    }


    private static String determineBindType(TypeKind typeKind, String nonPrimaryType) {
        String bindType = switch(typeKind) {
            case BOOLEAN -> Boolean.class.getName();
            case BYTE -> Byte.class.getName();
            case SHORT -> Short.class.getName();
            case INT -> Integer.class.getName();
            case LONG -> Long.class.getName();
            case CHAR -> Character.class.getName();
            case FLOAT -> Float.class.getName();
            case DOUBLE -> Double.class.getName();
            default -> nonPrimaryType;
        };
        return bindType;
    }

    private String resolve(Map<String, String> context, String template) {
        return resolve(context, template, Map.of());
    }
    private String resolve(Map<String, String> context, String template, Map<String, String> quickContext) {
        context = new HashMap<>(context);
        context.putAll(quickContext);
        for (String key : context.keySet()) {
            String value = context.get(key);
            template = template.replace("%" + key + "%", value);
        }
        return template;
    }

    private String firstUpper(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private String firstLower(String s) {
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }
}
