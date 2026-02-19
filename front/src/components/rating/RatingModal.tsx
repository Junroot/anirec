import { useState, useEffect } from 'react';
import { Modal } from '@/components/ui/Modal';
import { Button } from '@/components/ui/Button';
import { Spinner } from '@/components/ui/Spinner';
import { RatingInput } from './RatingInput';
import { RatingSlider } from './RatingSlider';
import { WatchStatusSelector } from './WatchStatusSelector';
import { getRating } from '@/api/ratingApi';
import type { WatchStatus } from '@/types/rating';
import { PLACEHOLDER_IMAGE } from '@/data/constants';

export interface RatingTarget {
  malId: number;
  title: string;
  imageUrl: string | null;
  type: string | null;
  episodes: number | null;
}

export function animeToRatingTarget(anime: {
  mal_id: number;
  title: string;
  images: { jpg: { image_url: string } };
  type: string | null;
  episodes: number | null;
}): RatingTarget {
  return {
    malId: anime.mal_id,
    title: anime.title,
    imageUrl: anime.images.jpg.image_url,
    type: anime.type,
    episodes: anime.episodes,
  };
}

interface RatingModalProps {
  target: RatingTarget | null;
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (animeId: number, score: number, status: WatchStatus) => void;
  submitting?: boolean;
}

export function RatingModal({ target, isOpen, onClose, onSubmit, submitting }: RatingModalProps) {
  const [score, setScore] = useState(0);
  const [watchStatus, setWatchStatus] = useState<WatchStatus | null>(null);
  const [useSlider, setUseSlider] = useState(false);
  const [loading, setLoading] = useState(false);
  const [isEdit, setIsEdit] = useState(false);

  useEffect(() => {
    if (!target || !isOpen) return;

    let cancelled = false;
    setLoading(true);
    setScore(0);
    setWatchStatus(null);
    setIsEdit(false);

    getRating(target.malId)
      .then(existing => {
        if (cancelled) return;
        if (existing) {
          setScore(existing.score);
          setWatchStatus(existing.watch_status as WatchStatus);
          setIsEdit(true);
        }
      })
      .catch(() => {
        // ignore — keep empty state
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });

    return () => { cancelled = true; };
  }, [target, isOpen]);

  if (!target) return null;

  const canSubmit = score > 0 && watchStatus !== null && !submitting;

  const handleSubmit = () => {
    if (!canSubmit) return;
    onSubmit(target.malId, score, watchStatus);
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={isEdit ? 'Edit Rating' : 'Rate Anime'} className="max-w-lg">
      <div className="space-y-6">
        <div className="flex gap-4">
          <img
            src={target.imageUrl || PLACEHOLDER_IMAGE}
            alt={target.title}
            className="w-16 h-22 rounded-lg object-cover shrink-0"
          />
          <div>
            <h4 className="font-medium text-on-surface">{target.title}</h4>
            <p className="text-sm text-outline mt-1">{target.type ?? '?'} · {target.episodes ?? '?'} episodes</p>
          </div>
        </div>

        {loading ? (
          <Spinner size="md" className="py-8" />
        ) : (
          <>
            <div className="space-y-4">
              <div className="flex items-center justify-between">
                <span className="text-sm text-on-surface-variant">Your Rating</span>
                <button
                  type="button"
                  onClick={() => setUseSlider(!useSlider)}
                  className="text-xs text-primary-container hover:text-primary transition-colors"
                >
                  {useSlider ? 'Use stars' : 'Use slider'}
                </button>
              </div>
              {useSlider ? (
                <RatingSlider value={score} onChange={setScore} />
              ) : (
                <RatingInput value={score} onChange={setScore} />
              )}
            </div>

            <WatchStatusSelector value={watchStatus} onChange={setWatchStatus} />

            <div className="flex gap-3 pt-2">
              <Button variant="secondary" onClick={onClose} className="flex-1" disabled={submitting}>Cancel</Button>
              <Button onClick={handleSubmit} className="flex-1" disabled={!canSubmit}>
                {submitting ? 'Saving...' : isEdit ? 'Update Rating' : 'Save Rating'}
              </Button>
            </div>
          </>
        )}
      </div>
    </Modal>
  );
}
