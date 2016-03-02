package com.levelmoney.kbuilders.test.builders

import com.levelmoney.kbuilders.test.builders.*
import com.levelmoney.kbuilders.test.builders.NestedParentObject
import com.levelmoney.kbuilders.test.builders.NestedParentObject.Builder
import com.levelmoney.kbuilders.test.builders.NestedParentObject.ChildObject

inline fun buildNestedParentObject(fn: NestedParentObject.Builder.() -> Unit): NestedParentObject = NestedParentObject.Builder().apply(fn).build()
inline fun NestedParentObject.Builder.child(fn: () -> ChildObject): NestedParentObject.Builder = child(fn())
inline fun buildChildObject(fn: ChildObject.Builder.() -> Unit): ChildObject = ChildObject.Builder().apply(fn).build()
inline fun ChildObject.Builder.value(fn: () -> Int): ChildObject.Builder = value(fn())
