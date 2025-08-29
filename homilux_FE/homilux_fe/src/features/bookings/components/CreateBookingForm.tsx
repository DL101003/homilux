// src/features/bookings/components/CreateBookingForm.tsx
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useState } from 'react';
import { Button } from '@/components/ui/button';
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { Card, CardContent } from '@/components/ui/card';
import { Checkbox } from '@/components/ui/checkbox';
import { useCreateBooking } from '../hooks/useBookings';
import { useEventTypes } from '@/features/event-types/hooks/useEventTypes';
import { useServices } from '@/features/services/hooks/useServices';
import { LOCATION_TYPE, SERVICE_TYPE } from '@/lib/constants';
import { formatCurrency } from '@/lib/utils';
import { CalendarIcon, Loader2, Plus, Minus } from 'lucide-react';
import { Calendar } from '@/components/ui/calendar';
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from '@/components/ui/popover';
import { format } from 'date-fns';
import { cn } from '@/lib/utils';
import type { components } from '@/types/api';

type RentalServiceResponse = components['schemas']['RentalServiceResponse'];

const bookingSchema = z.object({
  eventTypeId: z.number().min(1, 'Event type is required'),
  eventDatetime: z.date({
    message: 'Event date is required',
  }),
  expectedGuests: z.number().min(1, 'Number of guests is required'),
  locationType: z.enum(['RESTAURANT', 'EXTERNAL']),
  address: z.string().optional(),
  customerNotes: z.string().optional(),
  services: z.array(
    z.object({
      serviceId: z.number(),
      quantity: z.number().min(1),
    })
  ).min(1, 'Please select at least one service'),
});

type BookingFormData = z.infer<typeof bookingSchema>;

interface ServiceSelection {
  serviceId: number;
  quantity: number;
  service?: RentalServiceResponse;
}

export const CreateBookingForm = () => {
  const { mutate: createBooking, isPending } = useCreateBooking();
  const { data: eventTypes } = useEventTypes();
  const { data: services } = useServices();
  const [selectedServices, setSelectedServices] = useState<ServiceSelection[]>([]);

  const form = useForm<BookingFormData>({
    resolver: zodResolver(bookingSchema),
    defaultValues: {
      eventTypeId: 0,
      expectedGuests: 1, // Set default value instead of undefined
      locationType: 'RESTAURANT',
      address: '',
      customerNotes: '',
      services: [],
    },
  });

  const handleServiceToggle = (service: RentalServiceResponse) => {
    const existing = selectedServices.find(s => s.serviceId === service.id);
    
    if (existing) {
      const updated = selectedServices.filter(s => s.serviceId !== service.id);
      setSelectedServices(updated);
      form.setValue('services', updated);
    } else {
      const newService: ServiceSelection = {
        serviceId: service.id!,
        quantity: 1,
        service,
      };
      const updated = [...selectedServices, newService];
      setSelectedServices(updated);
      form.setValue('services', updated);
    }
  };

  const handleQuantityChange = (serviceId: number, quantity: number) => {
    if (quantity < 1) return;
    
    const updated = selectedServices.map(s => 
      s.serviceId === serviceId ? { ...s, quantity } : s
    );
    setSelectedServices(updated);
    form.setValue('services', updated);
  };

  const calculateTotal = () => {
    return selectedServices.reduce((total, item) => {
      const price = Number(item.service?.price || 0);
      return total + (price * item.quantity);
    }, 0);
  };

  const onSubmit = (data: BookingFormData) => {
    createBooking({
      ...data,
      eventDatetime: data.eventDatetime.toISOString(),
    });
  };

  // Group services by type
  const servicesByType = services?.reduce((acc, service) => {
    const type = service.type || 'OTHER';
    if (!acc[type]) {
      acc[type] = [];
    }
    acc[type].push(service);
    return acc;
  }, {} as Record<string, typeof services>);

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
        {/* Basic Information */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <FormField
            control={form.control}
            name="eventTypeId"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Event Type *</FormLabel>
                <Select
                  onValueChange={(value) => field.onChange(Number(value))}
                  value={field.value?.toString()}
                  disabled={isPending}
                >
                  <FormControl>
                    <SelectTrigger>
                      <SelectValue placeholder="Select event type" />
                    </SelectTrigger>
                  </FormControl>
                  <SelectContent>
                    {eventTypes?.map((type) => (
                      <SelectItem key={type.id} value={type.id!.toString()}>
                        {type.name}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
                <FormMessage />
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="eventDatetime"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Event Date & Time *</FormLabel>
                <Popover>
                  <PopoverTrigger asChild>
                    <FormControl>
                      <Button
                        variant="outline"
                        className={cn(
                          'w-full pl-3 text-left font-normal',
                          !field.value && 'text-muted-foreground'
                        )}
                        disabled={isPending}
                      >
                        {field.value ? (
                          format(field.value, 'PPP')
                        ) : (
                          <span>Pick a date</span>
                        )}
                        <CalendarIcon className="ml-auto h-4 w-4 opacity-50" />
                      </Button>
                    </FormControl>
                  </PopoverTrigger>
                  <PopoverContent className="w-auto p-0" align="start">
                    <Calendar
                      mode="single"
                      selected={field.value}
                      onSelect={field.onChange}
                      disabled={(date) =>
                        date < new Date() || date < new Date('1900-01-01')
                      }
                      initialFocus
                    />
                  </PopoverContent>
                </Popover>
                <FormMessage />
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="expectedGuests"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Expected Guests *</FormLabel>
                <FormControl>
                  <Input
                    type="number"
                    placeholder="100"
                    {...field}
                    value={field.value || ''}
                    onChange={(e) => field.onChange(parseInt(e.target.value) || 1)}
                    disabled={isPending}
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="locationType"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Location Type *</FormLabel>
                <Select
                  onValueChange={field.onChange}
                  defaultValue={field.value}
                  disabled={isPending}
                >
                  <FormControl>
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                  </FormControl>
                  <SelectContent>
                    {Object.entries(LOCATION_TYPE).map(([key, label]) => (
                      <SelectItem key={key} value={key}>
                        {label}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
                <FormMessage />
              </FormItem>
            )}
          />
        </div>

        {form.watch('locationType') === 'EXTERNAL' && (
          <FormField
            control={form.control}
            name="address"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Address</FormLabel>
                <FormControl>
                  <Input
                    placeholder="Enter event address"
                    {...field}
                    disabled={isPending}
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        )}

        <FormField
          control={form.control}
          name="customerNotes"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Notes (Optional)</FormLabel>
              <FormControl>
                <Textarea
                  placeholder="Any special requirements..."
                  {...field}
                  disabled={isPending}
                />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        {/* Services Selection */}
        <div className="space-y-4">
          <FormLabel>Services *</FormLabel>
          <FormMessage>{form.formState.errors.services?.message}</FormMessage>
          
          {servicesByType && Object.entries(servicesByType).map(([type, typeServices]) => (
            <div key={type} className="space-y-3">
              <h4 className="font-medium text-sm text-muted-foreground">
                {SERVICE_TYPE[type as keyof typeof SERVICE_TYPE] || type}
              </h4>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                {typeServices.map((service) => {
                  const isSelected = selectedServices.some(s => s.serviceId === service.id);
                  const selection = selectedServices.find(s => s.serviceId === service.id);
                  
                  return (
                    <Card 
                      key={service.id}
                      className={cn(
                        "cursor-pointer transition-colors",
                        isSelected && "border-primary bg-primary/5"
                      )}
                    >
                      <CardContent className="p-4">
                        <div className="flex items-start space-x-3">
                          <Checkbox
                            checked={isSelected}
                            onCheckedChange={() => handleServiceToggle(service)}
                            disabled={isPending}
                          />
                          <div className="flex-1">
                            <div className="flex items-start justify-between">
                              <div className="flex-1">
                                <p className="font-medium">{service.name}</p>
                                {service.description && (
                                  <p className="text-sm text-muted-foreground mt-1">
                                    {service.description}
                                  </p>
                                )}
                                <p className="text-sm font-medium mt-2">
                                  {formatCurrency(service.price)}
                                </p>
                              </div>
                            </div>
                            
                            {isSelected && (
                              <div className="flex items-center space-x-2 mt-3">
                                <Button
                                  type="button"
                                  variant="outline"
                                  size="icon"
                                  className="h-8 w-8"
                                  onClick={() => handleQuantityChange(service.id!, selection!.quantity - 1)}
                                  disabled={isPending || selection!.quantity <= 1}
                                >
                                  <Minus className="h-3 w-3" />
                                </Button>
                                <Input
                                  type="number"
                                  value={selection!.quantity}
                                  onChange={(e) => handleQuantityChange(service.id!, parseInt(e.target.value) || 1)}
                                  className="w-16 h-8 text-center"
                                  disabled={isPending}
                                />
                                <Button
                                  type="button"
                                  variant="outline"
                                  size="icon"
                                  className="h-8 w-8"
                                  onClick={() => handleQuantityChange(service.id!, selection!.quantity + 1)}
                                  disabled={isPending}
                                >
                                  <Plus className="h-3 w-3" />
                                </Button>
                              </div>
                            )}
                          </div>
                        </div>
                      </CardContent>
                    </Card>
                  );
                })}
              </div>
            </div>
          ))}
        </div>

        {/* Total */}
        {selectedServices.length > 0 && (
          <Card>
            <CardContent className="p-4">
              <div className="flex justify-between items-center">
                <span className="text-lg font-semibold">Estimated Total:</span>
                <span className="text-2xl font-bold text-primary">
                  {formatCurrency(calculateTotal())}
                </span>
              </div>
            </CardContent>
          </Card>
        )}

        <Button type="submit" disabled={isPending} className="w-full">
          {isPending && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
          {isPending ? 'Creating booking...' : 'Create Booking'}
        </Button>
      </form>
    </Form>
  );
};