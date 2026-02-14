import { useState } from 'react';
import { Modal } from '@/components/ui/Modal';
import { Button } from '@/components/ui/Button';
import { RatingInput } from './RatingInput';
import { RatingSlider } from './RatingSlider';
import { WatchStatusSelector } from './WatchStatusSelector';
import type { Anime } from '@/types/anime';
import type { WatchStatus } from '@/types/rating';
import { PLACEHOLDER_IMAGE } from '@/data/constants';

interface RatingModalProps {
  anime: Anime | null;
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (animeId: number, score: number, status: WatchStatus) => void;
}

export function RatingModal({ anime, isOpen, onClose, onSubmit }: RatingModalProps) {
  const [score, setScore] = useState(7);
  const [watchStatus, setWatchStatus] = useState<WatchStatus>('Completed');
  const [useSlider, setUseSlider] = useState(false);

  if (!anime) return null;

  const handleSubmit = () => {
    onSubmit(anime.mal_id, score, watchStatus);
    onClose();
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Rate Anime" className="max-w-lg">
      <div className="space-y-6">
        <div className="flex gap-4">
          <img
            src={anime.images.jpg.image_url || PLACEHOLDER_IMAGE}
            alt={anime.title}
            className="w-16 h-22 rounded-lg object-cover shrink-0"
          />
          <div>
            <h4 className="font-medium text-text-primary">{anime.title}</h4>
            <p className="text-sm text-text-muted mt-1">{anime.type} · {anime.episodes ?? '?'} episodes</p>
          </div>
        </div>

        <div className="space-y-4">
          <div className="flex items-center justify-between">
            <span className="text-sm text-text-secondary">Your Rating</span>
            <button
              type="button"
              onClick={() => setUseSlider(!useSlider)}
              className="text-xs text-primary-light hover:text-primary transition-colors"
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
          <Button variant="secondary" onClick={onClose} className="flex-1">Cancel</Button>
          <Button onClick={handleSubmit} className="flex-1">Save Rating</Button>
        </div>
      </div>
    </Modal>
  );
}
