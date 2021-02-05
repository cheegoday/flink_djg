package com.djg.lambda;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OptionalDemo {

    @Test
    public void reduceTest() {
        Optional<List<Integer>> optional = Optional.of(new ArrayList<>());//of()里面不能传入null
        System.out.println(optional.get()); // []
        Optional<List<Integer>> optional1 = Optional.ofNullable(new ArrayList<>());
        System.out.println(optional1.get()); // []
        Optional<List<Integer>> emptyOptional = Optional.empty();
        Optional<List<Integer>> emptyOptional2 = Optional.ofNullable(null);
        System.out.println(emptyOptional.equals(emptyOptional2)); //true
    }
}


