import React, { useState, useEffect } from 'react';
import { X, Play, Pause, SkipForward, SkipBack, RotateCcw, Code, HelpCircle } from 'lucide-react';

const SAMPLE_ALGORITHMS = {
  'Two Sum': {
    title: 'Two Sum / Two Pointers (Target = 9)',
    array: [2, 7, 11, 15],
    target: 9,
    steps: [
      { left: 0, right: 3, sum: 17, msg: 'Initial state: left=0 (val 2), right=3 (val 15). Sum = 17 > 9. Move right leftward.', codeLine: 2 },
      { left: 0, right: 2, sum: 13, msg: 'Step 1: left=0 (val 2), right=2 (val 11). Sum = 13 > 9. Move right leftward.', codeLine: 3 },
      { left: 0, right: 1, sum: 9, msg: 'Step 2: left=0 (val 2), right=1 (val 7). Sum = 9 == Target! Match Found at [0, 1].', codeLine: 5 }
    ],
    code: [
      'int left = 0, right = nums.length - 1;',
      'while (left < right) {',
      '    int sum = nums[left] + nums[right];',
      '    if (sum == target) return new int[]{left, right};',
      '    else if (sum > target) right--;',
      '    else left++;',
      '}'
    ]
  },
  'Sliding Window': {
    title: 'Sliding Window Subarray Sum (K = 3)',
    array: [2, 1, 5, 1, 3, 2],
    target: 3,
    steps: [
      { left: 0, right: 2, sum: 8, msg: 'Window [0..2]: elements [2, 1, 5]. Current Window Sum = 8. Max Sum = 8.', codeLine: 1 },
      { left: 1, right: 3, sum: 7, msg: 'Slide window to [1..3]: add 1, remove 2. Current Window Sum = 7. Max Sum = 8.', codeLine: 3 },
      { left: 2, right: 4, sum: 9, msg: 'Slide window to [2..4]: add 3, remove 1. Current Window Sum = 9. Max Sum updated to 9!', codeLine: 4 },
      { left: 3, right: 5, sum: 6, msg: 'Slide window to [3..5]: add 2, remove 5. Current Window Sum = 6. Max Sum remains 9.', codeLine: 5 }
    ],
    code: [
      'int windowSum = 0, maxSum = 0;',
      'for (int i = 0; i < k; i++) windowSum += nums[i];',
      'maxSum = windowSum;',
      'for (int r = k; r < nums.length; r++) {',
      '    windowSum += nums[r] - nums[r - k];',
      '    maxSum = Math.max(maxSum, windowSum);',
      '}'
    ]
  }
};

const ArrayVisualizerModal = ({ problem, onClose }) => {
  const algoKey = problem && problem.pattern === 'Sliding Window' ? 'Sliding Window' : 'Two Sum';
  const config = SAMPLE_ALGORITHMS[algoKey] || SAMPLE_ALGORITHMS['Two Sum'];

  const [currentStepIndex, setCurrentStepIndex] = useState(0);
  const [isPlaying, setIsPlaying] = useState(false);
  const [speed, setSpeed] = useState(1000);

  const step = config.steps[currentStepIndex];

  useEffect(() => {
    let timer;
    if (isPlaying) {
      timer = setInterval(() => {
        setCurrentStepIndex((prev) => {
          if (prev < config.steps.length - 1) return prev + 1;
          setIsPlaying(false);
          return prev;
        });
      }, speed);
    }
    return () => clearInterval(timer);
  }, [isPlaying, speed, config.steps.length]);

  return (
    <div className="modal-overlay">
      <div className="modal-card visualizer-modal">
        <div className="modal-header">
          <div>
            <h3 style={{ fontSize: '1.25rem', color: '#fff', margin: 0 }}>{problem ? problem.title : 'Algorithm Stepper'}</h3>
            <span className="pattern-pill" style={{ marginTop: '0.4rem' }}>{config.title}</span>
          </div>
          <button className="close-btn" onClick={onClose}><X size={20} /></button>
        </div>

        {/* Array Visualization Display */}
        <div className="array-visualizer-container">
          <div className="array-bars-row">
            {config.array.map((val, idx) => {
              const isLeft = idx === step.left;
              const isRight = idx === step.right;
              const inWindow = idx >= step.left && idx <= step.right;

              let barBg = '#334155';
              let borderColor = 'transparent';

              if (isLeft && isRight) {
                barBg = '#a855f7';
                borderColor = '#c084fc';
              } else if (isLeft) {
                barBg = '#6366f1';
                borderColor = '#818cf8';
              } else if (isRight) {
                barBg = '#38bdf8';
                borderColor = '#7dd3fc';
              } else if (inWindow) {
                barBg = 'rgba(99, 102, 241, 0.25)';
                borderColor = 'rgba(99, 102, 241, 0.4)';
              }

              return (
                <div key={idx} className="array-bar-item">
                  <div className="bar-box" style={{ background: barBg, borderColor: borderColor }}>
                    {val}
                  </div>
                  <div className="bar-index">i = {idx}</div>
                  <div className="bar-pointers">
                    {isLeft && <span className="pointer-label left">L</span>}
                    {isRight && <span className="pointer-label right">R</span>}
                  </div>
                </div>
              );
            })}
          </div>

          {/* Execution Commentary & Line Highlight */}
          <div className="step-commentary-box">
            <p className="commentary-text">💡 <strong>Step {currentStepIndex + 1} of {config.steps.length}:</strong> {step.msg}</p>
          </div>

          {/* Code Highlight Side-panel */}
          <div className="code-stepper-panel">
            <div className="code-header">
              <Code size={16} /> <span>Code Execution Trace</span>
            </div>
            <pre className="code-lines">
              {config.code.map((line, idx) => (
                <div key={idx} className={`code-line ${idx === step.codeLine ? 'active-line' : ''}`}>
                  <span className="line-num">{idx + 1}</span> {line}
                </div>
              ))}
            </pre>
          </div>
        </div>

        {/* Playback Controls */}
        <div className="playback-controls-bar">
          <div className="controls-group">
            <button className="ctrl-btn" onClick={() => { setCurrentStepIndex(0); setIsPlaying(false); }}>
              <RotateCcw size={16} />
            </button>
            <button className="ctrl-btn" onClick={() => setCurrentStepIndex(Math.max(0, currentStepIndex - 1))}>
              <SkipBack size={16} />
            </button>
            <button className="ctrl-btn primary" onClick={() => setIsPlaying(!isPlaying)}>
              {isPlaying ? <Pause size={18} /> : <Play size={18} />}
            </button>
            <button className="ctrl-btn" onClick={() => setCurrentStepIndex(Math.min(config.steps.length - 1, currentStepIndex + 1))}>
              <SkipForward size={16} />
            </button>
          </div>

          <div className="speed-selector">
            <label>Speed:</label>
            <select value={speed} onChange={(e) => setSpeed(Number(e.target.value))}>
              <option value={1500}>0.5x Slow</option>
              <option value={1000}>1x Normal</option>
              <option value={500}>2x Fast</option>
            </select>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ArrayVisualizerModal;
