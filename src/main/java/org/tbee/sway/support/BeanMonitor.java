package org.tbee.sway.support;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BeanMonitor {

    private final Set<Object> monitoredBeans = new HashSet<>();
    private final Set<Class> unableToMonitorClasses = new HashSet<>();
    private final Map<Class, BeanUtil.PropertyChangeConnector> classPropertyChangeConnectorMap = new HashMap<>();
    private final PropertyChangeListener propertyChangeListener;

    public BeanMonitor(PropertyChangeListener propertyChangeListener) {
        this.propertyChangeListener = propertyChangeListener;
    }

    public void monitor(Object bean) {
        // already monitoring, or cannot monitor
        Class<?> clazz = bean == null ? null : bean.getClass();
        if (bean == null || monitoredBeans.contains(bean) || unableToMonitorClasses.contains(clazz)) {
            return;
        }

        // Get the connector
        BeanUtil.PropertyChangeConnector propertyChangeConnector = classPropertyChangeConnectorMap.get(clazz);
        if (propertyChangeConnector == null) {
            propertyChangeConnector = BeanUtil.getPropertyChangeConnector(clazz);
            if (propertyChangeConnector == null || !propertyChangeConnector.isComplete()) {
                unableToMonitorClasses.add(clazz);
                return;
            }
            classPropertyChangeConnectorMap.put(clazz, propertyChangeConnector);
        }

        // Start monitoring
        propertyChangeConnector.register(bean, propertyChangeListener);
        monitoredBeans.add(bean);
    }

    public void unmonitorAll() {
        monitoredBeans.forEach(b -> {
            classPropertyChangeConnectorMap
                    .get(b.getClass())
                    .unregister(b, propertyChangeListener);
        });
        monitoredBeans.clear();
        unableToMonitorClasses.clear();
    }
}
