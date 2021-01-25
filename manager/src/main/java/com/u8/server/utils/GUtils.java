package com.u8.server.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ant on 2017/8/31.
 */
public final class GUtils {

    public static <T> List<T> subList(List<T> lst , ICompareFilter<T> filter){

        if(lst == null || lst.size() == 0){
            return lst;
        }

        List<T> result = new ArrayList<T>();
        for(T t : lst){
            if(filter.match(t)){
                result.add(t);
            }
        }

        return result;
    }


    public static interface ICompareFilter<T>{

        public boolean match(T data);

    }
}
