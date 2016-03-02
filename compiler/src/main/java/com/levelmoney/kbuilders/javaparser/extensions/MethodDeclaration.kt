/*
 * Copyright 2015 Level Money, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.levelmoney.kbuilders.javaparser.extensions

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.levelmoney.kbuilders.Config

fun MethodDeclaration.hasParameters(count: Int? = null): Boolean {
    val params = parameters ?: return count == 0
    return count == null || params.size == count
}

fun MethodDeclaration.isBuildMethod():Boolean {
    return name.equals("build") && !type.toString().equals("void")
}

fun MethodDeclaration.toKotlin(config: Config): List<String> {
    val retval = arrayListOf(baseKotlin(config))
    // Once we can reliably link TypeParameter <-> ClassOrInterfaceDeclaration, we can add a shortcut method here.
    return retval
}

fun MethodDeclaration.baseKotlin(config: Config): String {
    assertIsBuilder()
    val builderClass = getClassOrInterface()
    val enclosing = builderClass.getTypeForThisBuilder()
    val builderName = builderClass.name
    val type = kotlinifyType(parameters.first().type.toString())
    val name = name
    val inline = if (config.inline) "inline " else ""
    return "${inline}fun $enclosing.$builderName.${config.methodPrefix}$name(fn: () -> $type): $enclosing.$builderName = $name(fn())"
}

fun MethodDeclaration.builderKotlin(config: Config): String {
    assertIsBuilder()
    val builderClass = getClassOrInterface()
    val enclosing = builderClass.getTypeForThisBuilder()
    val builderName = builderClass.name
    val type = kotlinifyType(parameters.first().type.toString())
    val name = name
    val inline = if (config.inline) "inline " else ""
    return "${inline}fun $enclosing.$builderName.${config.methodPrefix}${name}Built(fn: $type.$builderName.() -> $type.$builderName): $enclosing.$builderName = $name(build$type{fn()})"
}

fun MethodDeclaration.getClassOrInterface(): ClassOrInterfaceDeclaration {
    return parentNode as ClassOrInterfaceDeclaration
}

fun kotlinifyType(type: String): String {
    return when (type) {
        "byte" -> "Byte"
        "short" -> "Short"
        "int", "Integer" -> "Int"
        "long" -> "Long"
        "float" -> "Float"
        "double" -> "Double"
        "boolean" -> "Boolean"
        "char" -> "Char"
        else -> kotlinifyOther(type)
    }
}

fun kotlinifyOther(type: String): String {
    return when {
        type.endsWith("[]") -> kotlinifyType(type.replace("[]", "")) + "Array"
        else -> type
    }
}

fun MethodDeclaration.assertIsBuilder() {
    getClassOrInterface().assertIsBuilder()
}
