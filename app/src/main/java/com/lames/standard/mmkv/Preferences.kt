package com.lames.standard.mmkv

import android.content.SharedPreferences
import android.os.Parcelable
import androidx.core.content.edit
import com.tencent.mmkv.MMKV
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


//region mmkv
private inline fun <T> MMKV.delegate(
    key: String? = null,
    defaultValue: T,
    crossinline getter: MMKV.(String, T) -> T,
    crossinline setter: MMKV.(String, T) -> Boolean
): ReadWriteProperty<Any, T> = object : ReadWriteProperty<Any, T> {
    override fun getValue(thisRef: Any, property: KProperty<*>): T =
        getter(key ?: property.name, defaultValue)

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        setter(key ?: property.name, value)
    }
}

private inline fun <T> MMKV.nullableDefaultValueDelegate(
    key: String? = null,
    defaultValue: T?,
    crossinline getter: MMKV.(String, T?) -> T,
    crossinline setter: MMKV.(String, T) -> Boolean
): ReadWriteProperty<Any, T> = object : ReadWriteProperty<Any, T> {
    override fun getValue(thisRef: Any, property: KProperty<*>): T =
        getter(key ?: property.name, defaultValue)

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        setter(key ?: property.name, value)
    }
}

fun MMKV.boolean(
    key: String? = null,
    defaultValue: Boolean = false
): ReadWriteProperty<Any, Boolean> =
    delegate(key, defaultValue, MMKV::decodeBool, MMKV::encode)

fun MMKV.int(key: String? = null, defaultValue: Int = 0): ReadWriteProperty<Any, Int> =
    delegate(key, defaultValue, MMKV::decodeInt, MMKV::encode)

fun MMKV.long(key: String? = null, defaultValue: Long = 0L): ReadWriteProperty<Any, Long> =
    delegate(key, defaultValue, MMKV::decodeLong, MMKV::encode)

fun MMKV.float(key: String? = null, defaultValue: Float = 0.0F): ReadWriteProperty<Any, Float> =
    delegate(key, defaultValue, MMKV::decodeFloat, MMKV::encode)

fun MMKV.double(key: String? = null, defaultValue: Double = 0.0): ReadWriteProperty<Any, Double> =
    delegate(key, defaultValue, MMKV::decodeDouble, MMKV::encode)

fun MMKV.byteArray(
    key: String? = null,
    defaultValue: ByteArray? = null
): ReadWriteProperty<Any, ByteArray?> =
    nullableDefaultValueDelegate(key, defaultValue, MMKV::decodeBytes, MMKV::encode)

fun MMKV.string(
    key: String? = null,
    defaultValue: String? = null
): ReadWriteProperty<Any, String?> =
    nullableDefaultValueDelegate(key, defaultValue, MMKV::decodeString, MMKV::encode)

fun MMKV.stringSet(
    key: String? = null,
    defaultValue: Set<String>? = null
): ReadWriteProperty<Any, Set<String>?> =
    nullableDefaultValueDelegate(key, defaultValue, MMKV::decodeStringSet, MMKV::encode)

inline fun <reified T : Parcelable> MMKV.parcelable(
    key: String? = null,
    defaultValue: T?
): ReadWriteProperty<Any, T> =
    object : ReadWriteProperty<Any, T> {
        override fun getValue(thisRef: Any, property: KProperty<*>): T =
            decodeParcelable(key ?: property.name, T::class.java, defaultValue)!!

        override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
            encode(key ?: property.name, value)
        }
    }
//endregion

//region shared preference
private inline fun <T> SharedPreferences.spDelegate(
    key: String? = null,
    defaultValue: T,
    crossinline getter: SharedPreferences.(String, T) -> T,
    crossinline setter: SharedPreferences.(String, T) -> Unit
): ReadWriteProperty<Any, T> = object : ReadWriteProperty<Any, T> {
    override fun getValue(thisRef: Any, property: KProperty<*>): T =
        getter(key ?: property.name, defaultValue)

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        setter(key ?: property.name, value)
    }
}

private inline fun <T> SharedPreferences.spNullableDefaultValueDelegate(
    key: String,
    defaultValue: T?,
    crossinline getter: SharedPreferences.(String, T?) -> T,
    crossinline setter: SharedPreferences.(String, T?) -> Unit
): ReadWriteProperty<Any, T?> = object : ReadWriteProperty<Any, T?> {
    override fun getValue(thisRef: Any, property: KProperty<*>): T? = getter(key, defaultValue)

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
        setter(key, value)
    }
}

fun SharedPreferences.spBoolean(
    key: String,
    defaultValue: Boolean = false
): ReadWriteProperty<Any, Boolean> =
    spDelegate(
        key,
        defaultValue,
        SharedPreferences::getBoolean
    ) { _, value -> edit { putBoolean(key, value) } }

fun SharedPreferences.spInt(key: String, defaultValue: Int = 0): ReadWriteProperty<Any, Int> =
    spDelegate(key, defaultValue, SharedPreferences::getInt) { _, value ->
        edit {
            putInt(
                key,
                value
            )
        }
    }

fun SharedPreferences.spLong(key: String, defaultValue: Long = 0L): ReadWriteProperty<Any, Long> =
    spDelegate(key, defaultValue, SharedPreferences::getLong) { _, value ->
        edit {
            putLong(
                key,
                value
            )
        }
    }

fun SharedPreferences.spFloat(
    key: String,
    defaultValue: Float = 0.0F
): ReadWriteProperty<Any, Float> =
    spDelegate(key, defaultValue, SharedPreferences::getFloat) { _, value ->
        edit {
            putFloat(
                key,
                value
            )
        }
    }

fun SharedPreferences.spString(
    key: String,
    defaultValue: String? = null
): ReadWriteProperty<Any, String?> =
    spNullableDefaultValueDelegate(
        key,
        defaultValue,
        SharedPreferences::getString
    ) { _, value -> edit { putString(key, value) } }
//endregion