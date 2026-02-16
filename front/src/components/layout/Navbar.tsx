import { Link, useLocation } from 'react-router';
import { Menu, X, LogOut, User as UserIcon } from 'lucide-react';
import { useState } from 'react';
import { useAuth } from '@/hooks/useAuth';
import { Avatar } from '@/components/ui/Avatar';
import { Button } from '@/components/ui/Button';

const navLinks = [
  { to: '/', label: 'Home' },
  { to: '/search/anime', label: 'Search' },
  { to: '/recommend', label: 'Recommendations', auth: true },
  { to: '/my-ratings', label: 'My Ratings', auth: true },
  { to: '/stats', label: 'Stats', auth: true },
];

export function Navbar() {
  const { user, isAuthenticated, loading, logout } = useAuth();
  const location = useLocation();
  const [mobileOpen, setMobileOpen] = useState(false);

  const visibleLinks = navLinks.filter(link => !link.auth || isAuthenticated);

  return (
    <nav className="sticky top-0 z-40 bg-surface/80 backdrop-blur-xl border-b border-surface-lighter">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          <Link to="/" className="flex items-center gap-2">
            <span className="text-2xl font-bold bg-gradient-to-r from-primary to-accent bg-clip-text text-transparent">
              AniRec
            </span>
          </Link>

          <div className="hidden md:flex items-center gap-1">
            {visibleLinks.map(link => (
              <Link
                key={link.to}
                to={link.to}
                className={`px-3 py-2 rounded-lg text-sm font-medium transition-colors ${
                  location.pathname === link.to
                    ? 'bg-primary/10 text-primary-light'
                    : 'text-text-secondary hover:text-text-primary hover:bg-surface-light'
                }`}
              >
                {link.label}
              </Link>
            ))}
          </div>

          <div className="hidden md:flex items-center gap-3">
            {loading ? (
              <div className="w-24 h-8" />
            ) : isAuthenticated ? (
              <div className="flex items-center gap-3">
                <div className="flex items-center gap-2">
                  <Avatar name={user?.username ?? ''} size="sm" />
                  <span className="text-sm text-text-secondary">{user?.username}</span>
                </div>
                <button
                  onClick={logout}
                  className="p-2 rounded-lg hover:bg-surface-light text-text-muted hover:text-text-primary transition-colors"
                  title="Logout"
                >
                  <LogOut size={18} />
                </button>
              </div>
            ) : (
              <div className="flex items-center gap-2">
                <Link to="/login">
                  <Button variant="ghost" size="sm">Login</Button>
                </Link>
                <Link to="/signup">
                  <Button size="sm">Sign Up</Button>
                </Link>
              </div>
            )}
          </div>

          <button
            className="md:hidden p-2 rounded-lg hover:bg-surface-light text-text-secondary"
            onClick={() => setMobileOpen(!mobileOpen)}
          >
            {mobileOpen ? <X size={24} /> : <Menu size={24} />}
          </button>
        </div>

        {mobileOpen && (
          <div className="md:hidden pb-4 border-t border-surface-lighter mt-2 pt-4">
            <div className="flex flex-col gap-1">
              {visibleLinks.map(link => (
                <Link
                  key={link.to}
                  to={link.to}
                  onClick={() => setMobileOpen(false)}
                  className={`px-3 py-2 rounded-lg text-sm font-medium ${
                    location.pathname === link.to
                      ? 'bg-primary/10 text-primary-light'
                      : 'text-text-secondary hover:bg-surface-light'
                  }`}
                >
                  {link.label}
                </Link>
              ))}
            </div>
            <div className="mt-4 px-3">
              {loading ? (
                <div className="h-8" />
              ) : isAuthenticated ? (
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <UserIcon size={16} className="text-text-muted" />
                    <span className="text-sm text-text-secondary">{user?.username}</span>
                  </div>
                  <button onClick={() => { logout(); setMobileOpen(false); }} className="text-sm text-error">
                    Logout
                  </button>
                </div>
              ) : (
                <div className="flex gap-2">
                  <Link to="/login" className="flex-1" onClick={() => setMobileOpen(false)}>
                    <Button variant="secondary" size="sm" className="w-full">Login</Button>
                  </Link>
                  <Link to="/signup" className="flex-1" onClick={() => setMobileOpen(false)}>
                    <Button size="sm" className="w-full">Sign Up</Button>
                  </Link>
                </div>
              )}
            </div>
          </div>
        )}
      </div>
    </nav>
  );
}
