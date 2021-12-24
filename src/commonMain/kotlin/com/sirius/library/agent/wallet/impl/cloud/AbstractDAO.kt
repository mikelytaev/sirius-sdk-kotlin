package com.sirius.library.agent.wallet.impl.cloud/*
package com.sirius.library.agent.wallet.impl

abstract class AbstractDAO<T> {
    */
/**
     * Method returns class implementing EntityInterface which was used in class
     * extending AbstractDAO
     *
     * @return Class<T extends EntityInterface>
    </T> *//*

    fun returnedClass(): java.lang.Class<T> {
        return getTypeArguments(AbstractDAO::class.java, javaClass)[0] as java.lang.Class<T>
    }

    companion object {
        */
/**
         * Get the underlying class for a type, or null if the type is a variable
         * type.
         *
         * @param type the type
         * @return the underlying class
         *//*

        fun getClass(type: java.lang.reflect.Type?): java.lang.Class<*>? {
            return if (type is java.lang.Class) {
                type as java.lang.Class?
            } else if (type is java.lang.reflect.ParameterizedType) {
                getClass((type as java.lang.reflect.ParameterizedType?).getRawType())
            } else if (type is java.lang.reflect.GenericArrayType) {
                val componentType: java.lang.reflect.Type =
                    (type as java.lang.reflect.GenericArrayType?).getGenericComponentType()
                val componentClass: java.lang.Class<*>? = getClass(componentType)
                if (componentClass != null) {
                    java.lang.reflect.Array.newInstance(componentClass, 0).javaClass
                } else {
                    null
                }
            } else {
                null
            }
        }

        */
/**
         * Get the actual type arguments a child class has used to extend a generic
         * base class.
         *
         * @param baseClass the base class
         * @param childClass the child class
         * @return a list of the raw classes for the actual type arguments.
         *//*

        fun <T> getTypeArguments(
            baseClass: java.lang.Class<T>, childClass: java.lang.Class<out T>
        ): List<java.lang.Class<*>> {
            val resolvedTypes: MutableMap<java.lang.reflect.Type?, java.lang.reflect.Type> =
                java.util.HashMap<java.lang.reflect.Type, java.lang.reflect.Type>()
            var type: java.lang.reflect.Type = childClass
            // start walking up the inheritance hierarchy until we hit baseClass
            while (getClass(type) != baseClass) {
                if (type is java.lang.Class) {
                    // there is no useful information for us in raw types, so just keep going.
                    type = (type as java.lang.Class).getGenericSuperclass()
                } else {
                    val parameterizedType: java.lang.reflect.ParameterizedType =
                        type as java.lang.reflect.ParameterizedType
                    val rawType: java.lang.Class<*> = parameterizedType.getRawType() as java.lang.Class
                    val actualTypeArguments: Array<java.lang.reflect.Type> = parameterizedType.getActualTypeArguments()
                    val typeParameters: Array<java.lang.reflect.TypeVariable<*>> = rawType.getTypeParameters()
                    for (i in actualTypeArguments.indices) {
                        resolvedTypes[typeParameters[i]] = actualTypeArguments[i]
                    }
                    if (rawType != baseClass) {
                        type = rawType.getGenericSuperclass()
                    }
                }
            }

            // finally, for each actual type argument provided to baseClass, determine (if possible)
            // the raw class for that type argument.
            val actualTypeArguments: Array<java.lang.reflect.Type>
            actualTypeArguments = if (type is java.lang.Class) {
                (type as java.lang.Class).getTypeParameters()
            } else {
                (type as java.lang.reflect.ParameterizedType).getActualTypeArguments()
            }
            val typeArgumentsAsClasses: MutableList<java.lang.Class<*>> = ArrayList<java.lang.Class<*>>()
            // resolve types by chasing down type variables.
            for (baseType in actualTypeArguments) {
                while (resolvedTypes.containsKey(baseType)) {
                    baseType = resolvedTypes[baseType]
                }
                typeArgumentsAsClasses.add(getClass(baseType))
            }
            return typeArgumentsAsClasses
        }
    }
}*/
