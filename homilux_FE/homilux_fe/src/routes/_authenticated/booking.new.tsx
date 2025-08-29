// src/routes/_authenticated/booking.new.tsx
import { createFileRoute } from '@tanstack/react-router';
import { CreateBookingForm } from '@/features/bookings/components/CreateBookingForm';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { ArrowLeft } from 'lucide-react';
import { useNavigate } from '@tanstack/react-router';

export const Route = createFileRoute('/_authenticated/booking/new')({
  component: NewBookingPage,
});

function NewBookingPage() {
  const navigate = useNavigate();

  return (
    <div className="container mx-auto py-8 max-w-4xl">
      <Button
        variant="ghost"
        onClick={() => navigate({ to: '/' })}
        className="mb-6"
      >
        <ArrowLeft className="mr-2 h-4 w-4" />
        Back to Home
      </Button>

      <Card>
        <CardHeader>
          <CardTitle className="text-2xl">Create New Booking</CardTitle>
        </CardHeader>
        <CardContent>
          <CreateBookingForm />
        </CardContent>
      </Card>
    </div>
  );
}