import React, { useState, useEffect } from 'react';
import { X, Play, Pause, SkipForward, SkipBack, RotateCcw, Code } from 'lucide-react';

const STACK_ALGORITHM = {
  title: 'Valid Parentheses Monotonic Stack',
  expression: '{ [ ( ) ] }',
  steps: [
    { char: '{', op: 'PUSH', stack: ['{'], msg: "Read '{': Open bracket. Push '{' onto stack.", codeLine: 2 },
    { char: '[', op: 'PUSH', stack: ['{', '['], msg: "Read '[': Open bracket. Push '[' onto stack.", codeLine: 2 },
    { char: '(', op: 'PUSH', stack: ['{', '[', '('], msg: "Read '(': Open bracket. Push '(' onto stack.", codeLine: 2 },
    { char: ')', op: 'POP', stack: ['{', '['], msg: "Read ')': Close bracket. Top is '(' -> Matching pair! Pop '(' from stack.", codeLine: 4 },
    { char: ']', op: 'POP', stack: ['{'], msg: "Read ']': Close bracket. Top is '[' -> Matching pair! Pop '[' from stack.", codeLine: 4 },
    { char: '}', op: 'POP', stack: [], msg: "Read '}': Close bracket. Top is '{' -> Matching pair! Pop '{' from stack. Stack is empty -> Valid!", codeLine: 6 }
  ],
  code: [
    'Stack<Character> stack = new Stack<>();',
    'for (char c : s.toCharArray()) {',
    '    if (c == "(" || c == "{" || c == "[") stack.push(c);',
    '    else if (!stack.isEmpty() && isMatch(stack.peek(), c)) stack.pop();',
    '    else return false;',
    '}',
    'return stack.isEmpty();'
  ]
};

const StackVisualizerModal = ({ problem, onClose }) => {
  const [currentStepIndex, setCurrentStepIndex] = useState(0);
  const [isPlaying, setIsPlaying] = useState(false);
  const [speed, setSpeed] = useState(1000);

  const step = STACK_ALGORITHM.steps[currentStepIndex];

  useEffect(() => {
    let timer;
    if (isPlaying) {
      timer = setInterval(() => {
        setCurrentStepIndex((prev) => {
          if (prev < STACK_ALGORITHM.steps.length - 1) return prev + 1;
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
            <h3 style={{ fontSize: '1.25rem', color: '#fff', margin: 0 }}>Stack Execution Stepper</h3>
            <span className="pattern-pill" style={{ marginTop: '0.4rem' }}>{STACK_ALGORITHM.title}</span>
          </div>
          <button className="close-btn" onClick={onClose}><X size={20} /></button>
        </div>

        <div className="array-visualizer-container">
          <div className="stack-display-container">
            <div className="stack-tower">
              {step.stack.length === 0 ? (
                <div className="stack-empty-label">Stack Empty (Size 0)</div>
              ) : (
                step.stack.slice().reverse().map((char, idx) => (
                  <div key={idx} className="stack-block">
                    {char} {idx === 0 && <span className="top-badge">TOP</span>}
                  </div>
                ))
              )}
            </div>
            <div className="op-badge-container">
              <span className={"op-badge " + step.op.toLowerCase()}>{step.op} '{step.char}'</span>
            </div>
          </div>

          <div className="step-commentary-box">
            <p className="commentary-text">💡 <strong>Step {currentStepIndex + 1}:</strong> {step.msg}</p>
          </div>

          <div className="code-stepper-panel">
            <div className="code-header"><Code size={16} /> <span>Stack Algorithm Code Trace</span></div>
            <pre className="code-lines">
              {STACK_ALGORITHM.code.map((line, idx) => (
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
            <button className="ctrl-btn" onClick={() => setCurrentStepIndex(Math.min(STACK_ALGORITHM.steps.length - 1, currentStepIndex + 1))}><SkipForward size={16} /></button>
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

export default StackVisualizerModal;