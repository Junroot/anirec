import { Heart } from 'lucide-react';

export function Footer() {
  return (
    <footer className="border-t border-outline-variant bg-surface-container/50 mt-auto">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="flex flex-col md:flex-row items-center justify-between gap-4">
          <div className="text-sm text-outline">
            &copy; {new Date().getFullYear()} AniRec. Built for anime fans.
          </div>
          <div className="flex items-center gap-1 text-sm text-outline">
            Made with <Heart size={14} className="text-error fill-error" /> using MAL data
          </div>
        </div>
      </div>
    </footer>
  );
}
