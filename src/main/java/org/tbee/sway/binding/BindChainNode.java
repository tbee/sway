package org.tbee.sway.binding;

public interface BindChainNode<BinderType, BindeeType> {
    BindeeType binderToBindee(BindeeType in);
    BinderType bindeeToBinder(BinderType in);
}
