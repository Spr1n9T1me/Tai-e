/*
 * Bamboo - A Program Analysis Framework for Java
 *
 * Copyright (C) 2020 Tian Tan <tiantan@nju.edu.cn>
 * Copyright (C) 2020 Yue Li <yueli@nju.edu.cn>
 * All rights reserved.
 *
 * This software is designed for the "Static Program Analysis" course at
 * Nanjing University, and it supports a subset of Java features.
 * Bamboo is only for educational and academic purposes, and any form of
 * commercial use is disallowed.
 */

package bamboo.pta.statement;

import bamboo.pta.element.Variable;

/**
 * Represents an array load: to = base[*];
 */
public class ArrayLoad implements Statement {

    private final Variable to;

    private final Variable base;

    public ArrayLoad(Variable to, Variable base) {
        this.to = to;
        this.base = base;
    }

    public Variable getTo() {
        return to;
    }

    public Variable getBase() {
        return base;
    }

    @Override
    public Kind getKind() {
        return Kind.ARRAY_LOAD;
    }

    @Override
    public String toString() {
        return to + " = " + base + "[*]";
    }
}
