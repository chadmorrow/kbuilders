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

import com.github.javaparser.ast.body.*
import com.github.javaparser.ast.type.ReferenceType
import com.levelmoney.kbuilders.Config
import com.levelmoney.kbuilders.javaparser.extensions.hasParameters

fun ClassOrInterfaceDeclaration.getInternalClasses(): List<ClassOrInterfaceDeclaration> {
    return members.filterIsInstance<ClassOrInterfaceDeclaration>()
}

fun ClassOrInterfaceDeclaration.getParentClass(): ClassOrInterfaceDeclaration? {
    return parentNode as ClassOrInterfaceDeclaration
}

// This could return false positives but should be adequate for v1.0
fun ClassOrInterfaceDeclaration.getBuilderClass(): ClassOrInterfaceDeclaration? {
    return getInternalClasses().firstOrNull { it.isBuilder() }
}

fun ClassOrInterfaceDeclaration.getBuilderMethods(): List<MethodDeclaration> {
    return getMethods().filter {
        it.type.toString().equals(name) && it.parameters?.size?:0 > 0
    }
}

fun ClassOrInterfaceDeclaration.getDefaultCtor(): ConstructorDeclaration? {
    return getConstructors().firstOrNull {
        it.hasParameters(0) && it.modifiers.and(ModifierSet.PUBLIC) != 0
    }
}

fun ClassOrInterfaceDeclaration.getCopyCtor(): ConstructorDeclaration? {
    return getConstructors().firstOrNull {
        it.hasParameters(1)
                && it.parameters[0].type.toString().equals(getParentClass()!!.name)
                && it.modifiers.and(ModifierSet.PUBLIC) != 0
    }
}

private fun ClassOrInterfaceDeclaration.getParentStaticFactory(): MethodDeclaration? {
    return getParentClass()!!.getMethods().firstOrNull {
        it.hasParameters(0)
                && it.name.equals("newBuilder")
    }
}

private fun ClassOrInterfaceDeclaration.getParentCopyStaticFactory(): MethodDeclaration? {
    return getParentClass()!!.getMethods().firstOrNull {
        it.hasParameters(1)
                && it.parameters[0].type.toString().equals(getParentClass()!!.name)
                && it.name.equals("newBuilder")
    }
}

fun ClassOrInterfaceDeclaration.getBuildMethod(): MethodDeclaration? {
    return getMethods().firstOrNull { it.isBuildMethod () }
}

fun ClassOrInterfaceDeclaration.isBuilder(): Boolean {
    return getBuildMethod() != null
}

fun ClassOrInterfaceDeclaration.getTypeForThisBuilder(): ReferenceType {
    return getBuildMethod()!!.type as ReferenceType
}

fun ClassOrInterfaceDeclaration.getMethods(): List<MethodDeclaration> {
    return members.filterIsInstance<MethodDeclaration>()
}

fun ClassOrInterfaceDeclaration.getConstructors(): List<ConstructorDeclaration> {
    return members.filterIsInstance<ConstructorDeclaration>()
}

fun ClassOrInterfaceDeclaration.assertIsBuilder() {
    if (!isBuilder()) throw IllegalStateException()
}

/**
 * Run on the Builder class. If it has a private constructor it will search
 * the parent for a newBuilder() method.
 *
 * Thanks Google...
 */
private fun ClassOrInterfaceDeclaration.builderGetCtor(): String {
    return getDefaultCtor()?.name ?:getParentStaticFactory()!!.name
}
private fun ClassOrInterfaceDeclaration.builderGetCopyCtor(): String? {
    return getCopyCtor()?.name ?:getParentCopyStaticFactory()?.name
}

fun ClassOrInterfaceDeclaration.getCreator(config: Config): String {
    assertIsBuilder()
    val parent = parentNode as ClassOrInterfaceDeclaration
    val type = parent.name
    val inline = if (config.inline) "inline " else ""
    val newBuilder = builderGetCtor()
    return """${inline}fun build$type(fn: $type.Builder.() -> Unit): $type = $type.$newBuilder().apply(fn).build()"""
}

fun ClassOrInterfaceDeclaration.getRebuild(config: Config): String? {
    assertIsBuilder()
    val parent = parentNode as ClassOrInterfaceDeclaration
    val type = parent.name
    val inline = if (config.inline) "inline " else ""
    val newBuilder = builderGetCopyCtor() ?: return null
    return """${inline}fun $type.rebuild(fn: $type.Builder.() -> Unit): $type = $type.$newBuilder(this).apply(fn).build()""""""
}

fun ClassOrInterfaceDeclaration.getMethodStrings(config: Config): List<String> {
    val retval = arrayListOf(getCreator(config))
    val rebuild = getRebuild(config)
    if (rebuild != null) retval.add(rebuild)
    retval.addAll(getBuilderMethods().flatMap { it.toKotlin(config) })
    return retval
}