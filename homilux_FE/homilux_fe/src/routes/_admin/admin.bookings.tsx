// src/routes/_admin/admin.bookings.tsx
import { createFileRoute, useNavigate } from '@tanstack/react-router';
import { useState } from 'react';
import { useAllBookings, useUpdateBookingStatus } from '@/features/bookings/hooks/useBookings';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { formatCurrency, formatDateTime } from '@/lib/utils';
import { BOOKING_STATUS, LOCATION_TYPE } from '@/lib/constants';
import { ChevronLeft, ChevronRight, Eye, Loader2 } from 'lucide-react';
import type { components } from '@/types/api';
import { LoadingSpinner } from '@/components/shared/LoadingSpinner';

type BookingResponse = components['schemas']['BookingResponse'];
type BookingStatus = NonNullable<BookingResponse['status']>;

export const Route = createFileRoute('/_admin/admin/bookings')({
  component: AdminBookingsPage,
});

function AdminBookingsPage() {
  const [page, setPage] = useState(0);
  const [searchTerm, setSearchTerm] = useState('');
  const { data, isLoading } = useAllBookings(page, 10);
  const { mutate: updateStatus } = useUpdateBookingStatus();
  const navigate = useNavigate();

  const bookings = (data?.result as BookingResponse[]) || [];
  const meta = data?.meta;

  const filteredBookings = bookings.filter(
    (booking) =>
      booking.user?.fullName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      booking.user?.email?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      booking.id?.toString().includes(searchTerm)
  );

  const handleStatusChange = (bookingId: number, newStatus: BookingStatus) => {
    updateStatus({ id: bookingId, data: { status: newStatus } });
  };

  if (isLoading) {
    return <LoadingSpinner size="lg" text="Loading bookings..." />;
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold">Booking Management</h1>
        <Button onClick={() => navigate({ to: '/booking/new' })}>
          Create Booking
        </Button>
      </div>

      <Input
        placeholder="Search by customer name, email or booking ID..."
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
        className="max-w-md"
      />

      <div className="rounded-md border">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>ID</TableHead>
              <TableHead>Customer</TableHead>
              <TableHead>Event Type</TableHead>
              <TableHead>Date</TableHead>
              <TableHead>Location</TableHead>
              <TableHead>Total</TableHead>
              <TableHead>Paid</TableHead>
              <TableHead>Status</TableHead>
              <TableHead>Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {filteredBookings.map((booking) => (
              <TableRow key={booking.id}>
                <TableCell>#{booking.id}</TableCell>
                <TableCell>
                  <div>
                    <p className="font-medium">{booking.user?.fullName}</p>
                    <p className="text-sm text-muted-foreground">{booking.user?.email}</p>
                  </div>
                </TableCell>
                <TableCell>{booking.eventType?.name}</TableCell>
                <TableCell>{formatDateTime(booking.eventDatetime)}</TableCell>
                <TableCell>
                  {booking.locationType ? LOCATION_TYPE[booking.locationType] : '-'}
                </TableCell>
                <TableCell>{formatCurrency(booking.totalAmount)}</TableCell>
                <TableCell>
                  <span className="text-green-600">
                    {formatCurrency(booking.amountPaid)}
                  </span>
                </TableCell>
                <TableCell>
                  <Select
                    value={booking.status}
                    onValueChange={(value) => 
                      handleStatusChange(booking.id!, value as BookingStatus)
                    }
                  >
                    <SelectTrigger className="w-[130px]">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      {Object.keys(BOOKING_STATUS).map((status) => (
                        <SelectItem key={status} value={status}>
                          <Badge
                            className={BOOKING_STATUS[status as BookingStatus].color}
                          >
                            {BOOKING_STATUS[status as BookingStatus].label}
                          </Badge>
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </TableCell>
                <TableCell>
                  <Button
                    variant="ghost"
                    size="icon"
                    onClick={() => 
                      navigate({ to: '/bookings/$id', params: { id: booking.id!.toString() } })
                    }
                  >
                    <Eye className="h-4 w-4" />
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>

      {/* Pagination */}
      {meta && (
        <div className="flex items-center justify-between">
          <p className="text-sm text-muted-foreground">
            Showing {meta.page * meta.pageSize + 1} to{' '}
            {Math.min((meta.page + 1) * meta.pageSize, meta.total)} of {meta.total} bookings
          </p>
          <div className="flex space-x-2">
            <Button
              variant="outline"
              size="sm"
              onClick={() => setPage(page - 1)}
              disabled={page === 0}
            >
              <ChevronLeft className="h-4 w-4" />
              Previous
            </Button>
            <Button
              variant="outline"
              size="sm"
              onClick={() => setPage(page + 1)}
              disabled={page >= (meta.pages || 1) - 1}
            >
              Next
              <ChevronRight className="h-4 w-4" />
            </Button>
          </div>
        </div>
      )}
    </div>
  );
}