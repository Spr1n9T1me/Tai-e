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

package pascal.taie.util.collection;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        GenericBitSetTest.class,
        ArraySetTest.class,
        ArrayMapTest.class,
        HybridArrayHashMapTest.class,
        HybridArrayHashSetTest.class,
        IndexMapTest.class,
        MultiMapTest.class,
        SetQueueTest.class,
        SimpleBitSetTest.class,
        SparseBitSetTest.class,
        StreamsTest.class,
        TwoKeyMapTest.class,
        ViewsTest.class,
})
public class CollectionTestSuite {
}