package com.uppercase.talend.components.processor;

public class Records {
    public String getName() {
        return _name;
    }

    private String _name;

    public int getAge() {
        return _age;
    }

    private int _age;

    public Records(String name, int age) {
        _name = name;
        _age = age;
    }
}
