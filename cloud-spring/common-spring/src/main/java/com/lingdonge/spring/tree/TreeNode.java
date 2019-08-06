package com.lingdonge.spring.tree;


import com.lingdonge.core.bean.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Getter
@Setter
public class TreeNode extends BaseEntity {

    protected int id;

    protected int parentId;

    protected List<TreeNode> children = new ArrayList<TreeNode>();

    public void add(TreeNode node) {
        children.add(node);
    }
}
