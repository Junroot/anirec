import { ChevronLeft, ChevronRight } from 'lucide-react';
import clsx from 'clsx';

interface PaginationProps {
  currentPage: number;
  lastPage: number;
  hasNextPage: boolean;
  onPageChange: (page: number) => void;
}

function getPageNumbers(current: number, last: number): (number | 'ellipsis')[] {
  if (last <= 7) {
    return Array.from({ length: last }, (_, i) => i + 1);
  }

  const pages: (number | 'ellipsis')[] = [1];

  if (current <= 4) {
    pages.push(2, 3, 4, 5, 'ellipsis', last);
  } else if (current >= last - 3) {
    pages.push('ellipsis', last - 4, last - 3, last - 2, last - 1, last);
  } else {
    pages.push('ellipsis', current - 1, current, current + 1, 'ellipsis', last);
  }

  return pages;
}

export function Pagination({ currentPage, lastPage, hasNextPage, onPageChange }: PaginationProps) {
  if (lastPage <= 1) return null;

  const pages = getPageNumbers(currentPage, lastPage);

  const handlePageChange = (page: number) => {
    onPageChange(page);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  return (
    <nav className="flex items-center justify-center gap-1 pt-8" aria-label="Pagination">
      <button
        onClick={() => handlePageChange(currentPage - 1)}
        disabled={currentPage <= 1}
        className="inline-flex items-center justify-center w-9 h-9 rounded-lg text-on-surface-variant hover:bg-surface-container-high disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
        aria-label="Previous page"
      >
        <ChevronLeft size={18} />
      </button>

      {pages.map((page, i) =>
        page === 'ellipsis' ? (
          <span key={`ellipsis-${i}`} className="w-9 h-9 inline-flex items-center justify-center text-on-surface-variant text-sm">
            ...
          </span>
        ) : (
          <button
            key={page}
            onClick={() => handlePageChange(page)}
            className={clsx(
              'inline-flex items-center justify-center w-9 h-9 rounded-lg text-sm font-medium transition-colors',
              page === currentPage
                ? 'bg-primary text-white'
                : 'text-on-surface-variant hover:bg-surface-container-high'
            )}
            aria-label={`Page ${page}`}
            aria-current={page === currentPage ? 'page' : undefined}
          >
            {page}
          </button>
        )
      )}

      <button
        onClick={() => handlePageChange(currentPage + 1)}
        disabled={!hasNextPage}
        className="inline-flex items-center justify-center w-9 h-9 rounded-lg text-on-surface-variant hover:bg-surface-container-high disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
        aria-label="Next page"
      >
        <ChevronRight size={18} />
      </button>
    </nav>
  );
}
