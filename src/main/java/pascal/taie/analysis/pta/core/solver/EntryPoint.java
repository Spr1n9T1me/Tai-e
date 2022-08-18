/*
 * Tai-e: A Static Analysis Framework for Java
 *
 * Copyright (C) 2022 Tian Tan <tiantan@nju.edu.cn>
 * Copyright (C) 2022 Yue Li <yueli@nju.edu.cn>
 *
 * This file is part of Tai-e.
 *
 * Tai-e is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * Tai-e is distributed in the hope that it will be useful,but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Tai-e. If not, see <https://www.gnu.org/licenses/>.
 */

package pascal.taie.analysis.pta.core.solver;

import pascal.taie.analysis.pta.core.heap.Obj;
import pascal.taie.language.classes.JMethod;

import java.util.Set;

/**
 * Represents entry points in pointer analysis. Each entry specifies:
 * <ol>
 *     <li>an entry method
 *     <li>the objects for this variable/parameters of the entry method
 * </ol>
 */
public abstract class EntryPoint {

    /**
     * The entry method.
     */
    protected final JMethod method;

    protected EntryPoint(JMethod method) {
        this.method = method;
    }

    /**
     * @return the entry method.
     */
    public JMethod getMethod() {
        return method;
    }

    /**
     * @return the objects for this variable.
     */
    public abstract Set<Obj> getThis();

    /**
     * @return the objects for i-th parameter.
     */
    public abstract Set<Obj> getParam(int i);

    @Override
    public String toString() {
        return getClass().getSimpleName() + '{' + method + '}';
    }
}