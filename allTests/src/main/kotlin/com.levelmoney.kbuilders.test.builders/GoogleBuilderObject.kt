package com.levelmoney.kbuilders.test.builders

import com.levelmoney.kbuilders.test.builders.*
import com.levelmoney.kbuilders.test.builders.GoogleBuilderObject
import com.levelmoney.kbuilders.test.builders.GoogleBuilderObject.Builder

inline fun buildGoogleBuilderObject(fn: GoogleBuilderObject.Builder.() -> Unit): GoogleBuilderObject = GoogleBuilderObject.newBuilder().apply(fn).build()
inline fun GoogleBuilderObject.rebuild(fn: GoogleBuilderObject.Builder.() -> Unit): GoogleBuilderObject = GoogleBuilderObject.newBuilder(this@rebuild).apply(fn).build()
inline fun GoogleBuilderObject.Builder.value(fn: () -> Int): GoogleBuilderObject.Builder = value(fn())
