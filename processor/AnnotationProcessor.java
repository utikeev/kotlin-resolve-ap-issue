package processor;

import api.MyAnnotation;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

@SupportedAnnotationTypes("api.MyAnnotation")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class AnnotationProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> annotated = roundEnv.getElementsAnnotatedWith(MyAnnotation.class);
        for (Element element : annotated) {
            PackageElement enclosing = getPackageElement(element);

            StringBuilder builder = new StringBuilder();
            builder.append("package ")
                    .append(enclosing.getQualifiedName().toString())
                    .append(";\n\n")
                    .append("import api.AbstractModule;\n")
                    .append("\n")
                    .append("public final class Generated")
                    .append(element.getSimpleName())
                    .append(" extends AbstractModule")
                    .append(" ")
                    .append("{\n")
                    .append("  @Override\n")
                    .append("  protected String getName() {")
                    .append("    return \"")
                    .append(element.getSimpleName())
                    .append("\";\n")
                    .append("  }\n")
                    .append("}\n");

            String implName = enclosing.getQualifiedName() + ".Generated" + element.getSimpleName();
            try {
                JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(implName);
                try (PrintWriter out = new PrintWriter(sourceFile.openWriter())) {
                    out.print(builder);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    private PackageElement getPackageElement(Element element) {
        while (!(element instanceof PackageElement)) {
            element = element.getEnclosingElement();
        }
        return (PackageElement) element;
    }
}
