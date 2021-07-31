/*
 * Tai-e: A Static Analysis Framework for Java
 *
 * Copyright (C) 2020-- Tian Tan <tiantan@nju.edu.cn>
 * Copyright (C) 2020-- Yue Li <yueli@nju.edu.cn>
 * All rights reserved.
 *
 * Tai-e is only for educational and academic purposes,
 * and any form of commercial use is disallowed.
 * Distribution of Tai-e is disallowed without the approval.
 */

package pascal.taie.analysis.pta.ci;

import pascal.taie.analysis.pta.core.heap.Obj;
import pascal.taie.language.classes.JField;
import pascal.taie.util.HashUtils;

/**
 * Represents instance field pointers in PFG.
 */
class InstanceFieldPtr extends Pointer {

    private final Obj base;

    private final JField field;

    InstanceFieldPtr(Obj base, JField field) {
        this.base = base;
        this.field = field;
    }

    Obj getBase() {
        return base;
    }

    JField getField() {
        return field;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InstanceFieldPtr fieldPtr = (InstanceFieldPtr) o;
        return base.equals(fieldPtr.base) && field.equals(fieldPtr.field);
    }

    @Override
    public int hashCode() {
        return HashUtils.hash(base, field);
    }

    @Override
    public String toString() {
        return base + "." + field.getName();
    }
}
