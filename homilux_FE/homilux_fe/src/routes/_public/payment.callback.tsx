// src/routes/_public/payment.callback.tsx
import { createFileRoute, useNavigate } from '@tanstack/react-router';
import { useEffect, useState } from 'react';
import { paymentApi } from '@/features/payments/api/payment.api';
import { CheckCircle, XCircle, Loader2 } from 'lucide-react';
import { Button } from '@/components/ui/button';

export const Route = createFileRoute('/_public/payment/callback')({
  component: PaymentCallbackPage,
});

function PaymentCallbackPage() {
  const navigate = useNavigate();
  const [status, setStatus] = useState<'loading' | 'success' | 'failed'>('loading');
  const [message, setMessage] = useState('');

  useEffect(() => {
    const handleCallback = async () => {
      try {
        // Get all URL params
        const params = Object.fromEntries(
          new URLSearchParams(window.location.search).entries()
        );

        const result = await paymentApi.handleVNPayCallback(params);
        
        // Check response code from VNPay
        const responseCode = params.vnp_ResponseCode;
        
        if (responseCode === '00') {
          setStatus('success');
          setMessage('Payment successful! Thank you for your payment.');
        } else {
          setStatus('failed');
          setMessage(`Payment failed. Error code: ${responseCode}`);
        }
      } catch (error) {
        setStatus('failed');
        setMessage('An error occurred while processing your payment.');
      }
    };

    handleCallback();
  }, []);

  return (
    <div className="flex min-h-screen items-center justify-center">
      <div className="max-w-md w-full text-center space-y-6 p-8">
        {status === 'loading' && (
          <>
            <Loader2 className="h-16 w-16 animate-spin mx-auto text-primary" />
            <h2 className="text-2xl font-bold">Processing Payment</h2>
            <p className="text-muted-foreground">
              Please wait while we confirm your payment...
            </p>
          </>
        )}

        {status === 'success' && (
          <>
            <CheckCircle className="h-16 w-16 mx-auto text-green-500" />
            <h2 className="text-2xl font-bold text-green-600">Payment Successful!</h2>
            <p className="text-muted-foreground">{message}</p>
            <Button onClick={() => navigate({ to: '/bookings/me' })}>
              View My Bookings
            </Button>
          </>
        )}

        {status === 'failed' && (
          <>
            <XCircle className="h-16 w-16 mx-auto text-red-500" />
            <h2 className="text-2xl font-bold text-red-600">Payment Failed</h2>
            <p className="text-muted-foreground">{message}</p>
            <div className="space-x-4">
              <Button variant="outline" onClick={() => navigate({ to: '/bookings/me' })}>
                View Bookings
              </Button>
              <Button onClick={() => window.history.back()}>
                Try Again
              </Button>
            </div>
          </>
        )}
      </div>
    </div>
  );
}