/*
 * Copyright (c) 2017 by Object Engineering GmbH, Uitikon, Switzerland
 * All rights reserved.
 * This product is the proprietary and sole property of Object Engineering GmbH.
 * Use, duplication or dissemination is forbidden and is subject
 * to prior written consent of Object Engineering GmbH (info@objeng.ch)
 */
package ch.kochse.tools.asyncapi.codegen.component;

public interface IComponentVisitor {

    enum TraverseControl {
        /**
         * Continue to traverse
         */
        CONTINUE,
        /**
         * Don't continue to traverse, but normal way back calling leave
         */
        CANCEL,
        /**
         * Don't continue to traverse without using leave on the way back
         */
        ABORT,

        ;

    }


    /**
     * Resets the visitor to initial state
     */
    void reset();

    /**
     * Enter the component node
     *
     * @param pComp node to enter
     * @return TraverseControl CONTINUE to continue to traverse, CANCEL traversal, ABORT traversal
     */
    TraverseControl enter(IComponentNode pComp);

    /**
     * Leave the component node
     *
     * @param pComp IComponentNode to leave
     * @return TraverseControl CONTINUE to continue to traverse, CANCEL traversal, ABORT traversal
     */
    TraverseControl leave(IComponentNode pComp);

}
