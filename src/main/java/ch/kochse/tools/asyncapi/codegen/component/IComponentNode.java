/*
 * Copyright (c) 2017 by Object Engineering GmbH, Uitikon, Switzerland
 * All rights reserved.
 * This product is the proprietary and sole property of Object Engineering GmbH.
 * Use, duplication or dissemination is forbidden and is subject
 * to prior written consent of Object Engineering GmbH (info@objeng.ch)
 */
package ch.kochse.tools.asyncapi.codegen.component;

import java.util.List;

import ch.kochse.tools.asyncapi.codegen.component.IComponentVisitor.TraverseControl;


public interface IComponentNode {

    /**
     * Let a visitor to traverse the component and its children
     *
     * @param pVisitor IComponentVisitor the visitor component
     * @return TraverseControl CONTINUE to continue to traverse, CANCEL traversal, ABORT traversal
     */
    TraverseControl visit(IComponentVisitor pVisitor);

    /**
     * Check if the component is a leave
     *
     * @return boolean true if it is the last node in a branch
     */
    boolean isLeaf();

    /**
     * Check if this component is the first element in the tree
     *
     * @return boolean true if it is the root of the tree
     */
    boolean isRoot();

    /**
     * Returns the parent node to which this node is connected
     *
     * @return IComponentNode parent node, null if it is root
     */
    IComponentNode getParent();

    /**
     * Returns a list of children the current node has
     *
     * @return List<IComponentNode> children of the node
     */
    List<IComponentNode> getChildren();

    /**
     * Return the real node to work on
     *
     * @return Object delegator object, in most cases the node itself
     */
    IComponentDelegate getTarget();

    /**
     * Looks up the tree for an object with key equal to pKey
     *
     * @param pKey String key of component looked for
     * @return IComponentNode object found or null
     */
    IComponentNode lookup(String pKey);

    /**
     * return the Key of the Component
     *
     * @return String key
     */
    String getKey();
}
