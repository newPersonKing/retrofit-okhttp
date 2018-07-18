package com.xsb.myupload.untils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * copy http://qussay.com/2013/09/28/handling-java-generic-types-with-reflection
 * <p>Utility class that uses {@code java.lang.reflect} standard library.
 * It provides easy access to the standard reflect methods that are
 * needed usually when dealing with generic object types.</p>
 *
 * @author Qussay Najjar
 * @version 1.1
 * @link http://qussay.com/2013/09/28/handling-java-generic-types-with-reflection
 * @since 2014-04-13
 */
public class ReflectionUtil {

    /**
     * When {@code Type} initialized with a value of an object, its fully qualified class name
     * will be prefixed with this.
     *
     * @see {@link ReflectionUtil#getClassName(Type)}
     */
    private static final String TYPE_CLASS_NAME_PREFIX = "class ";
    private static final String TYPE_INTERFACE_NAME_PREFIX = "interface ";

    /*
     *  Utility class with static access methods, no need for constructor.
     */
    private ReflectionUtil() {
    }

    /**
     * {@link Type#toString()} value is the fully qualified class name prefixed
     * with {@link ReflectionUtil# TYPE_NAME_PREFIX}. This method will substring it, for it to be eligible
     * for {@link Class#forName(String)}.
     *
     * @param type the {@code Type} value whose class name is needed.
     * @return {@code String} class name of the invoked {@code type}.
     * @see {@link ReflectionUtil#getClass()}
     */
    public static String getClassName(Type type) {
        if (type == null) {
            return "";
        }
        String className = type.toString();
        if (className.startsWith(TYPE_CLASS_NAME_PREFIX)) {
            className = className.substring(TYPE_CLASS_NAME_PREFIX.length());
        } else if (className.startsWith(TYPE_INTERFACE_NAME_PREFIX)) {
            className = className.substring(TYPE_INTERFACE_NAME_PREFIX.length());
        }
        return className;
    }

    /**
     * Returns the {@code Class} object associated with the given {@link Type}
     * depending on its fully qualified name.
     *
     * @param type the {@code Type} whose {@code Class} is needed.
     * @return the {@code Class} object for the class with the specified name.
     * @throws ClassNotFoundException if the class cannot be located.
     * @see {@link ReflectionUtil#getClassName(Type)}
     */
    public static Class<?> getClass(Type type)
            throws ClassNotFoundException {
        String className = getClassName(type);
        if (className == null || className.isEmpty()) {
            return null;
        }
        return Class.forName(className);
    }

    /**
     * Creates a new instance of the class represented by this {@code Type} object.
     *
     * @param type the {@code Type} object whose its representing {@code Class} object
     *             will be instantiated.
     * @return a newly allocated instance of the class represented by
     * the invoked {@code Type} object.
     * @throws ClassNotFoundException if the class represented by this {@code Type} object
     *                                cannot be located.
     * @throws InstantiationException if this {@code Type} represents an abstract class,
     *                                an interface, an array class, a primitive type, or void;
     *                                or if the class has no nullary constructor;
     *                                or if the instantiation fails for some other reason.
     * @throws IllegalAccessException if the class or its nullary constructor is not accessible.
     * @see {@link Class#newInstance()}
     */
    public static Object newInstance(Type type)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class<?> clazz = getClass(type);
        if (clazz == null) {
            return null;
        }
        return clazz.newInstance();
    }

    /**
     * Returns an array of {@code Type} objects representing the actual type
     * arguments to this object.
     * If the returned value is null, then this object represents a non-parameterized
     * object.
     *
     * @param object the {@code object} whose type arguments are needed.
     * @return an array of {@code Type} objects representing the actual type
     * arguments to this object.
     * @see {@link Class#getGenericSuperclass()}
     * @see {@link ParameterizedType#getActualTypeArguments()}
     */
    public static Type[] getParameterizedTypes(Object object) {
        Type superclassType = object.getClass().getGenericSuperclass();
        if (!ParameterizedType.class.isAssignableFrom(superclassType.getClass())) {
            return null;
        }

        return ((ParameterizedType) superclassType).getActualTypeArguments();
    }
    /*以Type数组的形式返回本类直接实现的接口列表 callback<T> 返回的是callback的实现列表*/
    public static Type[] getParameterizedTypeswithInterfaces(Object object) {
        return object.getClass().getGenericInterfaces();
    }

    /**
     * Checks whether a {@code Constructor} object with no parameter types is specified
     * by the invoked {@code Class} object or not.
     *
     * @param clazz the {@code Class} object whose constructors are checked.
     * @return {@code true} if a {@code Constructor} object with no parameter types is specified.
     * @throws SecurityException If a security manager, <i>s</i> is present and any of the
     *                           following conditions is met:
     *                           <ul>
     *                           <li> invocation of
     *                           {@link SecurityManager#checkMemberAccess
     *                           s.checkMemberAccess(this, Member.PUBLIC)} denies
     *                           access to the constructor
     *                           <p/>
     *                           <li> the caller's class loader is not the same as or an
     *                           ancestor of the class loader for the current class and
     *                           invocation of {@link SecurityManager#checkPackageAccess
     *                           s.checkPackageAccess()} denies access to the package
     *                           of this class
     *                           </ul>
     * @see {@link Class#getConstructor(Class...)}
     */
    public static boolean hasDefaultConstructor(Class<?> clazz) throws SecurityException {
        Class<?>[] empty = {};
        try {
            clazz.getConstructor(empty);
        } catch (NoSuchMethodException e) {
            return false;
        }
        return true;
    }

    /**
     * Returns a {@code Class} object that identifies the
     * declared class for the field represented by the given {@code String name} parameter inside
     * the invoked {@code Class<?> clazz} parameter.
     *
     * @param clazz the {@code Class} object whose declared fields to be
     *              checked for a certain field.
     * @param name  the field name as {@code String} to be
     *              compared with {@link Field#getName()}
     * @return the {@code Class} object representing the type of given field name.
     * @see {@link Class#getDeclaredFields()}
     * @see {@link Field#getType()}
     */
    public static Class<?> getFieldClass(Class<?> clazz, String name) {
        if (clazz == null || name == null || name.isEmpty()) {
            return null;
        }

        Class<?> propertyClass = null;

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getName().equalsIgnoreCase(name)) {
                propertyClass = field.getType();
                break;
            }
        }

        return propertyClass;
    }

    /**
     * Returns a {@code Class} object that identifies the
     * declared class as a return type for the method represented by the given
     * {@code String name} parameter inside the invoked {@code Class<?> clazz} parameter.
     *
     * @param clazz the {@code Class} object whose declared methods to be
     *              checked for the wanted method name.
     * @param name  the method name as {@code String} to be
     *              compared with {@link Method#getName()}
     * @return the {@code Class} object representing the return type of the given method name.
     * @see {@link Class#getDeclaredMethods()}
     * @see {@link Method#getReturnType()}
     */
    public static Class<?> getMethodReturnType(Class<?> clazz, String name) {
        if (clazz == null || name == null || name.isEmpty()) {
            return null;
        }

        name = name.toLowerCase();
        Class<?> returnType = null;

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(name)) {
                returnType = method.getReturnType();
                break;
            }
        }

        return returnType;
    }

    /**
     * Extracts the enum constant of the specified enum class with the
     * specified name. The name must match exactly an identifier used
     * to declare an enum constant in the given class.
     *
     * @param clazz the {@code Class} object of the enum type from which
     *              to return a constant.
     * @param name  the name of the constant to return.
     * @return the enum constant of the specified enum type with the
     * specified name.
     * @throws IllegalArgumentException if the specified enum type has
     *                                  no constant with the specified name, or the specified
     *                                  class object does not represent an enum type.
     * @see {@link Enum#valueOf(Class, String)}
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object getEnumConstant(Class<?> clazz, String name) {
        if (clazz == null || name == null || name.isEmpty()) {
            return null;
        }
        return Enum.valueOf((Class<Enum>) clazz, name);
    }


    /**
     * get T classz
     *
     * @param types
     * @return Type[]  getActualTypeArgumentsl callback<T> 返回的是 T </>
     */
    public static List<Type> methodHandler(Type[] types) {
        List<Type> needtypes = new ArrayList<>();
        for (Type paramType : types) {
            System.out.println("  " + paramType);
            // if Type is T
            if (paramType instanceof ParameterizedType) {
                Type[] parentypes = ((ParameterizedType) paramType).getActualTypeArguments();
                for (Type childtype : parentypes) {
                    needtypes.add(childtype);
                    if (childtype instanceof ParameterizedType) {
                        Type[] childtypes = ((ParameterizedType) childtype).getActualTypeArguments();
                        for (Type type : childtypes) {
                            needtypes.add(type);
                        }
                    }
                }
            }
        }
        return needtypes;
    }


    /**
     * Extracts the enum constant of the specified enum class with the
     * specified name. The name must match exactly an identifier used
     * to declare an enum constant in the given class.
     *
     * @param types the {@code Class} object of the enum type from which
     *              to return a constant.
     * @return the enum constant of the specified enum type with the
     * specified name.
     * @throws IllegalArgumentException if the specified enum type has
     *                                  no constant with the specified name, or the specified
     *                                  class object does not represent an enum type.
     * @see {@link Enum#valueOf(Class, String)}
     */
    private static Type methodHandler2(Type[] types) {
        List<Type> needtypes = new ArrayList<>();
        Type needParentType = null;

        for (Type paramType : types) {
            System.out.println("  " + paramType);
            // if Type is T
            if (paramType instanceof ParameterizedType) {
                Type[] parentypes = ((ParameterizedType) paramType).getActualTypeArguments();
                for (Type childtype : parentypes) {
                    needtypes.add(childtype);
                    needParentType = childtype;
                    if (childtype instanceof ParameterizedType) {
                        Type[] childtypes = ((ParameterizedType) childtype).getActualTypeArguments();
                        for (Type type : childtypes) {
                            needParentType = type;
                        }
                    }
                }
            }
        }
        return needParentType;
    }


    /**
     * @param clazz @param clazz the {@code Class} object whose declared methods to be
     *              checked for the wanted method name.
     * @return @return the enum constant of the specified enum type with the
     * specified name.
     */
    public static Type getClassType(Class<?> clazz) {

        return getClassType(((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments());
    }


    /**
     *     /**
     * Extracts the enum constant of the specified enum class with the
     * specified name. The name must match exactly an identifier used
     * to declare an enum constant in the given class.
     *
     * @param  types the {@code Class} object of the enum type from which
     *              to return a constant.
     * @return the enum constant of the specified enum type with the
     * specified name.
     * @throws IllegalArgumentException if the specified enum type has
     *                                  no constant with the specified name, or the specified
     *                                  class object does not represent an enum type.
     * @see {@link Enum#valueOf(Class, String)}
     */

    /**
     * @param clazz @param clazz the {@code Class} object whose declared methods to be
     *              checked for the wanted method name.
     * @return @return the enum constant of the specified enum type with the
     * specified name.
     */
    public static List<Type> getClassTypes(Class<?> clazz) {

        return getClassTypes(((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments());
    }


    public static List<Type> getClassTypes(Type[] types) {
        List<Type> needtypes = new ArrayList<>();
        for (Type paramType : types) {
            System.out.println("  " + paramType);
            // if Type is T
            if (paramType instanceof ParameterizedType) {
                Type[] parentypes = ((ParameterizedType) paramType).getActualTypeArguments();
                for (Type childtype : parentypes) {
                    needtypes.add(childtype);
                    if (childtype instanceof ParameterizedType) {
                        Type[] childtypes = ((ParameterizedType) childtype).getActualTypeArguments();
                        for (Type type : childtypes) {
                            needtypes.add(type);
                        }
                    }
                }
            }
        }
        return needtypes;
    }


    /**
     * /**
     * Extracts the enum constant of the specified enum class with the
     * specified name. The name must match exactly an identifier used
     * to declare an enum constant in the given class.
     *
     * @param types the {@code Class} object of the enum type from which
     *              to return a constant.
     * @return the enum constant of the specified enum type with the
     * specified name.
     * @throws IllegalArgumentException if the specified enum type has
     *                                  no constant with the specified name, or the specified
     *                                  class object does not represent an enum type.
     * @see {@link Enum#valueOf(Class, String)}
     */

    public static Type getClassType(Type[] types) {
        List<Type> needtypes = new ArrayList<>();
        Type needParentType = null;

        for (Type paramType : types) {
            System.out.println("  " + paramType);
            // if Type is T
            if (paramType instanceof ParameterizedType) {
                Type[] parentypes = ((ParameterizedType) paramType).getActualTypeArguments();
                for (Type childtype : parentypes) {
                    needtypes.add(childtype);
                    needParentType = childtype;
                    if (childtype instanceof ParameterizedType) {
                        Type[] childtypes = ((ParameterizedType) childtype).getActualTypeArguments();
                        for (Type type : childtypes) {
                            needParentType = type;
                        }
                    }
                }
            }
        }
        return needParentType;
    }

    public static Type getType(Class<?> c) {
        ParameterizedType genType = (ParameterizedType) c.getClass().getGenericSuperclass();
        Type[] actualTypeArguments = ((ParameterizedType) genType).getActualTypeArguments();
        return actualTypeArguments[0];
    }

    /**
     * 得到子类传递的参数化类型 //如果DeptAction extends
     * BaseAction<Dept>：modelClass=Dept.class //如果JobAction extends
     * BaseAction<Job>：modelClass=Job.class
     *
     * @param clz
     *            当前对象
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getSuperGenericClass(Class<?> clz) {
        Class<T> result = null;
        // 得到当前对象的父类"泛型类型"(也叫参数化类型)
        // superclass == GenericDao<Dept>成为参数化类型
        // superclass == BaseDao不是参数化类型
        Type superclass = clz.getGenericSuperclass();
        // 判断类型是否为参数化类型
        if (superclass instanceof ParameterizedType) {
            // 把父类类型转换成参数化类型（泛型类型）
            // 只有ParameterizedType才能通过getActualTypeArguments得到参数
            ParameterizedType parameterizedType = (ParameterizedType) superclass;
            // 得到参数化类型类型的参数
            // types == GenericDao<Dept>的"<Dept>"参数
            Type[] types = parameterizedType.getActualTypeArguments();
            // 返回子类传递的类型
            result = (Class<T>) types[0];
        }
        return result;
    }
}