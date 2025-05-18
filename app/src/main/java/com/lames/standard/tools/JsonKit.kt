@file:JvmName("JsonKit")

package com.lames.standard.tools

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


val instance: Gson = Gson()

@Throws(JsonSyntaxException::class)
inline fun <reified T> parseToObject(json: String): T = instance.fromJson<T>(json, T::class.java)

@Throws(JsonSyntaxException::class)
inline fun <reified T> parseToObject(element: JsonElement): T =
    instance.fromJson(element, T::class.java)

@Throws(JsonSyntaxException::class)
fun parseToJson(obj: Any): String = instance.toJson(obj)

@Throws(JsonSyntaxException::class)
inline fun <reified T> parseToList(json: String): ArrayList<T> =
    parseToList(JsonParser.parseString(json))

@Throws(JsonSyntaxException::class)
inline fun <reified T> parseToList(json: String, itemClass: Class<T>): ArrayList<T> =
    instance.fromJson(json, ParameterizedTypeImpl(itemClass))

@Throws(JsonSyntaxException::class)
inline fun <reified T> parseToList(element: JsonElement): ArrayList<T> {
    val array = element.asJsonArray
    val list: ArrayList<T> = ArrayList()
    array.forEach { list.add(parseToObject(it)) }
    return list
}

class ParameterizedTypeImpl(var clazz: Class<*>) : ParameterizedType {
    override fun getActualTypeArguments(): Array<Type> = arrayOf(clazz)
    override fun getRawType(): Type = MutableList::class.java
    override fun getOwnerType(): Type? = null
}