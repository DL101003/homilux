// src/features/payments/components/PaymentList.tsx
import { formatCurrency, formatDateTime } from '@/lib/utils';
import { PAYMENT_METHOD } from '@/lib/constants';
import { usePaymentsForBooking, useDeletePayment } from '../hooks/usePayments';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { Button } from '@/components/ui/button';
import { Trash2, Loader2 } from 'lucide-react';
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from '@/components/ui/alert-dialog';

interface PaymentListProps {
  bookingId: number;
  canDelete?: boolean;
}

export const PaymentList = ({ bookingId, canDelete = false }: PaymentListProps) => {
  const { data: payments, isLoading } = usePaymentsForBooking(bookingId);
  const { mutate: deletePayment } = useDeletePayment(bookingId);

  if (isLoading) {
    return (
      <div className="flex justify-center py-8">
        <Loader2 className="h-6 w-6 animate-spin" />
      </div>
    );
  }

  if (!payments || payments.length === 0) {
    return (
      <div className="text-center py-8 text-muted-foreground">
        No payments recorded yet
      </div>
    );
  }

  return (
    <Table>
      <TableHeader>
        <TableRow>
          <TableHead>Date</TableHead>
          <TableHead>Method</TableHead>
          <TableHead>Transaction ID</TableHead>
          <TableHead className="text-right">Amount</TableHead>
          <TableHead>Notes</TableHead>
          {canDelete && <TableHead className="w-[100px]">Actions</TableHead>}
        </TableRow>
      </TableHeader>
      <TableBody>
        {payments.map((payment) => (
          <TableRow key={payment.id}>
            <TableCell>{formatDateTime(payment.paymentDate)}</TableCell>
            <TableCell>
              {payment.paymentMethod ? PAYMENT_METHOD[payment.paymentMethod] : '-'}
            </TableCell>
            <TableCell>{payment.transactionId || '-'}</TableCell>
            <TableCell className="text-right font-medium">
              {formatCurrency(payment.amount)}
            </TableCell>
            <TableCell>{payment.notes || '-'}</TableCell>
            {canDelete && (
              <TableCell>
                <AlertDialog>
                  <AlertDialogTrigger asChild>
                    <Button variant="ghost" size="icon">
                      <Trash2 className="h-4 w-4" />
                    </Button>
                  </AlertDialogTrigger>
                  <AlertDialogContent>
                    <AlertDialogHeader>
                      <AlertDialogTitle>Delete Payment</AlertDialogTitle>
                      <AlertDialogDescription>
                        Are you sure you want to delete this payment? This will update the
                        booking's paid amount.
                      </AlertDialogDescription>
                    </AlertDialogHeader>
                    <AlertDialogFooter>
                      <AlertDialogCancel>Cancel</AlertDialogCancel>
                      <AlertDialogAction
                        onClick={() => payment.id && deletePayment(payment.id)}
                      >
                        Delete
                      </AlertDialogAction>
                    </AlertDialogFooter>
                  </AlertDialogContent>
                </AlertDialog>
              </TableCell>
            )}
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
};