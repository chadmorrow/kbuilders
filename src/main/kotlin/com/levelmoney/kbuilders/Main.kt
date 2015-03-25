package com.levelmoney.wire.kbuilders

import com.github.javaparser.JavaParser
import com.github.javaparser.ast.PackageDeclaration
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import java.io.File

/**
 * Created by Aaron Sarazan on 3/22/15
 * Copyright(c) 2015 Level, Inc.
 */

fun main(args : Array<String>) {
    val pakage = args.first()
    val files = arrayListOf<File>()
    for (i in 1..(args.size() - 1)) {
        files.add(File(args[i]))
    }
    print(generate(pakage, files))
}


/**
 * Generates a full kotlin file from the provided java files.
 */
public fun generate(pakage: String, files: List<File>): String {
    return generateFileMap(pakage, files).toString()
}

private fun generateFileMap(pakage: String, files: List<File>): FileMap {
    return FileMap(pakage, files.flatMap { fileToKlasses (it) }.toArrayList())
}

private fun fileToKlasses(file: File): MutableList<Klass> {
    val cu = JavaParser.parse(file)
    val retval = arrayListOf<Klass>()
    Visitor().visit(cu, retval)
    return retval
}

private data class FileMap(val pakage: String, val klasses: MutableList<Klass>) {

    override fun toString(): String =
            """package $pakage

${klasses.map { "import ${it.import}.${it.type}" }.join("\n")}
${klasses.map {
                it.factoryMethod() + "\n" +
                        it.methods.map { it.toString () }.join("\n")
            }.join("\n")}
"""
}

private data class Klass(val import: String, val type: String, val methods: List<Decl>) {
    public fun factoryMethod(): String =
            """
public fun ${$type.camelCase()}(fn: $type.Builder.() -> Unit): $type {
    val builder = $type.Builder()
    builder.fn()
    return builder.build()
}
"""
}

private fun String.camelCase(): String {
    return replaceRange(0, 1, get(0).toString().toLowerCase())
}

private data class Decl(val type: String, val enclosing: String, val name: String) {
    override fun toString(): String = "public fun $enclosing.$name(fn: () -> $type): $enclosing = $name(fn())"
}

private class Visitor : VoidVisitorAdapter<MutableList<Klass>>() {

    private var pakage: String? = null
    override fun visit(n: PackageDeclaration, arg: MutableList<Klass>?) {
        super.visit(n, arg)
        pakage = n.getName().toString()
    }

    override fun visit(n: ClassOrInterfaceDeclaration, arg: MutableList<Klass>) {
        val decls = arrayListOf<Decl>()
        n.getMembers().forEach {
            if (it is ClassOrInterfaceDeclaration) {
                if (it.getName().equals("Builder")) {
                    BuilderVisitor(n.getName()).visit(it, decls)
                } else {
                    Visitor().visit(it, arg)
                }
            }
        }
        arg.add(Klass(pakage!!, n.getName(), decls))
    }
}

private class BuilderVisitor(val type: String) : VoidVisitorAdapter<MutableList<Decl>>() {

    override fun visit(n: MethodDeclaration, arg: MutableList<Decl>) {
        val enclosing = type + ".Builder"
        val name = n.getName()
        val type = n.getParameters()?.firstOrNull()?.getType()?.toString()?:return
        arg.add(Decl(type, enclosing, name))
    }

}