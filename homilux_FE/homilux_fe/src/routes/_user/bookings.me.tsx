// src/routes/_user/bookings.me.tsx
import { createFileRoute, useNavigate } from '@tanstack/react-router';
import { useState } from 'react';
import { useMyBookings } from '@/features/bookings/hooks/useBookings';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { formatCurrency, formatDateTime } from '@/lib/utils';
import { BOOKING_STATUS, LOCATION_TYPE } from '@/lib/constants';
import { Calendar, MapPin, Users, DollarSign, ChevronLeft, ChevronRight, Loader2 } from 'lucide-react';
import type { components } from '@/types/api';

type BookingResponse = components['schemas']['BookingResponse'];

export const Route = createFileRoute('/_user/bookings/me')({
  component: MyBookingsPage,
});

function MyBookingsPage() {
  const [page, setPage] = useState(0);
  const { data, isLoading } = useMyBookings(page, 6);
  const navigate = useNavigate();

  const bookings = (data?.result as BookingResponse[]) || [];
  const meta = data?.meta;

  if (isLoading) {
    return (
      <div className="flex justify-center items-center min-h-[400px]">
        <Loader2 className="h-8 w-8 animate-spin" />
      </div>
    );
  }

  return (
    <div className="container mx-auto py-8">
      <h1 className="text-3xl font-bold mb-6">My Bookings</h1>

      {bookings.length === 0 ? (
        <Card>
          <CardContent className="text-center py-12">
            <p className="text-muted-foreground mb-4">You haven't made any bookings yet.</p>
            <Button onClick={() => navigate({ to: '/booking/new' })}>
              Create Your First Booking
            </Button>
          </CardContent>
        </Card>
      ) : (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {bookings.map((booking) => (
              <Card
                key={booking.id}
                className="cursor-pointer hover:shadow-lg transition-shadow"
                onClick={() => navigate({ to: '/bookings/$id', params: { id: booking.id!.toString() } })}
              >
                <CardHeader>
                  <div className="flex justify-between items-start">
                    <CardTitle className="text-lg">
                      Booking #{booking.id}
                    </CardTitle>
                    <Badge
                      className={booking.status ? BOOKING_STATUS[booking.status].color : ''}
                    >
                      {booking.status ? BOOKING_STATUS[booking.status].label : 'Unknown'}
                    </Badge>
                  </div>
                  <p className="text-sm text-muted-foreground">
                    {booking.eventType?.name}
                  </p>
                </CardHeader>
                <CardContent className="space-y-3">
                  <div className="flex items-center text-sm">
                    <Calendar className="h-4 w-4 mr-2 text-muted-foreground" />
                    {formatDateTime(booking.eventDatetime)}
                  </div>
                  <div className="flex items-center text-sm">
                    <Users className="h-4 w-4 mr-2 text-muted-foreground" />
                    {booking.expectedGuests} guests
                  </div>
                  <div className="flex items-center text-sm">
                    <MapPin className="h-4 w-4 mr-2 text-muted-foreground" />
                    {booking.locationType ? LOCATION_TYPE[booking.locationType] : '-'}
                  </div>
                  <div className="flex items-center text-sm">
                    <DollarSign className="h-4 w-4 mr-2 text-muted-foreground" />
                    <div>
                      <span className="font-medium">{formatCurrency(booking.totalAmount)}</span>
                      <span className="text-muted-foreground"> • Paid: </span>
                      <span className="text-green-600">{formatCurrency(booking.amountPaid)}</span>
                    </div>
                  </div>
                  
                  {/* Quick Actions */}
                  <div className="pt-3 border-t">
                    <div className="flex space-x-2">
                      {booking.status === 'COMPLETED' && (
                        <Button
                          variant="outline"
                          size="sm"
                          className="flex-1"
                          onClick={(e) => {
                            e.stopPropagation();
                            navigate({ to: '/bookings/$id/review', params: { id: booking.id!.toString() } });
                          }}
                        >
                          Add Review
                        </Button>
                      )}
                      {Number(booking.totalAmount) - Number(booking.amountPaid || 0) > 0 && (
                        <Button
                          size="sm"
                          className="flex-1"
                          onClick={(e) => {
                            e.stopPropagation();
                            navigate({ to: '/bookings/$id', params: { id: booking.id!.toString() }, hash: 'payments' });
                          }}
                        >
                          Pay Now
                        </Button>
                      )}
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>

          {/* Pagination */}
          {meta && meta.pages > 1 && (
            <div className="flex items-center justify-center mt-8 space-x-2">
              <Button
                variant="outline"
                size="sm"
                onClick={() => setPage(page - 1)}
                disabled={page === 0}
              >
                <ChevronLeft className="h-4 w-4" />
                Previous
              </Button>
              <span className="text-sm text-muted-foreground">
                Page {meta.page + 1} of {meta.pages}
              </span>
              <Button
                variant="outline"
                size="sm"
                onClick={() => setPage(page + 1)}
                disabled={page >= meta.pages - 1}
              >
                Next
                <ChevronRight className="h-4 w-4" />
              </Button>
            </div>
          )}
        </>
      )}
    </div>
  );
}