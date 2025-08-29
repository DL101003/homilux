// src/routes/_authenticated/bookings.$id.tsx
import { createFileRoute } from '@tanstack/react-router';
import { useBooking } from '@/features/bookings/hooks/useBookings';
import { PaymentList } from '@/features/payments/components/PaymentList';
import { CreatePaymentDialog } from '@/features/payments/components/CreatePaymentDialog';
import { Badge } from '@/components/ui/badge';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { formatCurrency, formatDateTime } from '@/lib/utils';
import { BOOKING_STATUS, LOCATION_TYPE } from '@/lib/constants';
import { Loader2, MapPin, Users, Calendar } from 'lucide-react';
import { usePermission } from '@/features/auth/hooks/useAuth';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';

export const Route = createFileRoute('/_authenticated/bookings/$id')({
  component: BookingDetailPage,
});

function BookingDetailPage() {
  const { id } = Route.useParams();
  const { data: booking, isLoading } = useBooking(Number(id));
  const { hasPermission } = usePermission();

  if (isLoading) {
    return (
      <div className="flex justify-center items-center min-h-[400px]">
        <Loader2 className="h-8 w-8 animate-spin" />
      </div>
    );
  }

  if (!booking) {
    return <div>Booking not found</div>;
  }

  const remainingAmount = Number(booking.totalAmount) - Number(booking.amountPaid || 0);
  const canManagePayments = hasPermission('/api/v1/bookings/{bookingId}/payments', 'POST');

  return (
    <div className="container mx-auto py-8 space-y-6">
      {/* Header */}
      <div className="flex justify-between items-start">
        <div>
          <h1 className="text-3xl font-bold">Booking #{booking.id}</h1>
          <p className="text-muted-foreground">
            Created on {formatDateTime(booking.createdAt)}
          </p>
        </div>
        <Badge
          className={
            booking.status ? BOOKING_STATUS[booking.status].color : ''
          }
        >
          {booking.status ? BOOKING_STATUS[booking.status].label : 'Unknown'}
        </Badge>
      </div>

      {/* Summary Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Total Amount</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{formatCurrency(booking.totalAmount)}</div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Amount Paid</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-green-600">
              {formatCurrency(booking.amountPaid)}
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Remaining</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-orange-600">
              {formatCurrency(remainingAmount)}
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Details Tabs */}
      <Tabs defaultValue="details" className="w-full">
        <TabsList>
          <TabsTrigger value="details">Details</TabsTrigger>
          <TabsTrigger value="services">Services</TabsTrigger>
          <TabsTrigger value="payments">Payments</TabsTrigger>
        </TabsList>

        <TabsContent value="details" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>Event Information</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div className="flex items-center space-x-2">
                  <Calendar className="h-4 w-4 text-muted-foreground" />
                  <span className="font-medium">Event Date:</span>
                  <span>{formatDateTime(booking.eventDatetime)}</span>
                </div>
                <div className="flex items-center space-x-2">
                  <Users className="h-4 w-4 text-muted-foreground" />
                  <span className="font-medium">Expected Guests:</span>
                  <span>{booking.expectedGuests}</span>
                </div>
                <div className="flex items-center space-x-2">
                  <MapPin className="h-4 w-4 text-muted-foreground" />
                  <span className="font-medium">Location Type:</span>
                  <span>
                    {booking.locationType
                      ? LOCATION_TYPE[booking.locationType]
                      : '-'}
                  </span>
                </div>
                {booking.address && (
                  <div className="flex items-center space-x-2">
                    <span className="font-medium">Address:</span>
                    <span>{booking.address}</span>
                  </div>
                )}
              </div>
              {booking.customerNotes && (
                <div>
                  <p className="font-medium mb-2">Customer Notes:</p>
                  <p className="text-muted-foreground">{booking.customerNotes}</p>
                </div>
              )}
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="services" className="space-y-4">
          <Card>
            <CardHeader>
              <CardTitle>Booked Services</CardTitle>
            </CardHeader>
            <CardContent>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Service</TableHead>
                    <TableHead className="text-right">Quantity</TableHead>
                    <TableHead className="text-right">Unit Price</TableHead>
                    <TableHead className="text-right">Total</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {booking.bookedServices?.map((service) => (
                    <TableRow key={service.serviceId}>
                      <TableCell>{service.serviceName}</TableCell>
                      <TableCell className="text-right">{service.quantity}</TableCell>
                      <TableCell className="text-right">
                        {formatCurrency(service.priceAtBooking)}
                      </TableCell>
                      <TableCell className="text-right font-medium">
                        {formatCurrency(service.lineTotal)}
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </CardContent>
          </Card>
        </TabsContent>

        <TabsContent value="payments" className="space-y-4">
          <div className="flex justify-between items-center mb-4">
            <h3 className="text-lg font-semibold">Payment History</h3>
            {remainingAmount > 0 && canManagePayments && (
              <CreatePaymentDialog
                bookingId={Number(id)}
                remainingAmount={remainingAmount}
              />
            )}
          </div>
          <PaymentList
            bookingId={Number(id)}
            canDelete={hasPermission('/api/v1/payments/{paymentId}', 'DELETE')}
          />
        </TabsContent>
      </Tabs>
    </div>
  );
}