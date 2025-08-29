// src/routes/_admin/admin.payments.tsx
import { createFileRoute } from '@tanstack/react-router';
import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { axiosClient } from '@/lib/axios';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { Badge } from '@/components/ui/badge';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { formatCurrency, formatDateTime } from '@/lib/utils';
import { PAYMENT_METHOD } from '@/lib/constants';
import { ChevronLeft, ChevronRight, Search, Loader2 } from 'lucide-react';
import type { components } from '@/types/api';

type PaymentResponse = components['schemas']['PaymentResponse'];
type ResultPaginationDto = components['schemas']['ResultPaginationDto'];

export const Route = createFileRoute('/_admin/admin/payments')({
  component: AdminPaymentsPage,
});

// API call to get all payments with pagination
const getAllPayments = async (page: number, size: number, search?: string) => {
  const params: any = { page, size };
  if (search) {
    params.search = search;
  }
  const response = await axiosClient.get<ResultPaginationDto>('/payments', { params });
  return response.data;
};

function AdminPaymentsPage() {
  const [page, setPage] = useState(0);
  const [searchTerm, setSearchTerm] = useState('');
  const [searchInput, setSearchInput] = useState('');

  const { data, isLoading } = useQuery({
    queryKey: ['admin-payments', page, searchTerm],
    queryFn: () => getAllPayments(page, 10, searchTerm),
  });

  const payments = (data?.result as PaymentResponse[]) || [];
  const meta = data?.meta;

  const handleSearch = () => {
    setSearchTerm(searchInput);
    setPage(0);
  };

  if (isLoading) {
    return (
      <div className="flex justify-center items-center min-h-[400px]">
        <Loader2 className="h-8 w-8 animate-spin" />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold">Payment Management</h1>
      </div>

      <div className="flex gap-2">
        <Input
          placeholder="Search by transaction ID or booking ID..."
          value={searchInput}
          onChange={(e) => setSearchInput(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
          className="max-w-md"
        />
        <Button onClick={handleSearch}>
          <Search className="h-4 w-4 mr-2" />
          Search
        </Button>
      </div>

      <div className="rounded-md border">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>ID</TableHead>
              <TableHead>Booking ID</TableHead>
              <TableHead>Amount</TableHead>
              <TableHead>Method</TableHead>
              <TableHead>Transaction ID</TableHead>
              <TableHead>Date</TableHead>
              <TableHead>Notes</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {payments.map((payment) => (
              <TableRow key={payment.id}>
                <TableCell>#{payment.id}</TableCell>
                <TableCell>
                  <Badge variant="outline">
                    Booking #{payment.id}
                  </Badge>
                </TableCell>
                <TableCell className="font-medium">
                  {formatCurrency(payment.amount)}
                </TableCell>
                <TableCell>
                  <Badge variant={
                    payment.paymentMethod === 'THIRD_PARTY_GATEWAY' ? 'default' :
                    payment.paymentMethod === 'BANK_TRANSFER' ? 'secondary' : 'outline'
                  }>
                    {payment.paymentMethod ? PAYMENT_METHOD[payment.paymentMethod] : '-'}
                  </Badge>
                </TableCell>
                <TableCell className="font-mono text-sm">
                  {payment.transactionId || '-'}
                </TableCell>
                <TableCell>{formatDateTime(payment.paymentDate)}</TableCell>
                <TableCell className="max-w-xs truncate">
                  {payment.notes || '-'}
                </TableCell>
              </TableRow>
            ))}
            {payments.length === 0 && (
              <TableRow>
                <TableCell colSpan={7} className="text-center text-muted-foreground">
                  No payments found
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>

      {/* Pagination */}
      {meta && meta.pages > 1 && (
        <div className="flex items-center justify-between">
          <p className="text-sm text-muted-foreground">
            Showing {meta.page * meta.pageSize + 1} to{' '}
            {Math.min((meta.page + 1) * meta.pageSize, meta.total)} of {meta.total} payments
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