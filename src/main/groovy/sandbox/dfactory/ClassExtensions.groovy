package sandbox.dfactory

class ClassExtensions {

    //TODO I wnat to remove this calle from here....... :(
    public static <T> T smartNew(Class<T> aClass, Class caller){
        return DFactory.instance().instanceForCallerOf(caller, aClass)
    }

    //TODO to implement...
    public static <T> T smartNew(Class<T> aClass, Class callee, Object... params){
        return aClass.newInstance(params)
    }
}
