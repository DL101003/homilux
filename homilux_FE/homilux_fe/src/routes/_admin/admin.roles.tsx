// src/routes/_admin/admin.roles.tsx
import { createFileRoute } from '@tanstack/react-router';
import { useState } from 'react';
import { useRoles, useDeleteRole, usePermissions } from '@/features/roles/hooks/useRoles';
import { RoleFormDialog } from '@/features/roles/components/RoleFormDialog';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Plus, Edit, Trash2, Shield, Loader2 } from 'lucide-react';
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog';
import type { components } from '@/types/api';

type RoleResponse = components['schemas']['RoleResponse'];

export const Route = createFileRoute('/_admin/admin/roles')({
  component: AdminRolesPage,
});

function AdminRolesPage() {
  const { data: roles, isLoading } = useRoles();
  const { data: permissions } = usePermissions();
  const { mutate: deleteRole } = useDeleteRole();
  const [selectedRole, setSelectedRole] = useState<RoleResponse | null>(null);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [deleteId, setDeleteId] = useState<number | null>(null);

  if (isLoading) {
    return (
      <div className="flex justify-center items-center min-h-[400px]">
        <Loader2 className="h-8 w-8 animate-spin" />
      </div>
    );
  }

  // Group permissions by module
  const permissionsByModule = permissions?.reduce((acc, permission) => {
    const module = permission.module || 'OTHER';
    if (!acc[module]) {
      acc[module] = [];
    }
    acc[module].push(permission);
    return acc;
  }, {} as Record<string, typeof permissions>);

  return (
    <div className="container mx-auto py-8">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">Role Management</h1>
        <Button
          onClick={() => {
            setSelectedRole(null);
            setIsFormOpen(true);
          }}
        >
          <Plus className="mr-2 h-4 w-4" />
          Add Role
        </Button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {roles?.map((role) => (
          <Card key={role.id} className="relative">
            {role.name === 'ADMIN' && (
              <Badge className="absolute top-2 right-2" variant="destructive">
                System
              </Badge>
            )}
            <CardHeader>
              <CardTitle className="flex items-center justify-between">
                <div className="flex items-center space-x-2">
                  <Shield className="h-5 w-5" />
                  <span>{role.name}</span>
                </div>
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                <div>
                  <p className="text-sm text-muted-foreground mb-2">
                    Permissions ({role.permissionIds?.length || 0})
                  </p>
                  <div className="flex flex-wrap gap-1">
                    {role.permissionIds?.slice(0, 5).map((permission) => (
                      <Badge key={permission.id} variant="secondary" className="text-xs">
                        {permission.module}
                      </Badge>
                    ))}
                    {role.permissionIds && role.permissionIds.length > 5 && (
                      <Badge variant="secondary" className="text-xs">
                        +{role.permissionIds.length - 5} more
                      </Badge>
                    )}
                  </div>
                </div>

                <div className="flex space-x-2">
                  <Button
                    variant="outline"
                    size="sm"
                    className="flex-1"
                    onClick={() => {
                      setSelectedRole(role);
                      setIsFormOpen(true);
                    }}
                    disabled={role.name === 'ADMIN'}
                  >
                    <Edit className="h-4 w-4 mr-1" />
                    Edit
                  </Button>
                  <Button
                    variant="outline"
                    size="sm"
                    className="flex-1"
                    onClick={() => setDeleteId(role.id!)}
                    disabled={role.name === 'ADMIN' || role.name === 'USER'}
                  >
                    <Trash2 className="h-4 w-4 mr-1" />
                    Delete
                  </Button>
                </div>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      <RoleFormDialog
        open={isFormOpen}
        onOpenChange={setIsFormOpen}
        role={selectedRole}
        permissions={permissions || []}
        permissionsByModule={permissionsByModule || {}}
        onSuccess={() => {
          setIsFormOpen(false);
          setSelectedRole(null);
        }}
      />

      <AlertDialog open={!!deleteId} onOpenChange={() => setDeleteId(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Delete Role</AlertDialogTitle>
            <AlertDialogDescription>
              Are you sure you want to delete this role? Users with this role will lose their
              permissions.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancel</AlertDialogCancel>
            <AlertDialogAction
              onClick={() => {
                if (deleteId) {
                  deleteRole(deleteId);
                  setDeleteId(null);
                }
              }}
            >
              Delete
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}