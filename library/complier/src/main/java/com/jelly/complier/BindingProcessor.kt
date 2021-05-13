package com.jelly.complier
import com.google.auto.service.AutoService
import com.jelly.annotation.OnClick
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import java.io.IOException
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements


@AutoService(Processor::class)
class BindingProcessor: AbstractProcessor() {
    private var mFiler: Filer? = null
    private var mElements: Elements? = null
    private var mMessage: Messager? = null
    override fun init(processingEnv: ProcessingEnvironment){
        super.init(processingEnv)
        mFiler = processingEnv.filer
        mElements = processingEnv.elementUtils
        mMessage = processingEnv.messager
    }
    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {
        // To change body of created functions use File | Settings | File Templates.
        println("======>start")

        /** * 定义了方法 */
        val main = MethodSpec.methodBuilder("printHello")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(Void.TYPE)
                .addParameter(Array<String>::class.java, "args")
                .addStatement("\$T.out.println(\$S)", System::class.java, "Hello, Kotlin!")
                .build()
        /** * 定义类 */
        val helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(main)
                .build()

        /** * 定义包名 */
        val javaFile = JavaFile.builder("com.knight.apt", helloWorld)
                .build()

        try {
            javaFile.writeTo(processingEnv.filer)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        println("=======>end")
        return false
    }
    override fun getSupportedSourceVersion(): SourceVersion? {
        return SourceVersion.latestSupported()
    }

    override fun getSupportedAnnotationTypes(): Set<String>? {
        val types: MutableSet<String> = LinkedHashSet()
        types.add(OnClick::class.java.canonicalName)// 声明使用的注解
        return types
    }
}