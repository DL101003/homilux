// src/routes/_admin/admin.event-types.tsx
import { createFileRoute } from '@tanstack/react-router';
import { useState } from 'react';
import { useEventTypes, useDeleteEventType } from '@/features/event-types/hooks/useEventTypes';
import { Button } from '@/components/ui/button';
import { Plus, Edit, Trash2, Loader2, Calendar } from 'lucide-react';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
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
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useCreateEventType, useUpdateEventType } from '@/features/event-types/hooks/useEventTypes';
import type { components } from '@/types/api';

type EventTypeResponse = components['schemas']['EventTypeResponse'];

const eventTypeSchema = z.object({
  name: z.string().min(1, 'Name is required'),
});

type EventTypeFormData = z.infer<typeof eventTypeSchema>;

export const Route = createFileRoute('/_admin/admin/event-types')({
  component: AdminEventTypesPage,
});

function AdminEventTypesPage() {
  const { data: eventTypes, isLoading } = useEventTypes();
  const { mutate: deleteEventType } = useDeleteEventType();
  const { mutate: createEventType, isPending: isCreating } = useCreateEventType();
  const { mutate: updateEventType, isPending: isUpdating } = useUpdateEventType();
  
  const [selectedEventType, setSelectedEventType] = useState<EventTypeResponse | null>(null);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [deleteId, setDeleteId] = useState<number | null>(null);

  const form = useForm<EventTypeFormData>({
    resolver: zodResolver(eventTypeSchema),
    defaultValues: {
      name: '',
    },
  });

  const onSubmit = (data: EventTypeFormData) => {
    if (selectedEventType?.id) {
      updateEventType(
        { id: selectedEventType.id, data },
        {
          onSuccess: () => {
            setIsFormOpen(false);
            setSelectedEventType(null);
            form.reset();
          },
        }
      );
    } else {
      createEventType(data, {
        onSuccess: () => {
          setIsFormOpen(false);
          form.reset();
        },
      });
    }
  };

  const openEditDialog = (eventType: EventTypeResponse) => {
    setSelectedEventType(eventType);
    form.reset({ name: eventType.name || '' });
    setIsFormOpen(true);
  };

  const openCreateDialog = () => {
    setSelectedEventType(null);
    form.reset({ name: '' });
    setIsFormOpen(true);
  };

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
        <div className="flex items-center space-x-2">
          <Calendar className="h-8 w-8" />
          <h1 className="text-3xl font-bold">Event Types</h1>
        </div>
        <Button onClick={openCreateDialog}>
          <Plus className="mr-2 h-4 w-4" />
          Add Event Type
        </Button>
      </div>

      <div className="rounded-md border">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>ID</TableHead>
              <TableHead>Name</TableHead>
              <TableHead className="w-[100px]">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {eventTypes?.map((eventType) => (
              <TableRow key={eventType.id}>
                <TableCell className="font-medium">{eventType.id}</TableCell>
                <TableCell>{eventType.name}</TableCell>
                <TableCell>
                  <div className="flex space-x-2">
                    <Button
                      variant="ghost"
                      size="icon"
                      onClick={() => openEditDialog(eventType)}
                    >
                      <Edit className="h-4 w-4" />
                    </Button>
                    <Button
                      variant="ghost"
                      size="icon"
                      onClick={() => setDeleteId(eventType.id!)}
                    >
                      <Trash2 className="h-4 w-4" />
                    </Button>
                  </div>
                </TableCell>
              </TableRow>
            ))}
            {(!eventTypes || eventTypes.length === 0) && (
              <TableRow>
                <TableCell colSpan={3} className="text-center text-muted-foreground">
                  No event types found
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>

      {/* Create/Edit Dialog */}
      <Dialog open={isFormOpen} onOpenChange={setIsFormOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>
              {selectedEventType ? 'Edit Event Type' : 'Create Event Type'}
            </DialogTitle>
          </DialogHeader>
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
              <FormField
                control={form.control}
                name="name"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Name</FormLabel>
                    <FormControl>
                      <Input {...field} disabled={isCreating || isUpdating} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <DialogFooter>
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => setIsFormOpen(false)}
                  disabled={isCreating || isUpdating}
                >
                  Cancel
                </Button>
                <Button type="submit" disabled={isCreating || isUpdating}>
                  {(isCreating || isUpdating) && (
                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  )}
                  {selectedEventType ? 'Update' : 'Create'}
                </Button>
              </DialogFooter>
            </form>
          </Form>
        </DialogContent>
      </Dialog>

      {/* Delete Confirmation Dialog */}
      <AlertDialog open={!!deleteId} onOpenChange={() => setDeleteId(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Delete Event Type</AlertDialogTitle>
            <AlertDialogDescription>
              Are you sure you want to delete this event type? This action cannot be undone.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancel</AlertDialogCancel>
            <AlertDialogAction
              onClick={() => {
                if (deleteId) {
                  deleteEventType(deleteId);
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