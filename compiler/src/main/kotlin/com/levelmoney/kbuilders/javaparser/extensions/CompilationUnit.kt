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

import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.levelmoney.kbuilders.javaparser.adapters.ClassNameCollectorVisitor
import com.levelmoney.kbuilders.javaparser.adapters.GetBuilderVisitor

fun CompilationUnit.getBuilders(): List<ClassOrInterfaceDeclaration> {
    val retval = arrayListOf<ClassOrInterfaceDeclaration>()
    GetBuilderVisitor().visit(this, retval)
    return retval
}

fun CompilationUnit.getRequiredImports(): List<String> {
    val retval = arrayListOf(`package`.name.toString() + ".*")
    ClassNameCollectorVisitor(`package`.name.toString()).visit(this, retval)
    retval.addAll(imports?.map { it.name.toString() }?:listOf())
    return retval.filterImports()
}

private fun List<String>.filterImports(): List<String> {
    return filter {
        !(it.startsWith("java."))
    }
}
