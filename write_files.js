const fs = require('fs');

let content = fs.readFileSync('frontend/src/pages/Problems.jsx', 'utf8');

// Insert import PatternSummaryHeader
content = content.replace(
  "import Navbar from '../components/Navbar';",
  "import Navbar from '../components/Navbar';\nimport PatternSummaryHeader from '../components/PatternSummaryHeader';"
);

// Insert <PatternSummaryHeader selectedPattern={selectedPattern} onSelectPattern={setSelectedPattern} /> right after page-header div
const target = `<div className="page-header">
          <div>
            <h1 className="page-title">DSA Problem Repository</h1>
            <p className="page-subtitle">Browse, filter by pattern, and solve curated coding questions for placements</p>
          </div>
        </div>`;

const replacement = target + `\n\n        <PatternSummaryHeader selectedPattern={selectedPattern} onSelectPattern={setSelectedPattern} />`;

content = content.replace(target, replacement);

fs.writeFileSync('frontend/src/pages/Problems.jsx', content, 'utf8');
console.log('Updated Problems.jsx with PatternSummaryHeader');
