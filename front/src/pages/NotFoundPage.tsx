import { Link } from 'react-router';
import { Home, AlertTriangle } from 'lucide-react';
import { Button } from '@/components/ui/Button';

export function NotFoundPage() {
  return (
    <div className="min-h-[60vh] flex flex-col items-center justify-center text-center px-4">
      <AlertTriangle size={64} className="text-warning mb-6" />
      <h1 className="text-5xl font-bold text-text-primary mb-4">404</h1>
      <p className="text-xl text-text-secondary mb-8">Page not found</p>
      <p className="text-text-muted mb-8 max-w-md">
        The page you're looking for doesn't exist or has been moved.
      </p>
      <Link to="/">
        <Button className="gap-2">
          <Home size={16} /> Back to Home
        </Button>
      </Link>
    </div>
  );
}
