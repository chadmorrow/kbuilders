package com.levelmoney.kbuilders.test.builders

import com.levelmoney.kbuilders.test.builders.*
import com.levelmoney.kbuilders.test.builders.ParentObject
import com.levelmoney.kbuilders.test.builders.ParentObject.Builder

inline fun buildParentObject(fn: ParentObject.Builder.() -> Unit): ParentObject = ParentObject.Builder().apply(fn).build()
inline fun ParentObject.Builder.child(fn: () -> BasicObject): ParentObject.Builder = child(fn())
