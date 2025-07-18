package io.dontsayboj.mako.ui

import com.intellij.DynamicBundle
import org.jetbrains.annotations.PropertyKey

object Bundle : DynamicBundle("messages.MessagesBundle") {
    fun message(@PropertyKey(resourceBundle = "messages.MessagesBundle") key: String, vararg params: Any): String {
        return getMessage(key, *params)
    }
}