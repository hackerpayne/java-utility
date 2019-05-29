package com.lingdonge.spring.tree;


import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Getter
@Setter
public class TreeNode implements Serializable {
    private static final long serialVersionUID = 8772115911922451037L;

    protected int id;

    protected int parentId;

    protected List<TreeNode> children = new ArrayList<TreeNode>();

    public void add(TreeNode node) {
        children.add(node);
    }
}
