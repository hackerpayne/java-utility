package com.lingdonge.spring.tree;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * 树工具类
 */
public class TreeUtil {

    /**
     * 根据父节点创建树结构
     *
     * @param parentID
     * @param nodeList
     * @param <T>
     * @return
     */
    public static <T extends ITreeNode> List<T> buildTree(Integer parentID, List<T> nodeList) {
        // 根节点列表
        List<T> list = Lists.newArrayList();

        // 顺序遍历节点列表，如果之前是有序的，那么构建树后同层级之间有序
        for (int i = 0; i < nodeList.size(); i++) {
            T node = nodeList.get(i);
            // 递归入口， String.valueOf防止null值
            if (String.valueOf(node.getPid()).equals(String.valueOf(parentID))) {
                // parentID作为入口
                List<T> children = buildTree(node.getId(), nodeList);
                node.setChildrenList(children);
                list.add(node);
            }
        }

        return list;
    }

}