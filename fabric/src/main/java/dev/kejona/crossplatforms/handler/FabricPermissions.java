package dev.kejona.crossplatforms.handler;

import dev.kejona.crossplatforms.CrossplatFormsFabric;
import dev.kejona.crossplatforms.permission.Permission;
import dev.kejona.crossplatforms.permission.Permissions;

import java.util.Collection;

public class FabricPermissions implements Permissions {
    public FabricPermissions(CrossplatFormsFabric crossplatFormsFabric) {

    }

    @Override
    public void registerPermissions(Collection<Permission> permissions) {

    }

    @Override
    public void notifyPluginLoaded() {
        Permissions.super.notifyPluginLoaded();
    }
}
