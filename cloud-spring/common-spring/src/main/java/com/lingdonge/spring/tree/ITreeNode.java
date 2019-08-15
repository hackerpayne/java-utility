package com.lingdonge.spring.tree;


import java.util.List;

/**
 *
 */
public interface ITreeNode {

    Integer getId();

    Integer getPid();

    <T extends ITreeNode> void setChildrenList(List<T> childrenList);
}
