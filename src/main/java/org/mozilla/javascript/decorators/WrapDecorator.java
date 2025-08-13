package org.mozilla.javascript.decorators;

import org.mozilla.javascript.*;

public class WrapDecorator extends Decorator {
    public static void init(Scriptable scope) {
        WrapDecorator wrap = new WrapDecorator();
        ScriptableObject.defineProperty(scope, "@wrap", wrap, ScriptableObject.DONTENUM);
    }

    @Override
    public Object consume(Object target, int descriptor, DecoratorType decoratorType, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (decoratorType != DecoratorType.WRAP) return target;
        if ((descriptor & FIELD) != 0) {
            throw ScriptRuntime.typeError("@wrap cannot be applied to class fields");
        }
        if (args.length == 0 || !(args[0] instanceof Callable)) return target;

        return ((Callable) args[0]).call(cx, scope, thisObj, new Object[]{ target });
    }
}
