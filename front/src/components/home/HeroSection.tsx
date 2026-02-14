import { Link } from 'react-router';
import { Sparkles, Search } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { useAuth } from '@/hooks/useAuth';

export function HeroSection() {
  const { isAuthenticated } = useAuth();

  return (
    <section className="relative overflow-hidden py-20 sm:py-28">
      <div className="absolute inset-0 bg-gradient-to-br from-primary/10 via-background to-accent/5" />
      <div className="absolute inset-0 bg-[radial-gradient(ellipse_at_top_right,_var(--color-primary)_0%,transparent_50%)] opacity-10" />
      <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <h1 className="text-4xl sm:text-5xl lg:text-6xl font-bold mb-6">
          <span className="bg-gradient-to-r from-primary via-accent to-primary-light bg-clip-text text-transparent">
            Discover Your Next
          </span>
          <br />
          <span className="text-text-primary">Favorite Anime</span>
        </h1>
        <p className="text-lg sm:text-xl text-text-secondary max-w-2xl mx-auto mb-10">
          Get personalized anime recommendations based on your taste.
          Rate, track, and explore thousands of titles.
        </p>
        <div className="flex flex-col sm:flex-row gap-4 justify-center">
          {isAuthenticated ? (
            <Link to="/recommend">
              <Button size="lg" className="gap-2">
                <Sparkles size={20} /> Get Recommendations
              </Button>
            </Link>
          ) : (
            <Link to="/signup">
              <Button size="lg" className="gap-2">
                <Sparkles size={20} /> Get Started Free
              </Button>
            </Link>
          )}
          <Link to="/search/anime">
            <Button variant="secondary" size="lg" className="gap-2">
              <Search size={20} /> Browse Anime
            </Button>
          </Link>
        </div>
      </div>
    </section>
  );
}
