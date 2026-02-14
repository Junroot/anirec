import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, Cell } from 'recharts';
import type { GenreStat } from '@/types/stats';

interface GenreBarChartProps {
  data: GenreStat[];
}

const COLORS = ['#6366f1', '#818cf8', '#a78bfa', '#c4b5fd', '#6366f1', '#818cf8', '#a78bfa', '#c4b5fd', '#6366f1', '#818cf8'];

export function GenreBarChart({ data }: GenreBarChartProps) {
  return (
    <div className="bg-surface rounded-xl border border-surface-lighter p-5">
      <h3 className="text-lg font-semibold text-text-primary mb-4">Genres Watched</h3>
      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={data} layout="vertical" margin={{ left: 60 }}>
          <XAxis type="number" stroke="#6b6985" fontSize={12} />
          <YAxis type="category" dataKey="genre" stroke="#6b6985" fontSize={12} width={80} />
          <Tooltip
            contentStyle={{ backgroundColor: '#1e1b2e', border: '1px solid #363352', borderRadius: '8px', color: '#f1f0f7' }}
            labelStyle={{ color: '#a5a3b7' }}
          />
          <Bar dataKey="count" radius={[0, 4, 4, 0]}>
            {data.map((_, index) => (
              <Cell key={index} fill={COLORS[index % COLORS.length]} />
            ))}
          </Bar>
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}
