package com.levelmoney.kbuilders.test.builders

import com.levelmoney.kbuilders.test.builders.*
import com.levelmoney.kbuilders.test.builders.Person
import com.levelmoney.kbuilders.test.builders.Person.Builder

inline fun Person.rebuild(fn: Person.Builder.() -> Unit): Person = Person.Builder(this@rebuild).apply(fn).build()
inline fun Person.Builder.firstName(fn: () -> String): Person.Builder = firstName(fn())
