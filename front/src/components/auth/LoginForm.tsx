import { useState } from 'react';
import { useNavigate, useLocation, Link } from 'react-router';
import { Mail, Lock, LogIn } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { useAuth } from '@/hooks/useAuth';

export function LoginForm() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [formError, setFormError] = useState<string | null>(null);
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const from = (location.state as { from?: { pathname: string } })?.from?.pathname || '/';

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setFormError(null);
    setIsSubmitting(true);
    try {
      await login(email, password);
      navigate(from, { replace: true });
    } catch (err) {
      setFormError(err instanceof Error ? err.message : 'Login failed');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="bg-surface-container rounded-xl p-8 border border-outline-variant shadow-2xl">
      <h2 className="text-2xl font-bold text-on-surface mb-2">Welcome back</h2>
      <p className="text-on-surface-variant text-sm mb-6">Sign in to your AniRec account</p>

      {formError && (
        <div className="mb-4 p-3 rounded-lg bg-error-container border border-error-container text-on-error-container text-sm">
          {formError}
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label className="block text-sm text-on-surface-variant mb-1.5">Email</label>
          <div className="relative">
            <Mail size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-outline" />
            <input
              type="email"
              value={email}
              onChange={e => setEmail(e.target.value)}
              placeholder="you@example.com"
              required
              className="w-full pl-10 pr-4 py-2.5 bg-surface-container-high border border-outline-variant rounded-lg text-on-surface placeholder-outline text-sm focus:outline-none focus:ring-2 focus:ring-primary/50"
            />
          </div>
        </div>
        <div>
          <label className="block text-sm text-on-surface-variant mb-1.5">Password</label>
          <div className="relative">
            <Lock size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-outline" />
            <input
              type="password"
              value={password}
              onChange={e => setPassword(e.target.value)}
              placeholder="••••••••"
              required
              className="w-full pl-10 pr-4 py-2.5 bg-surface-container-high border border-outline-variant rounded-lg text-on-surface placeholder-outline text-sm focus:outline-none focus:ring-2 focus:ring-primary/50"
            />
          </div>
        </div>
        <Button type="submit" className="w-full gap-2" isLoading={isSubmitting}>
          <LogIn size={16} /> Sign In
        </Button>
      </form>

      <p className="text-center text-sm text-on-surface-variant mt-6">
        Don't have an account?{' '}
        <Link to="/signup" className="text-primary hover:text-primary transition-colors">Sign up</Link>
      </p>
    </div>
  );
}
