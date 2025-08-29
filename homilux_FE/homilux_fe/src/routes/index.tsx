// src/routes/index.tsx
import { createFileRoute, Link, useNavigate } from '@tanstack/react-router';
import { useAuthStore } from '@/features/auth/stores/auth.store';
import { usePublicReviews } from '@/features/reviews/hooks/useReviews';
import { useEventTypes } from '@/features/event-types/hooks/useEventTypes';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { useLogout } from '@/features/auth/hooks/useAuth';
import {
  Star,
  Calendar,
  Users,
  MapPin,
  CheckCircle,
  ArrowRight,
  Menu,
  X,
  User,
  LogOut,
  LayoutDashboard,
  Sparkles,
  Trophy,
  Heart,
  Clock,
  Shield,
  Phone,
  Mail,
  ChevronRight,
} from 'lucide-react';
import { useState, useEffect } from 'react';
import { cn } from '@/lib/utils';
import type { components } from '@/types/api';

type ReviewResponse = components['schemas']['ReviewResponse'];

export const Route = createFileRoute('/')({
  component: HomePage,
});

function HomePage() {
  const { user, isAuthenticated, hasRole } = useAuthStore();
  const { mutate: logout } = useLogout();
  const navigate = useNavigate();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [scrolled, setScrolled] = useState(false);
  
  const { data: reviewsData } = usePublicReviews(0, 3);
  const { data: eventTypes } = useEventTypes();
  
  const reviews = (reviewsData?.result as ReviewResponse[]) || [];

  useEffect(() => {
    const handleScroll = () => {
      setScrolled(window.scrollY > 20);
    };
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const features = [
    {
      icon: Calendar,
      title: 'Event Planning',
      description: 'Professional event planning services for all occasions',
    },
    {
      icon: Users,
      title: 'Guest Management',
      description: 'Efficiently manage your guest list and seating arrangements',
    },
    {
      icon: MapPin,
      title: 'Venue Selection',
      description: 'Choose from premium venues or host at your location',
    },
    {
      icon: Sparkles,
      title: 'Custom Packages',
      description: 'Tailored packages to fit your budget and preferences',
    },
  ];

  const stats = [
    { value: '500+', label: 'Events Organized' },
    { value: '10K+', label: 'Happy Guests' },
    { value: '98%', label: 'Satisfaction Rate' },
    { value: '5★', label: 'Average Rating' },
  ];

  const handleDashboardNavigation = () => {
    if (hasRole()) {
      navigate({ to: '/admin/dashboard' });
    } else {
      navigate({ to: '/bookings/me' });
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-b from-white to-gray-50">
      {/* Header */}
      <header
        className={cn(
          'fixed top-0 w-full z-50 transition-all duration-300',
          scrolled ? 'bg-white/95 backdrop-blur-md shadow-sm' : 'bg-transparent'
        )}
      >
        <div className="container mx-auto px-4">
          <div className="flex items-center justify-between h-16">
            {/* Logo */}
            <Link to="/" className="flex items-center space-x-2">
              <div className="w-10 h-10 bg-gradient-to-br from-primary to-primary/80 rounded-lg flex items-center justify-center">
                <Sparkles className="h-6 w-6 text-white" />
              </div>
              <span className="text-2xl font-bold bg-gradient-to-r from-primary to-primary/70 bg-clip-text text-transparent">
                Homilux
              </span>
            </Link>

            {/* Desktop Navigation */}
            <nav className="hidden md:flex items-center space-x-8">
              <a href="#services" className="text-gray-700 hover:text-primary transition-colors">
                Services
              </a>
              <a href="#features" className="text-gray-700 hover:text-primary transition-colors">
                Features
              </a>
              <a href="#reviews" className="text-gray-700 hover:text-primary transition-colors">
                Reviews
              </a>
              <a href="#contact" className="text-gray-700 hover:text-primary transition-colors">
                Contact
              </a>
            </nav>

            {/* Auth Section */}
            <div className="flex items-center space-x-4">
              {isAuthenticated ? (
                <DropdownMenu>
                  <DropdownMenuTrigger asChild>
                    <Button variant="ghost" className="relative h-10 w-10 rounded-full">
                      <Avatar className="h-10 w-10">
                        <AvatarFallback className="bg-gradient-to-br from-primary to-primary/80 text-white">
                          {user?.fullName?.charAt(0)?.toUpperCase() || 'U'}
                        </AvatarFallback>
                      </Avatar>
                    </Button>
                  </DropdownMenuTrigger>
                  <DropdownMenuContent className="w-56" align="end" forceMount>
                    <DropdownMenuLabel className="font-normal">
                      <div className="flex flex-col space-y-1">
                        <p className="text-sm font-medium leading-none">{user?.fullName}</p>
                        <p className="text-xs leading-none text-muted-foreground">
                          {user?.email}
                        </p>
                      </div>
                    </DropdownMenuLabel>
                    <DropdownMenuSeparator />
                    <DropdownMenuItem onClick={() => navigate({ to: '/profile' })}>
                      <User className="mr-2 h-4 w-4" />
                      <span>Profile</span>
                    </DropdownMenuItem>
                    <DropdownMenuItem onClick={handleDashboardNavigation}>
                      <LayoutDashboard className="mr-2 h-4 w-4" />
                      <span>Dashboard</span>
                    </DropdownMenuItem>
                    <DropdownMenuSeparator />
                    <DropdownMenuItem onClick={() => logout()}>
                      <LogOut className="mr-2 h-4 w-4" />
                      <span>Log out</span>
                    </DropdownMenuItem>
                  </DropdownMenuContent>
                </DropdownMenu>
              ) : (
                <div className="flex items-center space-x-3">
                  <Button
                    variant="ghost"
                    onClick={() => navigate({ to: '/login' })}
                    className="hidden md:inline-flex"
                  >
                    Sign In
                  </Button>
                  <Button
                    onClick={() => navigate({ to: '/register' })}
                    className="bg-gradient-to-r from-primary to-primary/80 hover:from-primary/90 hover:to-primary/70"
                  >
                    Get Started
                  </Button>
                </div>
              )}

              {/* Mobile menu button */}
              <button
                className="md:hidden"
                onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              >
                {mobileMenuOpen ? <X className="h-6 w-6" /> : <Menu className="h-6 w-6" />}
              </button>
            </div>
          </div>
        </div>

        {/* Mobile Menu */}
        {mobileMenuOpen && (
          <div className="md:hidden bg-white border-t">
            <div className="container mx-auto px-4 py-4 space-y-3">
              <a
                href="#services"
                className="block px-3 py-2 text-gray-700 hover:text-primary"
                onClick={() => setMobileMenuOpen(false)}
              >
                Services
              </a>
              <a
                href="#features"
                className="block px-3 py-2 text-gray-700 hover:text-primary"
                onClick={() => setMobileMenuOpen(false)}
              >
                Features
              </a>
              <a
                href="#reviews"
                className="block px-3 py-2 text-gray-700 hover:text-primary"
                onClick={() => setMobileMenuOpen(false)}
              >
                Reviews
              </a>
              <a
                href="#contact"
                className="block px-3 py-2 text-gray-700 hover:text-primary"
                onClick={() => setMobileMenuOpen(false)}
              >
                Contact
              </a>
            </div>
          </div>
        )}
      </header>

      {/* Hero Section */}
      <section className="relative pt-32 pb-20 overflow-hidden">
        <div className="absolute inset-0 bg-gradient-to-br from-primary/5 via-transparent to-primary/5" />
        <div className="container mx-auto px-4 relative">
          <div className="max-w-4xl mx-auto text-center">
            <div className="inline-flex items-center px-3 py-1 rounded-full bg-primary/10 text-primary text-sm font-medium mb-6">
              <Sparkles className="h-4 w-4 mr-2" />
              Premium Event Management Service
            </div>
            <h1 className="text-5xl md:text-6xl font-bold text-gray-900 mb-6">
              Create Unforgettable
              <span className="bg-gradient-to-r from-primary to-primary/70 bg-clip-text text-transparent">
                {' '}Moments{' '}
              </span>
              That Last Forever
            </h1>
            <p className="text-xl text-gray-600 mb-8 max-w-2xl mx-auto">
              From intimate gatherings to grand celebrations, we transform your vision into 
              extraordinary experiences with our professional event management services.
            </p>
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              {isAuthenticated ? (
                <Button
                  size="lg"
                  onClick={() => navigate({ to: '/booking/new' })}
                  className="bg-gradient-to-r from-primary to-primary/80 hover:from-primary/90 hover:to-primary/70"
                >
                  Book Your Event
                  <ArrowRight className="ml-2 h-5 w-5" />
                </Button>
              ) : (
                <Button
                  size="lg"
                  onClick={() => navigate({ to: '/register' })}
                  className="bg-gradient-to-r from-primary to-primary/80 hover:from-primary/90 hover:to-primary/70"
                >
                  Start Planning Now
                  <ArrowRight className="ml-2 h-5 w-5" />
                </Button>
              )}
              <Button
                size="lg"
                variant="outline"
                onClick={() => document.getElementById('services')?.scrollIntoView({ behavior: 'smooth' })}
              >
                Explore Services
              </Button>
            </div>
          </div>
        </div>
      </section>

      {/* Stats Section */}
      <section className="py-16 bg-white">
        <div className="container mx-auto px-4">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-8">
            {stats.map((stat, index) => (
              <div key={index} className="text-center">
                <div className="text-3xl md:text-4xl font-bold text-primary mb-2">
                  {stat.value}
                </div>
                <div className="text-gray-600">{stat.label}</div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* Services Section */}
      <section id="services" className="py-20 bg-gray-50">
        <div className="container mx-auto px-4">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-gray-900 mb-4">
              Our Event Services
            </h2>
            <p className="text-xl text-gray-600 max-w-2xl mx-auto">
              We offer comprehensive event management solutions for every occasion
            </p>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {eventTypes?.slice(0, 6).map((type) => (
              <Card
                key={type.id}
                className="group hover:shadow-lg transition-all duration-300 cursor-pointer"
              >
                <CardContent className="p-6">
                  <div className="w-12 h-12 bg-primary/10 rounded-lg flex items-center justify-center mb-4 group-hover:bg-primary/20 transition-colors">
                    <Calendar className="h-6 w-6 text-primary" />
                  </div>
                  <h3 className="text-xl font-semibold mb-2">{type.name}</h3>
                  <p className="text-gray-600 mb-4">
                    Professional planning and execution for your special event
                  </p>
                  <Button variant="link" className="p-0 h-auto font-medium">
                    Learn more
                    <ChevronRight className="ml-1 h-4 w-4" />
                  </Button>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section id="features" className="py-20 bg-white">
        <div className="container mx-auto px-4">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-gray-900 mb-4">
              Why Choose Homilux
            </h2>
            <p className="text-xl text-gray-600 max-w-2xl mx-auto">
              Experience excellence with our comprehensive event management features
            </p>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
            {features.map((feature, index) => {
              const Icon = feature.icon;
              return (
                <div key={index} className="text-center group">
                  <div className="w-16 h-16 bg-gradient-to-br from-primary/10 to-primary/5 rounded-2xl flex items-center justify-center mx-auto mb-4 group-hover:from-primary/20 group-hover:to-primary/10 transition-colors">
                    <Icon className="h-8 w-8 text-primary" />
                  </div>
                  <h3 className="text-lg font-semibold mb-2">{feature.title}</h3>
                  <p className="text-gray-600">{feature.description}</p>
                </div>
              );
            })}
          </div>
        </div>
      </section>

      {/* Reviews Section */}
      <section id="reviews" className="py-20 bg-gray-50">
        <div className="container mx-auto px-4">
          <div className="text-center mb-12">
            <h2 className="text-3xl md:text-4xl font-bold text-gray-900 mb-4">
              What Our Clients Say
            </h2>
            <p className="text-xl text-gray-600 max-w-2xl mx-auto">
              Don't just take our word for it - hear from our satisfied clients
            </p>
          </div>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {reviews.length > 0 ? (
              reviews.map((review) => (
                <Card key={review.id} className="hover:shadow-lg transition-shadow">
                  <CardContent className="p-6">
                    <div className="flex items-center mb-4">
                      {[...Array(5)].map((_, i) => (
                        <Star
                          key={i}
                          className={cn(
                            'h-5 w-5',
                            i < parseInt(review.rating || '0')
                              ? 'fill-yellow-400 text-yellow-400'
                              : 'text-gray-300'
                          )}
                        />
                      ))}
                    </div>
                    <p className="text-gray-600 mb-4 line-clamp-3">{review.comment}</p>
                    <div className="flex items-center">
                      <Avatar className="h-10 w-10 mr-3">
                        <AvatarFallback>
                          {review.user?.fullName?.charAt(0) || 'U'}
                        </AvatarFallback>
                      </Avatar>
                      <div>
                        <p className="font-medium">{review.user?.fullName}</p>
                        <p className="text-sm text-gray-500">Verified Client</p>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              ))
            ) : (
              // Default testimonials if no reviews
              [
                {
                  rating: 5,
                  comment: 'Homilux made our wedding day absolutely perfect. Professional team and attention to detail!',
                  name: 'Sarah Johnson',
                },
                {
                  rating: 5,
                  comment: 'Outstanding service for our corporate event. Everything was handled flawlessly.',
                  name: 'Michael Chen',
                },
                {
                  rating: 5,
                  comment: 'They turned our vision into reality. Highly recommend for any special occasion!',
                  name: 'Emily Davis',
                },
              ].map((testimonial, index) => (
                <Card key={index} className="hover:shadow-lg transition-shadow">
                  <CardContent className="p-6">
                    <div className="flex items-center mb-4">
                      {[...Array(5)].map((_, i) => (
                        <Star
                          key={i}
                          className="h-5 w-5 fill-yellow-400 text-yellow-400"
                        />
                      ))}
                    </div>
                    <p className="text-gray-600 mb-4">{testimonial.comment}</p>
                    <div className="flex items-center">
                      <Avatar className="h-10 w-10 mr-3">
                        <AvatarFallback>{testimonial.name.charAt(0)}</AvatarFallback>
                      </Avatar>
                      <div>
                        <p className="font-medium">{testimonial.name}</p>
                        <p className="text-sm text-gray-500">Verified Client</p>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              ))
            )}
          </div>
        </div>
      </section>

      {/* Trust Section */}
      <section className="py-20 bg-white">
        <div className="container mx-auto px-4">
          <div className="max-w-4xl mx-auto">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
              <div className="text-center">
                <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
                  <Shield className="h-8 w-8 text-green-600" />
                </div>
                <h3 className="text-lg font-semibold mb-2">100% Secure</h3>
                <p className="text-gray-600">Your data and payments are fully protected</p>
              </div>
              <div className="text-center">
                <div className="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
                  <Clock className="h-8 w-8 text-blue-600" />
                </div>
                <h3 className="text-lg font-semibold mb-2">24/7 Support</h3>
                <p className="text-gray-600">We're here to help you anytime</p>
              </div>
              <div className="text-center">
                <div className="w-16 h-16 bg-purple-100 rounded-full flex items-center justify-center mx-auto mb-4">
                  <Trophy className="h-8 w-8 text-purple-600" />
                </div>
                <h3 className="text-lg font-semibold mb-2">Award Winning</h3>
                <p className="text-gray-600">Recognized for excellence in service</p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-20 bg-gradient-to-r from-primary to-primary/80">
        <div className="container mx-auto px-4 text-center">
          <h2 className="text-3xl md:text-4xl font-bold text-white mb-4">
            Ready to Create Your Perfect Event?
          </h2>
          <p className="text-xl text-white/90 mb-8 max-w-2xl mx-auto">
            Join thousands of satisfied clients who have trusted us with their special moments
          </p>
          {isAuthenticated ? (
            <Button
              size="lg"
              variant="secondary"
              onClick={() => navigate({ to: '/booking/new' })}
            >
              Start Planning Now
              <ArrowRight className="ml-2 h-5 w-5" />
            </Button>
          ) : (
            <div className="flex flex-col sm:flex-row gap-4 justify-center">
              <Button
                size="lg"
                variant="secondary"
                onClick={() => navigate({ to: '/register' })}
              >
                Get Started Free
                <ArrowRight className="ml-2 h-5 w-5" />
              </Button>
              <Button
                size="lg"
                variant="outline"
                className="bg-white/10 text-white border-white/30 hover:bg-white/20"
                onClick={() => navigate({ to: '/login' })}
              >
                Sign In
              </Button>
            </div>
          )}
        </div>
      </section>

      {/* Footer */}
      <footer id="contact" className="bg-gray-900 text-white py-12">
        <div className="container mx-auto px-4">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
            <div>
              <div className="flex items-center space-x-2 mb-4">
                <div className="w-10 h-10 bg-white/10 rounded-lg flex items-center justify-center">
                  <Sparkles className="h-6 w-6 text-white" />
                </div>
                <span className="text-2xl font-bold">Homilux</span>
              </div>
              <p className="text-gray-400">
                Creating extraordinary experiences for life's special moments.
              </p>
            </div>
            
            <div>
              <h3 className="font-semibold mb-4">Quick Links</h3>
              <ul className="space-y-2 text-gray-400">
                <li>
                  <a href="#services" className="hover:text-white transition-colors">
                    Services
                  </a>
                </li>
                <li>
                  <a href="#features" className="hover:text-white transition-colors">
                    Features
                  </a>
                </li>
                <li>
                  <a href="#reviews" className="hover:text-white transition-colors">
                    Reviews
                  </a>
                </li>
              </ul>
            </div>

            <div>
              <h3 className="font-semibold mb-4">Legal</h3>
              <ul className="space-y-2 text-gray-400">
                <li>
                  <a href="#" className="hover:text-white transition-colors">
                    Privacy Policy
                  </a>
                </li>
                <li>
                  <a href="#" className="hover:text-white transition-colors">
                    Terms of Service
                  </a>
                </li>
                <li>
                  <a href="#" className="hover:text-white transition-colors">
                    Cookie Policy
                  </a>
                </li>
              </ul>
            </div>

            <div>
              <h3 className="font-semibold mb-4">Contact Us</h3>
              <div className="space-y-3 text-gray-400">
                <div className="flex items-center space-x-2">
                  <Mail className="h-5 w-5" />
                  <span>support@homilux.com</span>
                </div>
                <div className="flex items-center space-x-2">
                  <Phone className="h-5 w-5" />
                  <span>1900 xxxx</span>
                </div>
                <div className="flex items-center space-x-2">
                  <MapPin className="h-5 w-5" />
                  <span>Ho Chi Minh City, Vietnam</span>
                </div>
              </div>
            </div>
          </div>

          <div className="border-t border-gray-800 mt-8 pt-8 text-center text-gray-400">
            <p>&copy; 2025 Homilux. All rights reserved. Made with <Heart className="inline h-4 w-4 text-red-500" /> in Vietnam</p>
          </div>
        </div>
      </footer>
    </div>
  );
}