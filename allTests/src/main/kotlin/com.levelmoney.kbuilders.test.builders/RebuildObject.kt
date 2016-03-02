package com.levelmoney.kbuilders.test.builders

import com.levelmoney.kbuilders.test.builders.*
import com.levelmoney.kbuilders.test.builders.RebuildObject
import com.levelmoney.kbuilders.test.builders.RebuildObject.Builder

inline fun buildRebuildObject(fn: RebuildObject.Builder.() -> Unit): RebuildObject = RebuildObject.Builder().apply(fn).build()
inline fun RebuildObject.rebuild(fn: RebuildObject.Builder.() -> Unit): RebuildObject = RebuildObject.Builder(this@rebuild).apply(fn).build()
inline fun RebuildObject.Builder.value(fn: () -> Int): RebuildObject.Builder = value(fn())
