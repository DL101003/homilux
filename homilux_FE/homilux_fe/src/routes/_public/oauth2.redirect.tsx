// src/routes/_public/oauth2.redirect.tsx
import { createFileRoute, useNavigate } from '@tanstack/react-router';
import { useEffect } from 'react';
import { useAuthStore } from '@/features/auth/stores/auth.store';
import { authApi } from '@/features/auth/api/auth.api';
import { toast } from 'sonner';
import { Loader2 } from 'lucide-react';

export const Route = createFileRoute('/_public/oauth2/redirect')({
  component: OAuth2RedirectHandler,
});

function OAuth2RedirectHandler() {
  const navigate = useNavigate();
  const { setAccessToken, setUser, hasRole } = useAuthStore();

  useEffect(() => {
    const handleOAuth2Redirect = async () => {
      try {
        // Extract access_token from URL fragment
        const hash = window.location.hash;
        const params = new URLSearchParams(hash.substring(1));
        const accessToken = params.get('access_token');

        if (!accessToken) {
          throw new Error('No access token found');
        }

        // Store access token
        setAccessToken(accessToken);

        // Fetch user account details
        const userData = await authApi.fetchAccount();
        setUser(userData);

        toast.success('Login with Google successful!');

        // Redirect based on role
        if (userData.role) {
          navigate({ to: '/admin/dashboard' });
        } else {
          navigate({ to: '/' });
        }
      } catch (error) {
        console.error('OAuth2 redirect error:', error);
        toast.error('Failed to complete Google login');
        navigate({ to: '/login' });
      }
    };

    handleOAuth2Redirect();
  }, [navigate, setAccessToken, setUser, hasRole]);

  return (
    <div className="flex min-h-screen items-center justify-center">
      <div className="text-center space-y-4">
        <Loader2 className="h-8 w-8 animate-spin mx-auto" />
        <p className="text-muted-foreground">Completing sign in with Google...</p>
      </div>
    </div>
  );
}