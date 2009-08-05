// Copyright (c) 2003 Compaq Corporation.  All rights reserved.
// Portions Copyright (c) 2003 Microsoft Corporation.  All rights reserved.
// Last modified on Mon 30 Apr 2007 at 13:33:37 PST by lamport
//      modified on Fri Sep 22 13:54:35 PDT 2000 by yuanyu

package tlc2.tool.liveness;

import tlc2.output.EC;
import tlc2.tool.TLCState;
import tlc2.tool.Tool;
import util.Assert;

/**
 * Handles always ([])
 * @author Leslie Lamport, Yuan Yu, Simon Zambrovski
 * @version $Id$
 */
class LNAll extends LiveExprNode
{
    /**
     * 
     */
    private static final String ALWAYS = "[]";
    protected LiveExprNode body;

    public LNAll(LiveExprNode body)
    {
        this.body = body;
    }

    public final LiveExprNode getBody()
    {
        return this.body;
    }

    public final int getLevel()
    {
        return 3;
    }

    public final boolean containAction()
    {
        return this.body.containAction();
    }

    public final boolean eval(Tool tool, TLCState s1, TLCState s2)
    {
        Assert.fail(EC.TLC_LIVE_CANNOT_EVAL_FORMULA, ALWAYS);
        return false; // make compiler happy
    }

    public final void toString(StringBuffer sb, String padding)
    {
        sb.append(ALWAYS);
        this.getBody().toString(sb, padding + "  ");
    }
}
