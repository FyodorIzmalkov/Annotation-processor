import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;

public class Main {
    private static String HOME = System.getProperty("user.home");
    public static void main(String[] args) throws IOException {
        System.out.println(HOME);
        MethodSpec today = MethodSpec.methodBuilder("today")
                .returns(Date.class)
                .addStatement("return new $T()", Date.class)
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(today)
                .build();

        JavaFile javaFile = JavaFile.builder("main.com.example.helloworld", helloWorld)
                .build();

        javaFile.writeTo(Paths.get("src"));
    }
}
