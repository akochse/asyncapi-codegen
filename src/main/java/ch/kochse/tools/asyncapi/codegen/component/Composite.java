/*
 * Copyright (c) 2017 by Object Engineering GmbH, Uitikon, Switzerland
 * All rights reserved.
 * This product is the proprietary and sole property of Object Engineering GmbH.
 * Use, duplication or dissemination is forbidden and is subject
 * to prior written consent of Object Engineering GmbH (info@objeng.ch)
 */
package ch.kochse.tools.asyncapi.codegen.component;

import java.util.ArrayList;
import java.util.List;

import ch.kochse.tools.asyncapi.codegen.component.IComponentVisitor.TraverseControl;


public class Composite
        extends Component {

    private List<IComponentNode> mChildren;


    public Composite(String pKey) {
        this(pKey, null, null);
    }

    /**
     *
     * Composite
     *
     * @param pParent
     */
    public Composite(String pKey, IComponentNode pParent) {
        super(pKey, pParent, null);
    }

    /**
     *
     * Composite
     * <br>
     * Description: set new ArrayList
     *
     * @param pParent
     * @param pTarget
     */
    public Composite(String pKey, IComponentNode pParent, IComponentDelegate pTarget) {
        super(pKey, pParent, pTarget);
        mChildren = new ArrayList<IComponentNode>();
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    /**
     * <b>add</b>
     * <br>
     * Description: add Component pComp to ArrayList
     *
     * @param pComp
     */
    public void add(Component pComp) {
        if (pComp != null) {
            mChildren.add(pComp);
            pComp.setParent(this);
        }
    }

    /**
     * <br>
     * Description: remove Component pComp from ArrayList
     *
     * @param pComp
     */
    @Override
    public Component remove(Component pComp) {
        Component comp = null;
        if (mChildren.remove(pComp)) {
            return pComp;
        } else {
            for (IComponentNode cmp : mChildren) {
                comp = ((Composite) cmp).remove(pComp);
                if (comp != null) {
                    break;
                }
            }
        }
        return comp;
    }

    @Override
    public List<IComponentNode> getChildren() {
        List<IComponentNode> children = null;
        if (mChildren != null) {
            children = new ArrayList<IComponentNode>();
            for (IComponentNode cmp : mChildren) {
                children.add(cmp);
            }
        }
        return children;
    }

    /**
     * @param IComponentVisitor pVisitor
     * @return ret
     */
    @Override
    public TraverseControl visit(IComponentVisitor pVisitor) {
        TraverseControl ret = TraverseControl.CONTINUE;

        if (pVisitor != null) {
            ret = pVisitor.enter(this);
            if ((ret == TraverseControl.CONTINUE) && (mChildren != null)) {
                for (IComponentNode comp : mChildren) {
                    ret = comp.visit(pVisitor);
                    if (ret != TraverseControl.CONTINUE) {
                        break;
                    }
                }
            }
            if (ret != TraverseControl.ABORT) {
                ret = pVisitor.leave(this);
            }
        }
        return ret;
    }


    @Override
    @Deprecated
    public Component findByKey(String pKey) {
        IComponentNode found = lookup(pKey);
        return (Component) found;
    }

    /**
     *
     */
    @Override
    public IComponentNode lookup(String pKey) {
        IComponentNode found = super.lookup(pKey);
        if (found == null) {
            for (IComponentNode cmp : mChildren) {
                IComponentNode lookedfor = cmp.lookup(pKey);
                if (lookedfor != null) {
                    found = lookedfor;
                    break;
                }
            }
        }
        return found;
    }

}
