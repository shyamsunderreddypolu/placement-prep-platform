import React, { useState, useEffect } from 'react';
import Navbar from '../components/Navbar';
import { getProblems } from '../services/problemService';
import { Search, ExternalLink, Code } from 'lucide-react';

const Problems = () => {
  const [problems, setProblems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedTopic, setSelectedTopic] = useState('');
  const [selectedDifficulty, setSelectedDifficulty] = useState('');
  const [searchQuery, setSearchQuery] = useState('');

  const fetchProblems = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await getProblems(selectedTopic, selectedDifficulty);
      setProblems(data);
    } catch (err) {
      setError('Failed to load problems. Please check your backend connection.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchProblems();
  }, [selectedTopic, selectedDifficulty]);

  const filteredProblems = problems.filter((problem) =>
    problem.title.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const getDifficultyBadgeClass = (diff) => {
    switch (diff) {
      case 'EASY':
        return 'badge-easy';
      case 'MEDIUM':
        return 'badge-medium';
      case 'HARD':
        return 'badge-hard';
      default:
        return '';
    }
  };

  return (
    <>
      <Navbar />
      <div className="page-container">
        <div className="page-header">
          <div>
            <h1 className="page-title">DSA Problem Repository</h1>
            <p className="page-subtitle">Browse, filter, and solve curated coding questions for placements</p>
          </div>
        </div>

        {/* Filters & Search Bar */}
        <div className="filters-bar">
          <div className="search-input-wrapper">
            <Search size={18} className="search-icon" />
            <input
              type="text"
              className="search-input"
              placeholder="Search problems by title..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
          </div>

          <div style={{ display: 'flex', gap: '1rem', alignItems: 'center', flexWrap: 'wrap' }}>
            <select
              className="filter-select"
              value={selectedTopic}
              onChange={(e) => setSelectedTopic(e.target.value)}
            >
              <option value="">All Topics</option>
              <option value="Arrays">Arrays</option>
              <option value="Strings">Strings</option>
              <option value="Trees">Trees</option>
              <option value="Graphs">Graphs</option>
              <option value="Dynamic Programming">Dynamic Programming</option>
              <option value="LinkedList">LinkedList</option>
            </select>

            <div className="difficulty-tabs">
              {['', 'EASY', 'MEDIUM', 'HARD'].map((diff) => (
                <button
                  key={diff}
                  className={`tab-btn ${selectedDifficulty === diff ? 'active' : ''}`}
                  onClick={() => setSelectedDifficulty(diff)}
                >
                  {diff || 'All'}
                </button>
              ))}
            </div>
          </div>
        </div>

        {error && <div className="error-banner">{error}</div>}

        {loading ? (
          <div style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-muted)' }}>
            Loading problems repository...
          </div>
        ) : filteredProblems.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '4rem 2rem', background: 'var(--bg-card)', borderRadius: '0.75rem' }}>
            <Code size={48} style={{ color: 'var(--text-muted)', marginBottom: '1rem' }} />
            <h3>No problems found</h3>
            <p style={{ color: 'var(--text-muted)', marginTop: '0.5rem' }}>
              Try clearing your filters or search query to view available problems.
            </p>
          </div>
        ) : (
          <div className="problems-grid">
            {filteredProblems.map((problem) => (
              <div key={problem.id} className="problem-card">
                <div>
                  <div className="problem-card-header">
                    <h3 className="problem-title">{problem.title}</h3>
                    <span className={`badge ${getDifficultyBadgeClass(problem.difficulty)}`}>
                      {problem.difficulty}
                    </span>
                  </div>
                  <span className="topic-tag">{problem.topic}</span>
                </div>

                <div className="problem-card-footer">
                  <a
                    href={problem.link}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="link-btn"
                  >
                    Solve Challenge <ExternalLink size={14} />
                  </a>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </>
  );
};

export default Problems;
