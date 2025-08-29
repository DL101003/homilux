// src/routes/_admin/admin.services.tsx
import { createFileRoute } from '@tanstack/react-router';
import { useState } from 'react';
import { useServices, useDeleteService } from '@/features/services/hooks/useServices';
import { ServiceFormDialog } from '@/features/services/components/ServiceFormDialog';
import { DataTable } from '@/components/shared/DataTable';
import { Button } from '@/components/ui/button';
import { formatCurrency } from '@/lib/utils';
import { SERVICE_TYPE } from '@/lib/constants';
import { Plus, Edit, Trash2, Loader2 } from 'lucide-react';
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
import type { ColumnDef } from '@tanstack/react-table';
import type { components } from '@/types/api';

type RentalServiceResponse = components['schemas']['RentalServiceResponse'];

export const Route = createFileRoute('/_admin/admin/services')({
  component: AdminServicesPage,
});

function AdminServicesPage() {
  const { data: services, isLoading } = useServices();
  const { mutate: deleteService } = useDeleteService();
  const [selectedService, setSelectedService] = useState<RentalServiceResponse | null>(null);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [deleteId, setDeleteId] = useState<number | null>(null);

  const columns: ColumnDef<RentalServiceResponse>[] = [
    {
      accessorKey: 'name',
      header: 'Name',
    },
    {
      accessorKey: 'type',
      header: 'Type',
      cell: ({ row }) => {
        const type = row.original.type;
        return type ? SERVICE_TYPE[type] : '-';
      },
    },
    {
      accessorKey: 'price',
      header: 'Price',
      cell: ({ row }) => formatCurrency(row.original.price),
    },
    {
      accessorKey: 'isCustomizable',
      header: 'Customizable',
      cell: ({ row }) => (row.original.isCustomizable ? 'Yes' : 'No'),
    },
    {
      id: 'actions',
      header: 'Actions',
      cell: ({ row }) => (
        <div className="flex space-x-2">
          <Button
            variant="ghost"
            size="icon"
            onClick={() => {
              setSelectedService(row.original);
              setIsFormOpen(true);
            }}
          >
            <Edit className="h-4 w-4" />
          </Button>
          <Button
            variant="ghost"
            size="icon"
            onClick={() => setDeleteId(row.original.id!)}
          >
            <Trash2 className="h-4 w-4" />
          </Button>
        </div>
      ),
    },
  ];

  if (isLoading) {
    return (
      <div className="flex justify-center items-center min-h-[400px]">
        <Loader2 className="h-8 w-8 animate-spin" />
      </div>
    );
  }

  return (
    <div className="container mx-auto py-8">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold">Service Management</h1>
        <Button
          onClick={() => {
            setSelectedService(null);
            setIsFormOpen(true);
          }}
        >
          <Plus className="mr-2 h-4 w-4" />
          Add Service
        </Button>
      </div>

      <DataTable
        columns={columns}
        data={services || []}
        searchKey="name"
        searchPlaceholder="Search services..."
      />

      <ServiceFormDialog
        open={isFormOpen}
        onOpenChange={setIsFormOpen}
        service={selectedService}
        onSuccess={() => {
          setIsFormOpen(false);
          setSelectedService(null);
        }}
      />

      <AlertDialog open={!!deleteId} onOpenChange={() => setDeleteId(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Delete Service</AlertDialogTitle>
            <AlertDialogDescription>
              Are you sure you want to delete this service? This action cannot be undone.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancel</AlertDialogCancel>
            <AlertDialogAction
              onClick={() => {
                if (deleteId) {
                  deleteService(deleteId);
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