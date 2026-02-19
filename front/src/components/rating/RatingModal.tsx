import { useState, useEffect } from 'react';
import { Modal } from '@/components/ui/Modal';
import { Button } from '@/components/ui/Button';
import { Spinner } from '@/components/ui/Spinner';
import { RatingInput } from './RatingInput';
import { RatingSlider } from './RatingSlider';
import { WatchStatusSelector } from './WatchStatusSelector';
import { getRating } from '@/api/ratingApi';
import type { Anime } from '@/types/anime';
import type { WatchStatus } from '@/types/rating';
import { PLACEHOLDER_IMAGE } from '@/data/constants';

interface RatingModalProps {
  anime: Anime | null;
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (animeId: number, score: number, status: WatchStatus) => void;
  submitting?: boolean;
}

export function RatingModal({ anime, isOpen, onClose, onSubmit, submitting }: RatingModalProps) {
  const [score, setScore] = useState(0);
  const [watchStatus, setWatchStatus] = useState<WatchStatus | null>(null);
  const [useSlider, setUseSlider] = useState(false);
  const [loading, setLoading] = useState(false);
  const [isEdit, setIsEdit] = useState(false);

  useEffect(() => {
    if (!anime || !isOpen) return;

    let cancelled = false;
    setLoading(true);
    setScore(0);
    setWatchStatus(null);
    setIsEdit(false);

    getRating(anime.mal_id)
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
  }, [anime, isOpen]);

  if (!anime) return null;

  const canSubmit = score > 0 && watchStatus !== null && !submitting;

  const handleSubmit = () => {
    if (!canSubmit) return;
    onSubmit(anime.mal_id, score, watchStatus);
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title={isEdit ? 'Edit Rating' : 'Rate Anime'} className="max-w-lg">
      <div className="space-y-6">
        <div className="flex gap-4">
          <img
            src={anime.images.jpg.image_url || PLACEHOLDER_IMAGE}
            alt={anime.title}
            className="w-16 h-22 rounded-lg object-cover shrink-0"
          />
          <div>
            <h4 className="font-medium text-on-surface">{anime.title}</h4>
            <p className="text-sm text-outline mt-1">{anime.type} · {anime.episodes ?? '?'} episodes</p>
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
