const fs = require('fs');

let content = fs.readFileSync('frontend/src/pages/Problems.jsx', 'utf8');

// Add import ArrayVisualizerModal and PlayCircle icon
content = content.replace(
  "import { Search, ExternalLink, Code, Layers } from 'lucide-react';",
  "import { Search, ExternalLink, Code, Layers, PlayCircle } from 'lucide-react';\nimport ArrayVisualizerModal from '../components/ArrayVisualizerModal';"
);

// Add visualizingProblem state
content = content.replace(
  "const [selectedProblem, setSelectedProblem] = useState(null);",
  "const [selectedProblem, setSelectedProblem] = useState(null);\n  const [visualizingProblem, setVisualizingProblem] = useState(null);"
);

// Add Visualize action button inside problem card footer
const footerTarget = `<button
                    className="btn-primary"
                    style={{ width: 'auto', padding: '0.4rem 0.75rem', fontSize: '0.85rem' }}
                    onClick={() => setSelectedProblem(problem)}
                  >
                    Log Solution
                  </button>`;

const footerReplacement = `<button
                    className="btn-secondary"
                    style={{ width: 'auto', padding: '0.4rem 0.75rem', fontSize: '0.85rem', display: 'flex', alignItems: 'center', gap: '0.3rem', background: '#334155', color: '#fff', border: 'none', borderRadius: '0.5rem', cursor: 'pointer' }}
                    onClick={() => setVisualizingProblem(problem)}
                  >
                    <PlayCircle size={14} /> Stepper
                  </button>
                  ` + footerTarget;

content = content.replace(footerTarget, footerReplacement);

// Render ArrayVisualizerModal at the bottom
const modalTarget = `{selectedProblem && (
        <LogSubmissionModal
          problem={selectedProblem}
          onClose={() => setSelectedProblem(null)}
          onSuccess={() => alert('Practice attempt logged successfully!')}
        />
      )}`;

const modalReplacement = modalTarget + `\n\n      {visualizingProblem && (
        <ArrayVisualizerModal
          problem={visualizingProblem}
          onClose={() => setVisualizingProblem(null)}
        />
      )}`;

content = content.replace(modalTarget, modalReplacement);

fs.writeFileSync('frontend/src/pages/Problems.jsx', content, 'utf8');
console.log('Updated Problems.jsx with ArrayVisualizerModal trigger');
