// src/features/payments/components/CreatePaymentDialog.tsx
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
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
import { Textarea } from '@/components/ui/textarea';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import { useCreatePayment, useVNPayPayment } from '../hooks/usePayments';
import { PAYMENT_METHOD } from '@/lib/constants';
import { formatCurrency } from '@/lib/utils';
import { Plus, CreditCard, Loader2 } from 'lucide-react';

const paymentSchema = z.object({
  amount: z.number().min(1000, 'Amount must be at least 1,000 VND'),
  paymentMethod: z.enum(['CASH', 'BANK_TRANSFER', 'THIRD_PARTY_GATEWAY']),
  transactionId: z.string().optional(),
  notes: z.string().optional(),
});

type PaymentFormData = z.infer<typeof paymentSchema>;

interface CreatePaymentDialogProps {
  bookingId: number;
  remainingAmount: number;
}

export const CreatePaymentDialog = ({
  bookingId,
  remainingAmount,
}: CreatePaymentDialogProps) => {
  const [open, setOpen] = useState(false);
  const { mutate: createPayment, isPending: isCreating } = useCreatePayment(bookingId);
  const { mutate: createVNPay, isPending: isVNPayLoading } = useVNPayPayment();

  const form = useForm<PaymentFormData>({
    resolver: zodResolver(paymentSchema),
    defaultValues: {
      amount: remainingAmount,
      paymentMethod: 'CASH',
    },
  });

  const onSubmit = (data: PaymentFormData) => {
    if (data.paymentMethod === 'THIRD_PARTY_GATEWAY') {
      // Use VNPay for online payment
      createVNPay(bookingId);
    } else {
      // Record manual payment
      createPayment(data, {
        onSuccess: () => {
          setOpen(false);
          form.reset();
        },
      });
    }
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button>
          <Plus className="mr-2 h-4 w-4" />
          Add Payment
        </Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-[500px]">
        <DialogHeader>
          <DialogTitle>Record Payment</DialogTitle>
          <DialogDescription>
            Remaining amount: {formatCurrency(remainingAmount)}
          </DialogDescription>
        </DialogHeader>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
            <FormField
              control={form.control}
              name="paymentMethod"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Payment Method</FormLabel>
                  <Select onValueChange={field.onChange} defaultValue={field.value}>
                    <FormControl>
                      <SelectTrigger>
                        <SelectValue />
                      </SelectTrigger>
                    </FormControl>
                    <SelectContent>
                      {Object.entries(PAYMENT_METHOD).map(([key, label]) => (
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

            {form.watch('paymentMethod') !== 'THIRD_PARTY_GATEWAY' && (
              <>
                <FormField
                  control={form.control}
                  name="amount"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Amount (VND)</FormLabel>
                      <FormControl>
                        <Input
                          type="number"
                          {...field}
                          onChange={(e) => field.onChange(Number(e.target.value))}
                        />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <FormField
                  control={form.control}
                  name="transactionId"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Transaction ID (Optional)</FormLabel>
                      <FormControl>
                        <Input {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <FormField
                  control={form.control}
                  name="notes"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Notes (Optional)</FormLabel>
                      <FormControl>
                        <Textarea {...field} />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
              </>
            )}

            <DialogFooter>
              <Button type="button" variant="outline" onClick={() => setOpen(false)}>
                Cancel
              </Button>
              <Button type="submit" disabled={isCreating || isVNPayLoading}>
                {(isCreating || isVNPayLoading) && (
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                )}
                {form.watch('paymentMethod') === 'THIRD_PARTY_GATEWAY'
                  ? 'Pay with VNPay'
                  : 'Record Payment'}
              </Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
};