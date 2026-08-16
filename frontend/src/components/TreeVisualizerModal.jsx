import React, { useState, useEffect } from 'react';
import { X, Play, Pause, SkipForward, SkipBack, RotateCcw, Code } from 'lucide-react';

const TREE_ALGORITHM = {
  title: 'Binary Tree Level Order Traversal (BFS)',
  steps: [
    { activeNodes: [1], visited: [1], msg: 'Level 1: Start BFS at Root (node 1). Add node 1 to Queue.', codeLine: 2 },
    { activeNodes: [2, 3], visited: [1, 2, 3], msg: 'Level 2: Pop node 1. Process children -> enqueue left node 2 & right node 3.', codeLine: 4 },
    { activeNodes: [4, 5, 6, 7], visited: [1, 2, 3, 4, 5, 6, 7], msg: 'Level 3: Pop node 2 & 3. Process children -> enqueue [4, 5] and [6, 7]. Traversal Complete!', codeLine: 5 }
  ],
  code: [
    'Queue<TreeNode> queue = new LinkedList<>();',
    'if (root != null) queue.add(root);',
    'while (!queue.isEmpty()) {',
    '    int size = queue.size();',
    '    for (int i = 0; i < size; i++) {',
    '        TreeNode curr = queue.poll();',
    '        if (curr.left != null) queue.add(curr.left);',
    '        if (curr.right != null) queue.add(curr.right);',
    '    }',
    '}'
  ]
};

const TreeVisualizerModal = ({ problem, onClose }) => {
  const [currentStepIndex, setCurrentStepIndex] = useState(0);
  const [isPlaying, setIsPlaying] = useState(false);
  const [speed, setSpeed] = useState(1000);

  const step = TREE_ALGORITHM.steps[currentStepIndex];

  useEffect(() => {
    let timer;
    if (isPlaying) {
      timer = setInterval(() => {
        setCurrentStepIndex((prev) => {
          if (prev < TREE_ALGORITHM.steps.length - 1) return prev + 1;
          setIsPlaying(false);
          return prev;
        });
      }, speed);
    }
    return () => clearInterval(timer);
  }, [isPlaying, speed]);

  return (
    <div className="modal-overlay">
      <div className="modal-card visualizer-modal">
        <div className="modal-header">
          <div>
            <h3 style={{ fontSize: '1.25rem', color: '#fff', margin: 0 }}>Tree Traversal Stepper</h3>
            <span className="pattern-pill" style={{ marginTop: '0.4rem' }}>{TREE_ALGORITHM.title}</span>
          </div>
          <button className="close-btn" onClick={onClose}><X size={20} /></button>
        </div>

        <div className="array-visualizer-container">
          <div className="tree-display-container">
            <div className="tree-level level-1">
              <div className={"tree-node " + (step.activeNodes.includes(1) ? "active" : step.visited.includes(1) ? "visited" : "")}>1</div>
            </div>
            <div className="tree-level level-2">
              <div className={"tree-node " + (step.activeNodes.includes(2) ? "active" : step.visited.includes(2) ? "visited" : "")}>2</div>
              <div className={"tree-node " + (step.activeNodes.includes(3) ? "active" : step.visited.includes(3) ? "visited" : "")}>3</div>
            </div>
            <div className="tree-level level-3">
              <div className={"tree-node " + (step.activeNodes.includes(4) ? "active" : step.visited.includes(4) ? "visited" : "")}>4</div>
              <div className={"tree-node " + (step.activeNodes.includes(5) ? "active" : step.visited.includes(5) ? "visited" : "")}>5</div>
              <div className={"tree-node " + (step.activeNodes.includes(6) ? "active" : step.visited.includes(6) ? "visited" : "")}>6</div>
              <div className={"tree-node " + (step.activeNodes.includes(7) ? "active" : step.visited.includes(7) ? "visited" : "")}>7</div>
            </div>
          </div>

          <div className="step-commentary-box">
            <p className="commentary-text">💡 <strong>Step {currentStepIndex + 1}:</strong> {step.msg}</p>
          </div>

          <div className="code-stepper-panel">
            <div className="code-header"><Code size={16} /> <span>BFS Tree Traversal Code Trace</span></div>
            <pre className="code-lines">
              {TREE_ALGORITHM.code.map((line, idx) => (
                <div key={idx} className={"code-line " + (idx === step.codeLine ? "active-line" : "")}>
                  <span className="line-num">{idx + 1}</span> {line}
                </div>
              ))}
            </pre>
          </div>
        </div>

        <div className="playback-controls-bar">
          <div className="controls-group">
            <button className="ctrl-btn" onClick={() => { setCurrentStepIndex(0); setIsPlaying(false); }}><RotateCcw size={16} /></button>
            <button className="ctrl-btn" onClick={() => setCurrentStepIndex(Math.max(0, currentStepIndex - 1))}><SkipBack size={16} /></button>
            <button className="ctrl-btn primary" onClick={() => setIsPlaying(!isPlaying)}>
              {isPlaying ? <Pause size={18} /> : <Play size={18} />}
            </button>
            <button className="ctrl-btn" onClick={() => setCurrentStepIndex(Math.min(TREE_ALGORITHM.steps.length - 1, currentStepIndex + 1))}><SkipForward size={16} /></button>
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

export default TreeVisualizerModal;