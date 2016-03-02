package com.levelmoney.kbuilders.test.builders

import com.levelmoney.kbuilders.test.builders.*
import com.levelmoney.kbuilders.test.builders.BasicObject
import com.levelmoney.kbuilders.test.builders.BasicObject.Builder

inline fun buildBasicObject(fn: BasicObject.Builder.() -> Unit): BasicObject = BasicObject.Builder().apply(fn).build()
inline fun BasicObject.Builder.value(fn: () -> Int): BasicObject.Builder = value(fn())
