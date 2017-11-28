/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.ub485.core;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.impl.SoftReferenceObjectPool;

/**
 * @author Evgeniy Egorov
 */
public class ActionRunnablePool extends SoftReferenceObjectPool {

    private static ActionRunnablePool instance = null;

    private ActionRunnablePool(BasePoolableObjectFactory basePoolableObjectFactory) {
        super(basePoolableObjectFactory);
    }

    public static ActionRunnablePool getInstance() {
        if (instance == null) {

            instance = new ActionRunnablePool(new BasePoolableObjectFactory() {
                @Override
                public Object makeObject() throws Exception {
                    return new ActionTransmit();
                }
            });

        }
        return instance;
    }

    public ActionTransmit borrowTransmitter() {
        try {
            return (ActionTransmit) instance.borrowObject();
        } catch (Exception ex) {
            throw new RuntimeException("Проблемы с ActionRunnablePool. ", ex);
        }
    }

    public void returnTransmitter(ActionTransmit aTransmitter) {
        try {
            instance.returnObject(aTransmitter);
        } catch (Exception ex) {
            throw new RuntimeException("Проблемы с  ActionRunnablePool. ", ex);
        }
    }
}
