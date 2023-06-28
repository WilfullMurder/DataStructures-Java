package org.example;

public class Utils {


    /**
     * A version of assert(.) that does not need -ea java flag
     * @param b the boolean assertion (must be true)
     * @throws AssertionError
     */
    protected static void myassert(boolean b) throws AssertionError{
        if(!b) throw new AssertionError();
    }
}
