/**********************************************************\
|                                                          |
|                          hprose                          |
|                                                          |
| Official WebSite: http://www.hprose.com/                 |
|                   http://www.hprose.net/                 |
|                   http://www.hprose.org/                 |
|                                                          |
\**********************************************************/
/**********************************************************\
 *                                                        *
 * HproseInvocationHandler.java                           *
 *                                                        *
 * hprose InvocationHandler class for Java.               *
 *                                                        *
 * LastModified: Apr 3, 2014                              *
 * Author: Ma Bingyao <andot@hprose.com>                  *
 *                                                        *
\**********************************************************/
package hprose.common;

import hprose.io.HproseHelper;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class HproseInvocationHandler implements InvocationHandler {
    private static final Byte byteZero = (byte) 0;
    private static final Short shortZero = (short) 0;
    private static final Integer intZero = 0;
    private static final Long longZero = (long) 0;
    private static final Character charZero = (char) 0;
    private static final Float floatZero = (float) 0;
    private static final Double doubleZero = (double) 0;

    private final HproseInvoker client;
    private final String ns;

    public HproseInvocationHandler(HproseInvoker client, String ns) {
        this.client = client;
        this.ns = ns;
    }

    public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
        String functionName = method.getName();
        MethodName methodName = method.getAnnotation(MethodName.class);
        if (methodName != null) {
            functionName = methodName.value();
        }
        HproseResultMode resultMode = HproseResultMode.Normal;
        ResultMode rm = method.getAnnotation(ResultMode.class);
        if (rm != null) {
            resultMode = rm.value();
        }
        boolean simple = false;
        SimpleMode sm = method.getAnnotation(SimpleMode.class);
        if (sm != null) {
            simple = sm.value();
        }
        boolean byRef = false;
        ByRef byref = method.getAnnotation(ByRef.class);
        if (byref != null) {
            byRef = byref.value();
        }
        Type[] paramTypes = method.getGenericParameterTypes();
        Type returnType = method.getGenericReturnType();
        if (void.class.equals(returnType) ||
            Void.class.equals(returnType)) {
            returnType = null;
        }
        int n = paramTypes.length;
        if (ns != null) {
            functionName = ns + '_' + functionName;
        }
        Object result = null;
        if ((n > 0) && HproseHelper.toClass(paramTypes[n - 1]).equals(HproseCallback1.class)) {
            if (paramTypes[n - 1] instanceof ParameterizedType) {
                returnType = ((ParameterizedType)paramTypes[n - 1]).getActualTypeArguments()[0];
            }
            HproseCallback1 callback = (HproseCallback1) arguments[n - 1];
            Object[] tmpargs = new Object[n - 1];
            System.arraycopy(arguments, 0, tmpargs, 0, n - 1);
            client.invoke(functionName, tmpargs, callback, null, returnType, resultMode, simple);
        }
        else if ((n > 0) && HproseHelper.toClass(paramTypes[n - 1]).equals(HproseCallback.class)) {
            if (paramTypes[n - 1] instanceof ParameterizedType) {
                returnType = ((ParameterizedType)paramTypes[n - 1]).getActualTypeArguments()[0];
            }
            HproseCallback callback = (HproseCallback) arguments[n - 1];
            Object[] tmpargs = new Object[n - 1];
            System.arraycopy(arguments, 0, tmpargs, 0, n - 1);
            client.invoke(functionName, tmpargs, callback, null, returnType, byRef, resultMode, simple);
        }
        else if ((n > 1) && HproseHelper.toClass(paramTypes[n - 2]).equals(HproseCallback1.class)
                         && HproseHelper.toClass(paramTypes[n - 1]).equals(HproseErrorEvent.class)) {
            if (paramTypes[n - 2] instanceof ParameterizedType) {
                returnType = ((ParameterizedType)paramTypes[n - 2]).getActualTypeArguments()[0];
            }
            HproseCallback1 callback = (HproseCallback1) arguments[n - 2];
            HproseErrorEvent errorEvent = (HproseErrorEvent) arguments[n - 1];
            Object[] tmpargs = new Object[n - 2];
            System.arraycopy(arguments, 0, tmpargs, 0, n - 2);
            client.invoke(functionName, tmpargs, callback, errorEvent, returnType, resultMode, simple);
        }
        else if ((n > 1) && HproseHelper.toClass(paramTypes[n - 2]).equals(HproseCallback.class)
                         && HproseHelper.toClass(paramTypes[n - 1]).equals(HproseErrorEvent.class)) {
            if (paramTypes[n - 2] instanceof ParameterizedType) {
                returnType = ((ParameterizedType)paramTypes[n - 2]).getActualTypeArguments()[0];
            }
            HproseCallback callback = (HproseCallback) arguments[n - 2];
            HproseErrorEvent errorEvent = (HproseErrorEvent) arguments[n - 1];
            Object[] tmpargs = new Object[n - 2];
            System.arraycopy(arguments, 0, tmpargs, 0, n - 2);
            client.invoke(functionName, tmpargs, callback, errorEvent, returnType, byRef, resultMode, simple);
        }
        else {
            result = client.invoke(functionName, arguments, returnType, byRef, resultMode, simple);
        }
        if (result == null) {
            if (int.class.equals(returnType)) {
                return intZero;
            }
            if (long.class.equals(returnType)) {
                return longZero;
            }
            if (byte.class.equals(returnType)) {
                return byteZero;
            }
            if (short.class.equals(returnType)) {
                return shortZero;
            }
            if (float.class.equals(returnType)) {
                return floatZero;
            }
            if (double.class.equals(returnType)) {
                return doubleZero;
            }
            if (char.class.equals(returnType)) {
                return charZero;
            }
            if (boolean.class.equals(returnType)) {
                return Boolean.FALSE;
            }
        }
        return result;
    }
}
