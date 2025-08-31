/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.mozilla.javascript;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <code>NativeJavaList</code> is a wrapper for java objects implementing <code>java.util.List
 * </code> interface. This wrapper delegates index based access in javascript (like <code>
 * value[x] = 3</code>) to the according {@link List#get(int)}, {@link List#set(int, Object)} and
 * {@link List#add(Object)} methods. This allows you to use java lists in many places like a
 * javascript <code>Array</code>.
 *
 * <p>Supported functions:
 *
 * <ul>
 *   <li>index based access is delegated to List.get/set/add. If <code>index &gt;= length</code>,
 *       the skipped elements will be filled with <code>null</code> values
 *   <li>iterator support with <code>for...of</code> (provided by NativeJavaObject for all
 *       iterables)
 *   <li>when iterating with <code>for .. in</code> (or <code>for each .. in</code>) then <code>
 *       getIds
 *       </code> + index based access is used.
 *   <li>reading and setting <code>length</code> property. When modifying the length property, the
 *       list is either truncated or will be filled with <code>null</code> values up to <code>length
 *       </code>
 *   <li>deleting entries: <code>delete value[index]</code> will be equivalent with <code>
 *       value[index] = null</code> and is implemented to provide array compatibility.
 * </ul>
 *
 * <b>Important:</b> JavaList does not support sparse arrays. So setting the length property to a
 * high value or writing to a high index may allocate a lot of memory.
 *
 * <p><b>Note:</b> Although <code>JavaList</code> looks like a javascript-<code>Array</code>, it is
 * not an <code>
 * Array</code>. Some methods behave very similar like <code>Array.indexOf</code> and <code>
 * java.util.List.indexOf</code>, others are named differently like <code>Array.includes</code> vs.
 * <code>java.util.List.contains</code>. Especially <code>forEach</code> is different in <code>Array
 * </code> and <code>java.util.List</code>. Also deleting entries will set entries to <code>null
 * </code> instead to <code>Undefined</code>
 */
public class NativeJavaList extends NativeJavaObject implements SymbolScriptable {
    private static final long serialVersionUID = -924027554283675333L;

    @Override
    public String getClassName() {
        return "JavaArray";
    }

    public static NativeJavaList wrap(Scriptable scope, Object array) {
        return new NativeJavaList(scope, array);
    }

    @Override
    public Object unwrap() {
        return list;
    }

    public NativeJavaList(Scriptable scope, Object list) {
        super(scope, null, list.getClass());
        if (!(list instanceof List)) {
            throw new RuntimeException("Array expected");
        }
        //noinspection unchecked
        this.list = (List<Object>) list;
    }

    @Override
    public boolean has(String id, Scriptable start) {
        return id.equals("length") || super.has(id, start);
    }

    @Override
    public boolean has(int index, Scriptable start) {
        return 0 <= index && index < list.size();
    }

    @Override
    public boolean has(Symbol key, Scriptable start) {
        return SymbolKey.IS_CONCAT_SPREADABLE.equals(key);
    }

    @Override
    public Object get(String id, Scriptable start) {
        if (id.equals("length"))
            return list.size();
        Object result = super.get(id, start);
        if (result == NOT_FOUND &&
                !ScriptableObject.hasProperty(getPrototype(), id)) {
            throw Context.reportRuntimeError2(
                    "msg.java.member.not.found", list.getClass().getName(), id);
        }
        return result;
    }

    @Override
    public Object get(int index, Scriptable start) {
        if (0 <= index && index < list.size()) {
            Context cx = Context.getContext();
            Object element = list.get(index);
            return cx.getWrapFactory().wrap(cx, this, element, null);
        }
        return Undefined.instance;
    }

    @Override
    public Object get(Symbol key, Scriptable start) {
        if (SymbolKey.IS_CONCAT_SPREADABLE.equals(key)) {
            return true;
        }
        return Scriptable.NOT_FOUND;
    }

    @Override
    public void put(String id, Scriptable start, Object value) {
        // Ignore assignments to "length"--it's readonly.
        if (!id.equals("length"))
            throw Context.reportRuntimeError1(
                    "msg.java.array.member.not.found", id);
    }

    @Override
    public void put(int index, Scriptable start, Object value) {
        for (int i = list.size(); i <= index; i++) {
            list.add(null);
        }

        if (0 <= index) {
            list.set(index, Context.jsToJava(value, Object.class));
        } else {
            throw Context.reportRuntimeError2(
                    "msg.java.array.index.out.of.bounds", String.valueOf(index),
                    String.valueOf(list.size() - 1));
        }
    }

    @Override
    public void delete(Symbol key) {
        // All symbols are read-only
    }

    @Override
    public Object getDefaultValue(Class<?> hint) {
        if (hint == null || hint == ScriptRuntime.StringClass)
            return Arrays.deepToString(list.toArray(new Object[0]));
        if (hint == ScriptRuntime.BooleanClass)
            return Boolean.TRUE;
        if (hint == ScriptRuntime.NumberClass)
            return ScriptRuntime.NaNobj;
        return this;
    }

    @Override
    public Object[] getIds() {
        Object[] result = new Object[list.size()];
        int i = result.length;
        while (--i >= 0)
            result[i] = i;
        return result;
    }

    @Override
    public Scriptable getPrototype() {
        if (prototype == null) {
            prototype =
                    ScriptableObject.getArrayPrototype(this.getParentScope());
        }
        return prototype;
    }

    List<Object> list;
}
