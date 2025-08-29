// src/features/roles/components/RoleFormDialog.tsx
import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { Checkbox } from '@/components/ui/checkbox';
import { ScrollArea } from '@/components/ui/scroll-area';
import { useCreateRole, useUpdateRole } from '../hooks/useRoles';
import { Loader2 } from 'lucide-react';
import type { components } from '@/types/api';

type RoleResponse = components['schemas']['RoleResponse'];
type PermissionResponse = components['schemas']['PermissionResponse'];

const roleSchema = z.object({
  name: z.string().min(1, 'Name is required'),
  permissionIds: z.array(z.number()).min(1, 'Select at least one permission'),
});

type RoleFormData = z.infer<typeof roleSchema>;

interface RoleFormDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  role: RoleResponse | null;
  permissions: PermissionResponse[];
  permissionsByModule: Record<string, PermissionResponse[]>;
  onSuccess?: () => void;
}

export const RoleFormDialog = ({
  open,
  onOpenChange,
  role,
  permissions,
  permissionsByModule,
  onSuccess,
}: RoleFormDialogProps) => {
  const { mutate: createRole, isPending: isCreating } = useCreateRole();
  const { mutate: updateRole, isPending: isUpdating } = useUpdateRole();
  const [selectedPermissions, setSelectedPermissions] = useState<number[]>([]);

  const form = useForm<RoleFormData>({
    resolver: zodResolver(roleSchema),
    defaultValues: {
      name: '',
      permissionIds: [],
    },
  });

  useEffect(() => {
    if (role) {
      const permissionIds = role.permissionIds?.map(p => p.id!).filter(Boolean) || [];
      form.reset({
        name: role.name || '',
        permissionIds,
      });
      setSelectedPermissions(permissionIds);
    } else {
      form.reset();
      setSelectedPermissions([]);
    }
  }, [role, form]);

  const handlePermissionToggle = (permissionId: number) => {
    const newPermissions = selectedPermissions.includes(permissionId)
      ? selectedPermissions.filter(id => id !== permissionId)
      : [...selectedPermissions, permissionId];
    
    setSelectedPermissions(newPermissions);
    form.setValue('permissionIds', newPermissions);
  };

  const handleModuleToggle = (modulePermissions: PermissionResponse[]) => {
    const moduleIds = modulePermissions.map(p => p.id!).filter(Boolean);
    const allSelected = moduleIds.every(id => selectedPermissions.includes(id));
    
    let newPermissions: number[];
    if (allSelected) {
      newPermissions = selectedPermissions.filter(id => !moduleIds.includes(id));
    } else {
      newPermissions = [...new Set([...selectedPermissions, ...moduleIds])];
    }
    
    setSelectedPermissions(newPermissions);
    form.setValue('permissionIds', newPermissions);
  };

  const onSubmit = (data: RoleFormData) => {
    if (role?.id) {
      updateRole(
        { id: role.id, data },
        {
          onSuccess: () => {
            onSuccess?.();
          },
        }
      );
    } else {
      createRole(data, {
        onSuccess: () => {
          onSuccess?.();
        },
      });
    }
  };

  const isPending = isCreating || isUpdating;

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-3xl">
        <DialogHeader>
          <DialogTitle>{role ? 'Edit Role' : 'Create Role'}</DialogTitle>
        </DialogHeader>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="name"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Role Name</FormLabel>
                  <FormControl>
                    <Input {...field} disabled={isPending} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="permissionIds"
              render={() => (
                <FormItem>
                  <FormLabel>Permissions</FormLabel>
                  <ScrollArea className="h-[400px] w-full rounded-md border p-4">
                    {Object.entries(permissionsByModule).map(([module, modulePermissions]) => (
                      <div key={module} className="mb-6">
                        <div className="flex items-center space-x-2 mb-3">
                          <Checkbox
                            checked={modulePermissions.every(p => 
                              selectedPermissions.includes(p.id!)
                            )}
                            onCheckedChange={() => handleModuleToggle(modulePermissions)}
                            disabled={isPending}
                          />
                          <span className="font-medium">{module}</span>
                        </div>
                        <div className="ml-6 space-y-2">
                          {modulePermissions.map((permission) => (
                            <div key={permission.id} className="flex items-center space-x-2">
                              <Checkbox
                                checked={selectedPermissions.includes(permission.id!)}
                                onCheckedChange={() => handlePermissionToggle(permission.id!)}
                                disabled={isPending}
                              />
                              <label className="text-sm">
                                {permission.name} ({permission.method} {permission.apiPath})
                              </label>
                            </div>
                          ))}
                        </div>
                      </div>
                    ))}
                  </ScrollArea>
                  <FormMessage />
                </FormItem>
              )}
            />

            <div className="flex justify-end space-x-2">
              <Button
                type="button"
                variant="outline"
                onClick={() => onOpenChange(false)}
                disabled={isPending}
              >
                Cancel
              </Button>
              <Button type="submit" disabled={isPending}>
                {isPending && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                {role ? 'Update' : 'Create'}
              </Button>
            </div>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
};