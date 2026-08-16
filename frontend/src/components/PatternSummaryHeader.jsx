import React from 'react';
import { Layers, ArrowRight, Zap, Target, GitBranch, Database } from 'lucide-react';

const PATTERN_GUIDES = [
  {
    name: 'Two Pointers',
    icon: ArrowRight,
    color: '#6366f1',
    bg: 'rgba(99, 102, 241, 0.12)',
    border: 'rgba(99, 102, 241, 0.3)',
    desc: 'Use 2 indices moving towards each other or at different speeds to solve array/string search in O(N).'
  },
  {
    name: 'Sliding Window',
    icon: Zap,
    color: '#38bdf8',
    bg: 'rgba(56, 189, 248, 0.12)',
    border: 'rgba(56, 189, 248, 0.3)',
    desc: 'Maintain a dynamic window of elements over contiguous subarrays/substrings to optimize brute-force O(N²).'
  },
  {
    name: 'Monotonic Stack',
    icon: Layers,
    color: '#f43f5e',
    bg: 'rgba(244, 63, 94, 0.12)',
    border: 'rgba(244, 63, 94, 0.3)',
    desc: 'Use a stack maintaining increasing/decreasing order to find next greater or smaller elements in O(N).'
  },
  {
    name: 'Fast & Slow Pointers',
    icon: Target,
    color: '#a855f7',
    bg: 'rgba(168, 85, 247, 0.12)',
    border: 'rgba(168, 85, 247, 0.3)',
    desc: 'Floyds Tortoise & Hare algorithm using two pointers moving at different speeds for cycle detection.'
  },
  {
    name: 'Top-K Heap',
    icon: Database,
    color: '#eab308',
    bg: 'rgba(234, 179, 8, 0.12)',
    border: 'rgba(234, 179, 8, 0.3)',
    desc: 'Use a Min-Heap or Max-Heap to track the K largest/smallest elements efficiently in O(N log K).'
  },
  {
    name: 'BFS / DFS',
    icon: GitBranch,
    color: '#10b981',
    bg: 'rgba(16, 185, 129, 0.12)',
    border: 'rgba(16, 185, 129, 0.3)',
    desc: 'Breadth-First Search (Queue) and Depth-First Search (Recursion/Stack) for tree & graph traversals.'
  }
];

const PatternSummaryHeader = ({ selectedPattern, onSelectPattern }) => {
  return (
    <div className="pattern-summary-section">
      <div className="pattern-summary-title">
        <Layers size={18} style={{ color: 'var(--primary-accent)' }} />
        <span>Explore Core DSA Solving Patterns</span>
      </div>
      <div className="pattern-cards-grid">
        {PATTERN_GUIDES.map((pat) => {
          const Icon = pat.icon;
          const isSelected = selectedPattern === pat.name;
          return (
            <div
              key={pat.name}
              className={`pattern-card ${isSelected ? 'selected' : ''}`}
              style={{
                background: isSelected ? pat.bg : 'var(--bg-card)',
                borderColor: isSelected ? pat.color : 'var(--border-color)'
              }}
              onClick={() => onSelectPattern(isSelected ? '' : pat.name)}
            >
              <div className="pattern-card-top">
                <div
                  className="pattern-icon-box"
                  style={{ background: pat.bg, color: pat.color, borderColor: pat.border }}
                >
                  <Icon size={16} />
                </div>
                <h4 style={{ color: isSelected ? pat.color : 'var(--text-main)' }}>{pat.name}</h4>
              </div>
              <p className="pattern-desc">{pat.desc}</p>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default PatternSummaryHeader;
