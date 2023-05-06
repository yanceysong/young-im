package com.yanceysong.im.infrastructure.route.algroithm.hash;

import com.yanceysong.im.common.enums.user.UserErrorCode;
import com.yanceysong.im.common.exception.YoungImException;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @ClassName TreeMapConsistentHash
 * @Description
 * @date 2023/5/6 11:00
 * @Author yanceysong
 * @Version 1.0
 */
public class TreeMapConsistentHash extends AbstractConsistentHash {

    private final TreeMap<Long, String> treeMap = new TreeMap<>();
    private static final int NODE_SIZE = 2;

    @Override
    protected void add(long key, String value) {
        for (int i = 0; i < NODE_SIZE; i++) {
            treeMap.put(super.hash("node" + key + i), value);
        }
        treeMap.put(key, value);
    }

    @Override
    protected String getFirstNodeValue(String value) {
        Long hash = super.hash(value);
        SortedMap<Long, String> last = treeMap.tailMap(hash);
        if (!last.isEmpty()) {
            return last.get(last.firstKey());
        }
        if (treeMap.size() == 0) {
            throw new YoungImException(UserErrorCode.SERVER_NOT_AVAILABLE);
        }
        return treeMap.firstEntry().getValue();
    }

    @Override
    protected void processBefore() {
        treeMap.clear();
    }
}
