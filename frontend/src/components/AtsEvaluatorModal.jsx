import React, { useState } from 'react';
import { analyzeResume } from '../services/atsService';
import { X, Sparkles, CheckCircle2, AlertTriangle, Lightbulb } from 'lucide-react';

const AtsEvaluatorModal = ({ resume, onClose }) => {
  const [jobDescription, setJobDescription] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [result, setResult] = useState(null);

  const handleAnalyze = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const data = await analyzeResume(resume.id, jobDescription);
      setResult(data);
    } catch (err) {
      setError('Failed to analyze resume. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const getScoreColor = (score) => {
    if (score >= 80) return '#10b981';
    if (score >= 50) return '#f59e0b';
    return '#ef4444';
  };

  return (
    <div className="modal-overlay">
      <div className="modal-card" style={{ maxWidth: '640px', maxHeight: '90vh', overflowY: 'auto' }}>
        <div className="modal-header">
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Sparkles size={20} style={{ color: 'var(--primary-accent)' }} />
            <h3>AI ATS Resume Analyzer</h3>
          </div>
          <button className="close-btn" onClick={onClose}><X size={18} /></button>
        </div>

        <div className="modal-body">
          <div style={{ marginBottom: '1.25rem' }}>
            <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Target Resume</span>
            <div style={{ fontSize: '1.05rem', fontWeight: 600, marginTop: '0.2rem' }}>{resume.fileName}</div>
          </div>

          {error && <div className="error-banner">{error}</div>}

          {!result ? (
            <form onSubmit={handleAnalyze}>
              <div className="form-group">
                <label htmlFor="jd">Target Placement Role / Job Description Keywords</label>
                <textarea
                  id="jd"
                  className="form-input"
                  style={{ minHeight: '120px', resize: 'vertical' }}
                  placeholder="Paste placement job description text or target technical skills (e.g. Java, Spring Boot, MySQL, React, Microservices)..."
                  value={jobDescription}
                  onChange={(e) => setJobDescription(e.target.value)}
                />
              </div>

              <button type="submit" className="btn-primary" disabled={loading}>
                {loading ? 'Analyzing Keywords & ATS Score...' : <><Sparkles size={16} /> Evaluate ATS Compatibility</>}
              </button>
            </form>
          ) : (
            <div className="ats-results" style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
              {/* Score Meter Banner */}
              <div
                style={{
                  background: 'rgba(15, 23, 42, 0.6)',
                  border: `1px solid ${getScoreColor(result.score)}`,
                  borderRadius: '0.75rem',
                  padding: '1.5rem',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'space-between',
                  gap: '1rem'
                }}
              >
                <div>
                  <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>ATS Placement Readiness Score</div>
                  <div style={{ fontSize: '2.25rem', fontWeight: 800, color: getScoreColor(result.score) }}>
                    {result.score}%
                  </div>
                </div>
                <div style={{ width: '120px', height: '10px', background: 'rgba(255, 255, 255, 0.1)', borderRadius: '5px', overflow: 'hidden' }}>
                  <div style={{ width: `${result.score}%`, height: '100%', background: getScoreColor(result.score), transition: 'width 0.5s ease-out' }}></div>
                </div>
              </div>

              {/* Matched Skills */}
              <div>
                <h4 style={{ fontSize: '0.95rem', fontWeight: 600, marginBottom: '0.5rem', display: 'flex', alignItems: 'center', gap: '0.4rem', color: '#34d399' }}>
                  <CheckCircle2 size={16} /> Matched Skills ({result.matchedSkills.length})
                </h4>
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
                  {result.matchedSkills.map((skill) => (
                    <span key={skill} className="badge badge-easy" style={{ textTransform: 'capitalize' }}>
                      {skill}
                    </span>
                  ))}
                </div>
              </div>

              {/* Missing Skills */}
              {result.missingSkills.length > 0 && (
                <div>
                  <h4 style={{ fontSize: '0.95rem', fontWeight: 600, marginBottom: '0.5rem', display: 'flex', alignItems: 'center', gap: '0.4rem', color: '#f87171' }}>
                    <AlertTriangle size={16} /> Missing Keywords ({result.missingSkills.length})
                  </h4>
                  <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
                    {result.missingSkills.map((skill) => (
                      <span key={skill} className="badge badge-hard" style={{ textTransform: 'capitalize' }}>
                        {skill}
                      </span>
                    ))}
                  </div>
                </div>
              )}

              {/* Recommendations */}
              <div>
                <h4 style={{ fontSize: '0.95rem', fontWeight: 600, marginBottom: '0.5rem', display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                  <Lightbulb size={16} style={{ color: 'var(--warning-color)' }} /> Actionable Recommendations
                </h4>
                <ul style={{ listStyleType: 'disc', paddingLeft: '1.25rem', color: '#cbd5e1', fontSize: '0.9rem', display: 'flex', flexDirection: 'column', gap: '0.4rem' }}>
                  {result.recommendations.map((rec, index) => (
                    <li key={index}>{rec}</li>
                  ))}
                </ul>
              </div>

              <button
                className="btn-primary"
                style={{ width: 'auto', alignSelf: 'flex-end', marginTop: '0.5rem' }}
                onClick={() => setResult(null)}
              >
                Analyze Another Description
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default AtsEvaluatorModal;
