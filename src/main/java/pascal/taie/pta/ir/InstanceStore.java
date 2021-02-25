/*
 * Tai-e - A Program Analysis Framework for Java
 *
 * Copyright (C) 2020 Tian Tan <tiantan@nju.edu.cn>
 * Copyright (C) 2020 Yue Li <yueli@nju.edu.cn>
 * All rights reserved.
 *
 * This software is designed for the "Static Program Analysis" course at
 * Nanjing University, and it supports a subset of Java features.
 * Tai-e is only for educational and academic purposes, and any form of
 * commercial use is disallowed.
 */

package pascal.taie.pta.ir;

import pascal.taie.java.classes.FieldReference;

/**
 * Represents an instance store: base.field = from.
 */
public class InstanceStore extends AbstractStatement {

    private final Variable base;

    private final FieldReference fieldRef;

    private final Variable from;

    public InstanceStore(Variable base, FieldReference fieldRef, Variable from) {
        this.base = base;
        this.fieldRef = fieldRef;
        this.from = from;
        base.addInstanceStore(this);
    }

    public Variable getBase() {
        return base;
    }

    public FieldReference getFieldRef() {
        return fieldRef;
    }

    public Variable getFrom() {
        return from;
    }

    @Override
    public void accept(StatementVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return base + "." + fieldRef + " = " + from;
    }
}