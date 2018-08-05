/**
 * Copyright 2018 bingoogolapple
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.bingoogolapple.scaffold.mvp.impl

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * 作者:王浩 邮件:bingoogolapple@gmail.com
 * 创建时间:05/08/2018 8:10 PM
 * 描述:
 */
class DefaultInvocationHandler : InvocationHandler {

    override fun invoke(proxy: Any, method: Method, args: Array<Any>): Any? {
        val returnType = method.returnType
        return when (returnType) {
            Int::class.javaPrimitiveType -> 0
            Boolean::class.javaPrimitiveType -> false
            Boolean::class.javaPrimitiveType -> false
            Byte::class.javaPrimitiveType -> 0.toByte()
            Short::class.javaPrimitiveType -> 0.toShort()
            Long::class.javaPrimitiveType -> 0.toLong()
            Char::class.javaPrimitiveType -> 0.toChar()
            Float::class.javaPrimitiveType -> 0.toFloat()
            Double::class.javaPrimitiveType -> 0.toDouble()
            String::class.javaPrimitiveType -> ""
            else -> null
        }
    }

    companion object {
        private val INSTANCE = DefaultInvocationHandler()

        fun <T> of(interfaceClass: Class<T>): T {
            return Proxy.newProxyInstance(interfaceClass.classLoader, arrayOf(interfaceClass), INSTANCE) as T
        }
    }
}