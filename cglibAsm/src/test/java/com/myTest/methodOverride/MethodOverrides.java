package com.myTest.methodOverride;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Create by qxz on 2018/1/23
 * Description:
 */
public class MethodOverrides {

    private final Set<MethodOverride> overrides = new LinkedHashSet<>(0);

    public MethodOverrides() {
    }

    public Set<MethodOverride> getOverrides() {
        return this.overrides;
    }

    public void addOverride(MethodOverride override) {
        this.overrides.add(override);
    }

    public MethodOverride getOverride(Method method) {
        synchronized (this.overrides) {
            MethodOverride match = null;
            for (MethodOverride candidate : this.overrides) {
                if (candidate == null) continue;
                if (candidate.matches(method)) {
                    match = candidate;
                }
            }
            return match;
        }
    }
}
