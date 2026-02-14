import { Heart } from 'lucide-react';

export function Footer() {
  return (
    <footer className="border-t border-surface-lighter bg-surface/50 mt-auto">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="flex flex-col md:flex-row items-center justify-between gap-4">
          <div className="text-sm text-text-muted">
            &copy; {new Date().getFullYear()} AniRec. Built for anime fans.
          </div>
          <div className="flex items-center gap-1 text-sm text-text-muted">
            Made with <Heart size={14} className="text-error fill-error" /> using MAL data
          </div>
        </div>
      </div>
    </footer>
  );
}
