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


public class Component
        implements IComponentNode, IComponentDelegate {

    /**
     * Parent this component is connected
     */
    private IComponentNode     parent;
    /**
     * Target for the visitor to handle
     */
    private IComponentDelegate target;
    
    private String key; 

    public Component(String pKey) {
       key = pKey;
    }

    /**
     * @param pKey
     * @param pParent
     * @param pTarget
     */
    public Component(String pKey, IComponentNode pParent, IComponentDelegate pTarget) {
        this(pKey, pParent);
        target = pTarget;
    }

    /**
     * @param pKey
     * @param pParent
     */
    public Component(String pKey, IComponentNode pParent) {
        this(pKey);
        parent = pParent;
    }

    /**
     * visit
     *
     * @param pVisitor
     */
    @Override
    public TraverseControl visit(IComponentVisitor pVisitor) {
        TraverseControl ret = TraverseControl.CONTINUE;

        if (pVisitor != null) {
            ret = pVisitor.enter(this);
            if (ret != TraverseControl.ABORT) {
                ret = pVisitor.leave(this);
            }
        }
        return ret;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public boolean isRoot() {
        return parent == null;
    }

    @Override
    public IComponentNode getParent() {
        return parent;
    }


    /**
     * @param pParent the parent to set
     */
    protected void setParent(IComponentNode pParent) {
        parent = pParent;
    }

    @Override
    public List<IComponentNode> getChildren() {
        return null;
    }

    @Override
    public IComponentDelegate getTarget() {
        if (target != null) {
            return target;
        } else {
            return this;
        }
    }

    /**
     * <b>remove</b>
     * <br>
     * Description: compare component pComp
     *
     * @param pComp
     * @return
     */
    public Component remove(Component pComp) {
        if (pComp == this) {
            return this;
        } else {
            return null;
        }
    }

    @Deprecated
    public Component findByKey(String pKey) {
        if (this.getKey().equals(pKey)) {
            return this;
        }
        return null;
    }

    /**
     * lookup
     * <br>
     * Description: compare Interface with pKey
     *
     * @param pKey
     */
    @Override
    public IComponentNode lookup(String pKey) {
        if (this.getKey().equals(pKey)) {
            return this;
        }
        return null;
    }

	@Override
	public String getKey() {
		return key;
	}

}
