package com.levelmoney.kbuilders.test.builders

import com.levelmoney.kbuilders.test.builders.*
import com.levelmoney.kbuilders.test.builders.Wire2Object
import com.levelmoney.kbuilders.test.builders.Wire2Object.Builder

inline fun buildWire2Object(fn: Wire2Object.Builder.() -> Unit): Wire2Object = Wire2Object.Builder().apply(fn).build()
inline fun Wire2Object.rebuild(fn: Wire2Object.Builder.() -> Unit): Wire2Object = newBuilder().apply(fn).build()
inline fun Wire2Object.Builder.value(fn: () -> Int): Wire2Object.Builder = value(fn())
