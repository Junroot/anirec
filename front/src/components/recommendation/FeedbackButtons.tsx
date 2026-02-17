import { ThumbsUp, ThumbsDown } from 'lucide-react';
import clsx from 'clsx';

interface FeedbackButtonsProps {
  feedback: 'like' | 'dislike' | null | undefined;
  onFeedback: (type: 'like' | 'dislike') => void;
}

export function FeedbackButtons({ feedback, onFeedback }: FeedbackButtonsProps) {
  return (
    <div className="flex items-center gap-2">
      <button
        onClick={() => onFeedback('like')}
        className={clsx(
          'p-2 rounded-lg transition-all',
          feedback === 'like'
            ? 'bg-success/20 text-success'
            : 'text-outline hover:bg-surface-container-highest hover:text-success'
        )}
        title="Good recommendation"
      >
        <ThumbsUp size={16} />
      </button>
      <button
        onClick={() => onFeedback('dislike')}
        className={clsx(
          'p-2 rounded-lg transition-all',
          feedback === 'dislike'
            ? 'bg-error/20 text-error'
            : 'text-outline hover:bg-surface-container-highest hover:text-error'
        )}
        title="Not for me"
      >
        <ThumbsDown size={16} />
      </button>
    </div>
  );
}
