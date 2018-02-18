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
import com.github.javaparser.ast.body.ConstructorDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import com.github.javaparser.ast.body.ModifierSet
import com.github.javaparser.ast.type.ReferenceType

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
        it.type.toString() == name && it.parameters?.size ?: 0 > 0
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
                && it.parameters[0].type.toString() == getParentClass()!!.name
                && it.modifiers.and(ModifierSet.PUBLIC) != 0
    }
}

private fun ClassOrInterfaceDeclaration.getParentStaticFactory(): MethodDeclaration? {
    return getParentClass()!!.getMethods().firstOrNull {
        it.hasParameters(0)
                && it.name == "newBuilder"
    }
}

private fun ClassOrInterfaceDeclaration.getParentCopyStaticFactory(): MethodDeclaration? {
    return getParentClass()!!.getMethods().firstOrNull {
        it.hasParameters(1)
                && it.parameters[0].type.toString().split('.').last() == getParentClass()!!.name
                && it.name == "newBuilder"
    }
}

fun ClassOrInterfaceDeclaration.getBuildMethod(): MethodDeclaration? {
    return getMethods().firstOrNull { it.isBuildMethod() }
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
    return getDefaultCtor()?.name ?: getParentStaticFactory()!!.name
}

private fun ClassOrInterfaceDeclaration.builderGetCopyCtor(): String? {
    return getCopyCtor()?.name ?: getParentCopyStaticFactory()?.name
}

fun ClassOrInterfaceDeclaration.getCreator(): String {
    assertIsBuilder()
    val parent = parentNode as ClassOrInterfaceDeclaration
    val type = parent.name
    val newBuilder = builderGetCtor()
    return """public inline fun build$type(fn: $type.Builder.() -> Unit): $type {
    val builder = $type.$newBuilder()
    builder.fn()
    return builder.build()
}"""
}

fun ClassOrInterfaceDeclaration.getRebuild(): String? {
    assertIsBuilder()
    val parent = parentNode as ClassOrInterfaceDeclaration
    val type = parent.name
    val newBuilder = builderGetCopyCtor() ?: return null
    return """public inline fun $type.rebuild(fn: $type.Builder.() -> Unit): $type {
    val builder = $type.$newBuilder(this)
    builder.fn()
    return builder.build()
}"""
}

fun ClassOrInterfaceDeclaration.getMethodStrings(): List<String> {
    val retval = arrayListOf(getCreator())
    val rebuild = getRebuild()
    if (rebuild != null) retval.add(rebuild)
//    This would enable k-combinator syntax for every property in the builder. We can re-enable if people want it.
//    retval.addAll(getBuilderMethods().flatMap { it.toKotlin(config) })
    return retval
}